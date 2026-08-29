package com.example.model

import com.example.data.HostGenerator
import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SessionItem(
    val name: String,
    val startTime: String,
    val endTime: String,
    val defaultHost: String = "Available"
)

data class RosarySchedule(
    val morning1Start: String = "07:00", val morning1End: String = "07:30",
    val morning2Start: String = "08:00", val morning2End: String = "08:30",
    val morning3Start: String = "09:00", val morning3End: String = "09:30",
    val evening1Start: String = "19:00", val evening1End: String = "19:30",
    val evening2Start: String = "20:00", val evening2End: String = "20:30",
    val evening3Start: String = "21:00", val evening3End: String = "21:30",
    val broadcastMessage: String = "Welcome to YeshuVerse Live Rosary",
    val showPrayingCount: Boolean = true,
    val basePrayingCount: Int = 384,
    val minPrayingCount: Int = 10,
    val maxPrayingCount: Int = 150,
    val slotClaims: Map<String, String> = emptyMap(),
    val claimsDate: String = ""
) {
    @Exclude
    fun getAllSessions(): List<SessionItem> {
        return listOf(
            SessionItem("Morning Rosary 1", morning1Start, morning1End, HostGenerator.getHostForSession("Morning Rosary 1")),
            SessionItem("Morning Rosary 2", morning2Start, morning2End, HostGenerator.getHostForSession("Morning Rosary 2")),
            SessionItem("Morning Rosary 3", morning3Start, morning3End, HostGenerator.getHostForSession("Morning Rosary 3")),
            SessionItem("Evening Rosary 1", evening1Start, evening1End, HostGenerator.getHostForSession("Evening Rosary 1")),
            SessionItem("Evening Rosary 2", evening2Start, evening2End, HostGenerator.getHostForSession("Evening Rosary 2")),
            SessionItem("Evening Rosary 3", evening3Start, evening3End, HostGenerator.getHostForSession("Evening Rosary 3"))
        )
    }

    @Exclude
    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time)
    }

    @Exclude
    fun isSessionEndedToday(sessionName: String): Boolean {
        val session = getAllSessions().find { it.name == sessionName } ?: return false
        if (session.endTime.isBlank()) return false
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMin = parseTimeToMinutes(session.startTime)
        val endMin = parseTimeToMinutes(session.endTime)

        return if (endMin >= startMin) {
            currentMinutes >= endMin
        } else {
            false
        }
    }

    @Exclude
    fun isSlotClaimed(sessionName: String): Boolean {
        val today = getTodayDateString()
        if (claimsDate.isNotBlank() && claimsDate != today) {
            return false
        }
        if (isSessionEndedToday(sessionName)) {
            return false
        }
        val claimed = slotClaims[sessionName]
        return !claimed.isNullOrBlank()
    }

    @Exclude
    fun getEffectiveHostForSession(sessionName: String): String {
        if (isSlotClaimed(sessionName)) {
            val claimed = slotClaims[sessionName]
            if (!claimed.isNullOrBlank()) return claimed
        }
        return HostGenerator.getHostForSession(sessionName)
    }

    @Exclude
    fun getSanitizedSchedule(): RosarySchedule {
        val today = getTodayDateString()
        if (claimsDate.isNotBlank() && claimsDate != today) {
            return copy(slotClaims = emptyMap(), claimsDate = today)
        }
        val validClaims = slotClaims.filterKeys { sessionName ->
            !isSessionEndedToday(sessionName)
        }
        return copy(slotClaims = validClaims, claimsDate = today)
    }

    @Exclude
    fun getCurrentActiveSession(): SessionItem? {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        for (session in getAllSessions()) {
            if (session.startTime.isBlank() || session.endTime.isBlank()) continue
            val startMin = parseTimeToMinutes(session.startTime)
            val endMin = parseTimeToMinutes(session.endTime)

            if (endMin < startMin) {
                // Session spans midnight (e.g., 23:30 to 00:30)
                if (currentMinutes >= startMin || currentMinutes <= endMin) {
                    return session
                }
            } else {
                if (currentMinutes in startMin..endMin) {
                    return session
                }
            }
        }

        return null
    }

    @Exclude
    fun getElapsedSecondsForActiveSession(): Int? {
        val active = getCurrentActiveSession() ?: return null
        val startMin = parseTimeToMinutes(active.startTime)
        val startSec = startMin * 60

        val now = Calendar.getInstance()
        val currentSec = now.get(Calendar.HOUR_OF_DAY) * 3600 +
                now.get(Calendar.MINUTE) * 60 +
                now.get(Calendar.SECOND)

        var diff = currentSec - startSec
        if (diff < 0) {
            // Spanned across midnight
            diff += 86400
        }
        return diff.coerceAtLeast(0)
    }

    @Exclude
    fun getNextUpcomingSession(): SessionItem? {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val sortedSessions = getAllSessions().filter {
            it.startTime.isNotBlank() && it.endTime.isNotBlank()
        }.sortedBy { parseTimeToMinutes(it.startTime) }

        if (sortedSessions.isEmpty()) return null

        // Find session later today
        for (session in sortedSessions) {
            val startMin = parseTimeToMinutes(session.startTime)
            if (startMin > currentMinutes) {
                return session
            }
        }
        // If all passed today, return the first session tomorrow
        return sortedSessions.firstOrNull()
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        return try {
            val clean = timeStr.trim().uppercase(Locale.ROOT)
            if (clean.isBlank()) return 0
            
            val isPm = clean.contains("PM")
            val isAm = clean.contains("AM")
            val digitsOnly = clean.replace("AM", "").replace("PM", "").trim()
            val parts = digitsOnly.split(":")
            var hours = parts[0].trim().toInt()
            val mins = if (parts.size > 1) parts[1].trim().toInt() else 0

            if (isPm && hours < 12) {
                hours += 12
            } else if (isAm && hours == 12) {
                hours = 0
            }
            hours * 60 + mins
        } catch (e: Exception) {
            0
        }
    }
}

