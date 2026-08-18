package com.example.data

import android.util.Log
import com.example.model.LanguageEnum
import com.example.model.LiveRoomState
import com.example.model.MysteryType
import com.example.model.RosarySchedule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseSyncRepository {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Firebase Firestore not initialized: ${e.message}")
            null
        }
    }

    companion object {
        private val fallbackRoomState = MutableStateFlow(
            LiveRoomState(
                roomId = "global_rosary_room",
                roomName = "YeshuVerse Global Live Rosary",
                hostName = "Available",
                isLive = true,
                currentMysteryType = RosaryPrayers.getTodayDefaultMystery(),
                currentStepIndex = 0,
                isPlaying = false,
                participantCount = 284,
                language = LanguageEnum.HINDI,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        )

        private val fallbackSchedule = MutableStateFlow(RosarySchedule())
    }

    fun observeLiveRoom(roomId: String = "global_rosary_room"): Flow<LiveRoomState> = callbackFlow {
        val db = firestore
        if (db == null) {
            val job = kotlinx.coroutines.MainScope().launch {
                fallbackRoomState.collect { state ->
                    trySend(state)
                }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            val docRef = db.collection("rosary_live_rooms").document(roomId)
            listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseSync", "Listen failed: ${error.message}")
                    trySend(fallbackRoomState.value)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val mysteryName = snapshot.getString("mysteryType") ?: MysteryType.JOYFUL.name
                    val mysteryType = try {
                        MysteryType.valueOf(mysteryName)
                    } catch (e: Exception) {
                        MysteryType.JOYFUL
                    }

                    val langName = snapshot.getString("language") ?: LanguageEnum.HINDI.name
                    val language = try {
                        LanguageEnum.valueOf(langName)
                    } catch (e: Exception) {
                        LanguageEnum.HINDI
                    }

                    val state = LiveRoomState(
                        roomId = roomId,
                        roomName = snapshot.getString("roomName") ?: "YeshuVerse Live Rosary",
                        hostName = snapshot.getString("hostName")?.takeIf { it.isNotBlank() } ?: "Available",
                        isLive = snapshot.getBoolean("isLive") ?: true,
                        currentMysteryType = mysteryType,
                        currentStepIndex = (snapshot.getLong("currentStepIndex") ?: 0L).toInt(),
                        isPlaying = snapshot.getBoolean("isPlaying") ?: false,
                        participantCount = (snapshot.getLong("participantCount") ?: 284L).toInt(),
                        language = language,
                        lastUpdatedTimestamp = snapshot.getLong("lastUpdatedTimestamp") ?: System.currentTimeMillis()
                    )
                    trySend(state)
                } else {
                    updateLiveRoomState(fallbackRoomState.value)
                    trySend(fallbackRoomState.value)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error setting up listener: ${e.message}")
            trySend(fallbackRoomState.value)
        }

        awaitClose {
            listener?.remove()
        }
    }

    fun getSchedule(): Flow<RosarySchedule?> = callbackFlow {
        val db = firestore
        if (db == null) {
            val job = kotlinx.coroutines.MainScope().launch {
                fallbackSchedule.collect { sched ->
                    trySend(sched)
                }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("settings").document("rosary_schedule")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(fallbackSchedule.value)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val s = snapshot.toObject(RosarySchedule::class.java)
                            if (s != null) {
                                fallbackSchedule.value = s
                                trySend(s)
                            } else {
                                trySend(fallbackSchedule.value)
                            }
                        } catch (e: Exception) {
                            trySend(fallbackSchedule.value)
                        }
                    } else {
                        trySend(fallbackSchedule.value)
                    }
                }
        } catch (e: Exception) {
            trySend(fallbackSchedule.value)
        }

        awaitClose { listener?.remove() }
    }

    fun saveSchedule(schedule: RosarySchedule) {
        fallbackSchedule.value = schedule
        try {
            firestore?.collection("settings")?.document("rosary_schedule")?.set(schedule)
                ?.addOnFailureListener { e ->
                    Log.e("FirebaseSync", "Failed to save schedule to Firestore: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error saving schedule: ${e.message}", e)
        }
    }

    fun updateLiveRoomState(newState: LiveRoomState) {
        fallbackRoomState.value = newState

        val db = firestore ?: return
        try {
            val map = mapOf(
                "roomId" to newState.roomId,
                "roomName" to newState.roomName,
                "hostName" to newState.hostName,
                "isLive" to newState.isLive,
                "mysteryType" to newState.currentMysteryType.name,
                "currentStepIndex" to newState.currentStepIndex,
                "isPlaying" to newState.isPlaying,
                "participantCount" to newState.participantCount,
                "language" to newState.language.name,
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )
            db.collection("rosary_live_rooms").document(newState.roomId).set(map)
                .addOnSuccessListener {
                    Log.d("FirebaseSync", "Successfully synced live room state to Firestore: ${newState.roomId}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseSync", "Failed to write to Firestore: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Failed to write to Firestore: ${e.message}")
        }
    }
}
