package com.example.data

import java.util.Calendar

object HostGenerator {
    private val hostNames = listOf(
        "Father John", "Sister Maria", "Rahul D'Souza", "Neha Thomas", "Kiran Paul",
        "Father Thomas", "Sister Elizabeth", "Priya Joseph", "Vikas Anthony", "Sneha John",
        "Pooja Matthew", "Sister Angela", "Amit Masih", "Father Paul", "Deepak David",
        "Anjali Peter", "Sanjay Christian", "Kavita Samuel", "Anil George", "Sunita James",
        "Father Bartholomew", "Manoj Stephen", "Anita Luke", "Suresh Mark", "Ramesh Simon",
        "Reena Philip", "Rajesh Andrews", "Meena Bartholomew", "Dinesh Thomas", "Geeta Matthew",
        "Prakash James", "Asha John", "Vijay Peter", "Ritu Paul", "Ajay Simon", "Sonu Joseph"
    )

    fun getTodayHost(): String {
        return getHostForSession("Morning Rosary 1")
    }

    fun getHostForSession(sessionName: String): String {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        val sessionIndex = when (sessionName) {
            "Morning Rosary 1" -> 1
            "Morning Rosary 2" -> 2
            "Morning Rosary 3" -> 3
            "Evening Rosary 1" -> 4
            "Evening Rosary 2" -> 5
            "Evening Rosary 3" -> 6
            else -> kotlin.math.abs(sessionName.hashCode()) % 20 + 1
        }

        val combinedSeed = dayOfYear * 11 + sessionIndex * 7 + year * 3
        val index = kotlin.math.abs(combinedSeed) % hostNames.size
        return hostNames[index]
    }
}

