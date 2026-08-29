package com.example.model
import com.example.data.HostGenerator

enum class LanguageEnum(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिंदी"),
    MALAYALAM("ml", "Malayalam", "മലയാളം")
}

enum class MysteryType(val englishTitle: String, val hindiTitle: String, val malayalamTitle: String, val defaultDays: String) {
    JOYFUL("Joyful Mysteries", "आनंद के भेद", "സന്തോഷത്തിന്റെ രഹസ്യങ്ങൾ", "Monday & Saturday"),
    SORROWFUL("Sorrowful Mysteries", "दुःख के भेद", "ദുഃഖത്തിന്റെ രഹസ്യങ്ങൾ", "Tuesday & Friday"),
    GLORIOUS("Glorious Mysteries", "महिमा के भेद", "മഹിമയുടെ രഹസ്യങ്ങൾ", "Wednesday & Sunday"),
    LUMINOUS("Luminous Mysteries", "प्रकाश के भेद", "പ്രകാശത്തിന്റെ രഹസ്യങ്ങൾ", "Thursday")
}

enum class PrayerType(
    val englishName: String,
    val hindiName: String,
    val malayalamName: String,
    val durationSeconds: Int
) {
    SIGN_OF_THE_CROSS("Sign of the Cross", "क्रूस का चिन्ह", "കുരിശടയാളം", 12),
    APOSTLES_CREED("Apostles' Creed", "प्रेरितों का धर्मसार", "വിശ്വാസപ്രമാണം", 55),
    OUR_FATHER("Our Father", "हे हमारे पिता", "സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ", 35),
    HAIL_MARY("Hail Mary", "प्रणाम मरीया", "നന്മ നിറഞ്ഞ മറിയമേ", 24),
    GLORY_BE("Glory Be", "पिता और पुत्र...", "ത്രിത്വസ്തുതി", 16),
    FATIMA_PRAYER("Fatima Prayer", "ओ मेरे येसु...", "ഓ എന്റെ ഈശോയേ...", 22),
    HAIL_HOLY_QUEEN("Hail Holy Queen", "हे पवित्र रानी", "പരിശുദ്ധ രാജ്ഞീ", 55),
    MEMORARE("The Memorare", "हे अति दयालु कुंवारी मरियम", "എത്രയും ദയയുള്ള മാതാവേ", 45),
    LITANY_OF_LORETO("Litany of Loreto", "माता मरियम की लितनियाँ", "മാതാവിന്റെ ലുത്തിനിയ", 60),
    CONCLUDING_PRAYER("Concluding Prayer", "समापन प्रार्थना", "സമാപന പ്രാർത്ഥന", 35),
    INTRO_PRAYER("Introduction", "प्रार्थना की शुरुआत", "പ്രാരംഭ പ്രാർത്ഥന", 15)
}

data class RosaryMystery(
    val type: MysteryType,
    val index: Int, // 1 to 5
    val englishTitle: String,
    val hindiTitle: String,
    val malayalamTitle: String,
    val englishMeditation: String,
    val hindiMeditation: String,
    val malayalamMeditation: String,
    val englishFruit: String,
    val hindiFruit: String,
    val malayalamFruit: String
)

data class RosaryBeadStep(
    val stepIndex: Int, // 0 to 59
    val decadeIndex: Int, // 0 for intro/outro, 1..5 for decades
    val beadInDecade: Int, // 1..10 for Hail Marys, 0 for Our Father/Intro
    val prayerType: PrayerType,
    val mysteryIndex: Int? = null, // 1..5 if associated with a mystery
    val isLargeBead: Boolean = false,
    val labelEnglish: String,
    val labelHindi: String,
    val labelMalayalam: String
)


data class LiveRoomState(
    val roomId: String = "global_rosary_room",
    val roomName: String = "YeshuVerse Global Live Rosary",
    val hostName: String = "Available",
    val isLive: Boolean = true,
    val currentMysteryType: MysteryType = MysteryType.JOYFUL,
    val currentStepIndex: Int = 0,
    val isPlaying: Boolean = false,
    val participantCount: Int = 184,
    val language: LanguageEnum = LanguageEnum.HINDI,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
