package com.example.viewmodel

import com.example.model.LanguageEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

data class PrayerComment(
    val id: String = UUID.randomUUID().toString(),
    val userName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

object LiveChatSimulator {
    private val _comments = MutableStateFlow<List<PrayerComment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val indianNames = listOf(
        "Aarav", "Aanya", "Vivaan", "Diya", "Aditya", "Ishita", "Vihaan", "Ananya", "Arjun", "Myra",
        "Sai", "Prisha", "Reyansh", "Riya", "Ayaan", "Aarohi", "Krishna", "Kriti", "Ishaan", "Saanvi",
        "Shaurya", "Navya", "Atharva", "Kavya", "Dhruv", "Avni", "Kabir", "Meera", "Rudra", "Nisha",
        "Kian", "Ira", "Darsh", "Sara", "Dev", "Aditi", "Veer", "Neha", "Neev", "Pooja",
        "Sneha", "Karan", "Kirti", "Arnav", "Shruti", "Ansh", "Tara", "Yuvan", "Roshni",
        "Tanya", "Laksh", "Aisha", "Rishabh", "Ridhi", "Samar", "Simran", "Aaditya", "Tiya",
        "Yash", "Kyra", "Yug", "Sia", "Ayush", "Mahi", "Jay", "Naina", "Rahul", "Vani",
        "Amit", "Anjali", "Rohan", "Priya", "Vikram", "Ritu", "Suraj", "Geeta", "Mohit", "Kiran",
        "Deepak", "Aarti", "Sunil", "Anita", "Sanjay", "Suman", "Rajesh", "Poonam", "Manish", "Komal",
        "Mary", "Joseph", "Peter", "Paul", "John", "Thomas", "Antony", "Francis", "George", "David",
        "Maria", "Elizabeth", "Theresa", "Anna", "Rose", "Agnes", "Catherine", "Margaret", "Helen", "Lucy",
        "Mathew", "Kurian", "Chacko", "Varghese", "Mariamma", "Annamma", "Reena", "Biju", "Jomon", "Alphy",
        "Sojan", "Nimmy", "Rony", "Tessa", "Jiby", "Anu", "Ancy", "Joby", "Shinto", "Sneha John"
    )

    private val messagesMalayalam = listOf(
        "ആമേൻ",
        "ആമേൻ 🙏",
        "ദൈവത്തിന് സ്തുതി",
        "യേശുവേ നന്ദി",
        "കർത്താവേ ഞങ്ങളുടെ പ്രാർത്ഥന കേൾക്കണമേ",
        "ഹല്ലേലൂയ്യ",
        "കർത്താവേ ഞങ്ങളോട് കരുണയായിരിക്കണമേ",
        "എന്റെ കുടുംബത്തിന് വേണ്ടി പ്രാർത്ഥിക്കണമേ",
        "ആമേൻ ആമേൻ",
        "ദൈവം വലിയവനാണ്",
        "പരിശുദ്ധ കന്യകാമറിയമേ, ഞങ്ങൾക്കായി പ്രാർത്ഥിക്കണമേ",
        "എന്റെ അമ്മയുടെ സൗഖ്യത്തിനായി പ്രാർത്ഥിക്കണമേ",
        "🙏 ആമേൻ",
        "ഈശോയെ ഞാൻ അങ്ങയിൽ ശരണപ്പെടുന്നു",
        "എല്ലാവർക്കും സമാധാനം ലഭിക്കട്ടെ",
        "സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ",
        "നന്മ നിറഞ്ഞ മറിയമേ ഞങ്ങൾക്കായി പ്രാർത്ഥിക്കണമേ",
        "Praise the Lord 🙏",
        "Amen 🙏",
        "Thank you Jesus"
    )

    private val messagesHindi = listOf(
        "आमीन",
        "आमीन 🙏",
        "प्रभु की स्तुति हो",
        "धन्यवाद यीशु",
        "प्रभु हमारी प्रार्थना सुन",
        "हालेलूयाह",
        "हे प्रभु, हम पर दया कर",
        "मेरी माँ के स्वास्थ्य के लिए प्रार्थना करें",
        "आमीन आमीन",
        "प्रभु महान है",
        "स्वर्ग की रानी, हमारे लिए प्रार्थना कर",
        "मेरे परिवार को आशीष दे प्रभु",
        "🙏 आमीन",
        "Praise the Lord",
        "Amen 🙏",
        "Yeshu ki jay",
        "प्रभु यीशु की जय",
        "मेरे बच्चों के भविष्य के लिए प्रार्थना करें",
        "शांति और प्रेम के लिए प्रार्थना"
    )

    private val messagesEnglish = listOf(
        "Amen",
        "Amen 🙏",
        "Praise the Lord",
        "Thank you Jesus",
        "Lord hear our prayer",
        "Hallelujah",
        "Lord have mercy on us",
        "Please pray for my mother's health",
        "Amen Amen",
        "God is great",
        "Holy Mary, pray for us",
        "Bless my family Lord",
        "🙏 Amen",
        "Praying with you all",
        "Jesus I trust in you",
        "Glory to God",
        "Pray for world peace",
        "Hail Mary, full of grace"
    )

    fun getDailyActiveNames(): List<String> {
        val todaySeed = LocalDate.now().toEpochDay()
        val random = Random(todaySeed)
        val shuffledNames = indianNames.shuffled(random)
        return shuffledNames.take(60) // 60 active names per day
    }

    fun addComment(userName: String = "You", message: String) {
        if (message.isBlank()) return
        val newComment = PrayerComment(userName = userName, message = message.trim())
        _comments.value = (_comments.value + newComment).takeLast(40)
    }

    suspend fun startSimulation(
        languageFlow: kotlinx.coroutines.flow.StateFlow<LanguageEnum>,
        participantCountFlow: kotlinx.coroutines.flow.StateFlow<Int>? = null
    ) {
        val activeNames = getDailyActiveNames()
        while (true) {
            val count = participantCountFlow?.value ?: 0
            if (count <= 0) {
                delay(3000L)
                continue
            }
            
            // Calculate delay based on active praying count for natural, realistic chat flow
            val delayMs = when {
                count <= 15 -> Random.nextLong(18000, 32000) // 18-32s delay
                count <= 40 -> Random.nextLong(12000, 22000) // 12-22s delay
                count <= 100 -> Random.nextLong(8000, 16000)  // 8-16s delay
                else -> Random.nextLong(5000, 10000)         // 5-10s delay
            }
            delay(delayMs)

            val lang = languageFlow.value
            
            // Ensure the name pool NEVER exceeds the active praying count!
            val availableNamesCount = count.coerceAtLeast(1).coerceAtMost(activeNames.size)
            val namePool = activeNames.take(availableNamesCount)
            val name = if (namePool.isNotEmpty()) namePool.random() else "Devotee"
            
            // Comment ratio calculation:
            // When language is MALAYALAM -> 4 Malayalam : 2 English (approx 67% Malayalam, 33% English)
            // When language is HINDI -> 4 Hindi : 2 English (approx 67% Hindi, 33% English)
            // When language is ENGLISH -> 100% English
            val finalMessage = when (lang) {
                LanguageEnum.MALAYALAM -> {
                    val roll = Random.nextInt(6) // 0, 1, 2, 3 -> Malayalam (4), 4, 5 -> English (2)
                    if (roll < 4) {
                        messagesMalayalam.random()
                    } else {
                        messagesEnglish.random()
                    }
                }
                LanguageEnum.HINDI -> {
                    val roll = Random.nextInt(6) // 0, 1, 2, 3 -> Hindi (4), 4, 5 -> English (2)
                    if (roll < 4) {
                        messagesHindi.random()
                    } else {
                        messagesEnglish.random()
                    }
                }
                LanguageEnum.ENGLISH -> {
                    messagesEnglish.random()
                }
            }

            val newComment = PrayerComment(userName = name, message = finalMessage)
            
            _comments.value = (_comments.value + newComment).takeLast(25) // Keep last 25 comments
        }
    }
}
