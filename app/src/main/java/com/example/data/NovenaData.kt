package com.example.data

import com.example.model.LanguageEnum

data class NovenaDay(
    val dayNumber: Int,
    val hindiTitle: String,
    val englishTitle: String,
    val hindiPrayer: String,
    val englishPrayer: String,
    val malayalamPrayer: String,
    val malayalamTitle: String
)

object NovenaData {
    val novenaTitleHindi = "शांति और आशा की नौ-दिवसीय प्रार्थना"
    val novenaSubtitleHindi = "Original Novena"
    val novenaTitleEnglish = "9-Day Novena of Peace and Hope"
    val novenaTitleMalayalam = "സമാധാനത്തിന്റെയും പ്രത്യാശയുടെയും 9 ദിവസത്തെ നൊവേന"

    val signOfCrossHindi = "पिता, पुत्र और पवित्र आत्मा के नाम पर। आमेन।"
    val signOfCrossEnglish = "In the name of the Father, and of the Son, and of the Holy Spirit. Amen."
    val signOfCrossMalayalam = "പിതാവിന്റെയും പുത്രന്റെയും പരിശുദ്ധാത്മാവിന്റെയും നാമത്തിൽ. ആമ്മേൻ."

    val openingPrayerHindi = """
        हे सर्वशक्तिमान परमेश्वर,
        मैं विनम्र हृदय से आपके सामने आता हूँ। प्रभु यीशु मसीह के द्वारा मुझे अपनी कृपा प्रदान कीजिए। मेरे मन को शांति, मेरे हृदय को विश्वास और मेरे जीवन को आपकी पवित्र इच्छा के अनुसार चलने की शक्ति दीजिए। पवित्र आत्मा मुझे सत्य, प्रेम और धैर्य के मार्ग पर ले चले।

        आमेन।
    """.trimIndent()

    val openingPrayerEnglish = """
        Almighty God,
        I come before You with a humble heart. Grant me Your grace through Lord Jesus Christ. Give peace to my mind, faith to my heart, and strength to live according to Your holy will. May the Holy Spirit guide me on the path of truth, love, and patience.

        Amen.
    """.trimIndent()
    val openingPrayerMalayalam = """
        സർവ്വശക്തനായ ദൈവമേ,
        വിനീതമായ ഹൃദയത്തോടെ ഞാൻ അങ്ങയുടെ സന്നിധിയിൽ വരുന്നു. കർത്താവായ യേശുക്രിസ്തുവിലൂടെ അങ്ങയുടെ കൃപ എനിക്ക് നൽകണമേ. എന്റെ മനസ്സിന് സമാധാനവും, ഹൃദയത്തിന് വിശ്വാസവും, അങ്ങയുടെ തിരുഹിതമനുസരിച്ച് ജീവിക്കാൻ ശക്തിയും നൽകണമേ. സത്യത്തിന്റെയും സ്നേഹത്തിന്റെയും ക്ഷമയുടെയും പാതയിലൂടെ പരിശുദ്ധാത്മാവ് എന്നെ നയിക്കട്ടെ.
        ആമ്മേൻ.
    """.trimIndent()

    val specialIntentionHindi = "अपनी विशेष मनोकामना प्रस्तुत करें।"
    val specialIntentionEnglish = "Present your personal intention to the Lord."
    val specialIntentionMalayalam = "നിങ്ങളുടെ പ്രത്യേക നിയോഗം നിശബ്ദമായി ഈശോയോട് അപേക്ഷിക്കുക."

    val dailyCorePrayersHindi = listOf(
        "हे हमारे पिता",
        "प्रणाम मरियम",
        "महिमा हो"
    )

    val dailyCorePrayersEnglish = listOf(
        "Our Father",
        "Hail Mary",
        "Glory Be"
    )

    val concludingPrayerHindi = """
        हे स्वर्गीय पिता,
        इन नौ दिनों की प्रार्थना में मेरे साथ रहने के लिए आपका धन्यवाद। मुझे ऐसा जीवन जीने की कृपा दीजिए जो आपके प्रेम, दया और सत्य का साक्षी बने। मेरी प्रार्थनाओं को अपनी पवित्र इच्छा के अनुसार स्वीकार कीजिए और मुझे प्रतिदिन आपके और निकट आने का अनुग्रह दीजिए।

        हे धन्य कुँवारी मरियम,
        हमारे लिए प्रार्थना कीजिए।

        पिता, पुत्र और पवित्र आत्मा के नाम पर।
        आमेन।
    """.trimIndent()

    val concludingPrayerMalayalam = """
        സ്വർഗ്ഗസ്ഥനായ പിതാവേ,
        ഈ ഒൻപതു ദിവസത്തെ പ്രാർത്ഥനയിൽ ഞങ്ങളോടൊപ്പം ആയിരുന്നതിന് നന്ദി. അങ്ങയുടെ സ്നേഹത്തിനും കരുണയ്ക്കും സാക്ഷ്യം വഹിക്കുന്ന ഒരു ജീവിതം നയിക്കാൻ ഞങ്ങളെ അനുഗ്രഹിക്കണമേ.
        പരിശുദ്ധ കന്യാമറിയമേ,
        ഞങ്ങൾക്കുവേണ്ടി അപേക്ഷിക്കണമേ.
        പിതാവിന്റെയും പുത്രന്റെയും പരിശുദ്ധാത്മാവിന്റെയും നാമത്തിൽ.
        ആമ്മേൻ.
    """.trimIndent()
    val concludingPrayerEnglish = """
        O Heavenly Father,
        Thank You for being with me during these nine days of prayer. Grant me the grace to live a life that witnesses to Your love, mercy, and truth. Accept my prayers according to Your holy will and draw me closer to You each day.

        O Blessed Virgin Mary,
        Pray for us.

        In the name of the Father, and of the Son, and of the Holy Spirit.
        Amen.
    """.trimIndent()

    val days = listOf(
        NovenaDay(
            dayNumber = 1,
            hindiTitle = "पहला दिन – विश्वास",
            englishTitle = "Day 1 – Faith",
            hindiPrayer = """
                हे प्रभु यीशु,
                मेरे विश्वास को दृढ़ कीजिए। जब मैं भयभीत होऊँ, तब मुझे याद दिलाइए कि आप सदैव मेरे साथ हैं।
            """.trimIndent(),
            englishPrayer = """
                O Lord Jesus,
                Strengthen my faith. When I am afraid, remind me that You are always with me.
            """.trimIndent(),
            malayalamTitle = "ഒന്നാം ദിവസം – വിശ്വാസം",
            malayalamPrayer = """
                കർത്താവായ ഈശോയേ,
                എന്റെ വിശ്വാസം വർദ്ധിപ്പിക്കണമേ. ഞാൻ ഭയപ്പെടുമ്പോൾ, അങ്ങ് എപ്പോഴും എന്റെ കൂടെയുണ്ടെന്ന് എന്നെ ഓർമ്മിപ്പിക്കണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 2,
            hindiTitle = "दूसरा दिन – आशा",
            englishTitle = "Day 2 – Hope",
            hindiPrayer = """
                हे प्रभु,
                जब मैं निराश हो जाऊँ, तब मेरे जीवन में आशा का प्रकाश भर दीजिए। मुझे आपके वचनों पर भरोसा रखने की कृपा दीजिए।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                When I feel hopeless, fill my life with the light of hope. Grant me the grace to trust in Your words.
            """.trimIndent(),
            malayalamTitle = "രണ്ടാം ദിവസം – പ്രത്യാശ",
            malayalamPrayer = """
                കർത്താവേ,
                ഞാൻ നിരാശനാകുമ്പോൾ എന്റെ ജീവിതത്തിൽ പ്രത്യാശയുടെ പ്രകാശം നിറയ്ക്കണമേ. അങ്ങയുടെ വചനങ്ങളിൽ ആശ്രയിക്കാൻ എന്നെ സഹായിക്കണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 3,
            hindiTitle = "तीसरा दिन – प्रेम",
            englishTitle = "Day 3 – Love",
            hindiPrayer = """
                हे प्रभु,
                मुझे ऐसा हृदय दीजिए जो सबसे प्रेम करे, सबसे भलाई करे और किसी से बैर न रखे।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                Give me a heart that loves everyone, does good to all, and harbors no enmity.
            """.trimIndent(),
            malayalamTitle = "മൂന്നാം ദിവസം – സ്നേഹം",
            malayalamPrayer = """
                കർത്താവേ,
                എല്ലാവരെയും സ്നേഹിക്കുന്ന, ആരുടേയും നേരെ വൈരാഗ്യം വെച്ചുപുലർത്താത്ത ഒരു ഹൃദയം എനിക്ക് നൽകണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 4,
            hindiTitle = "चौथा दिन – क्षमा",
            englishTitle = "Day 4 – Forgiveness",
            hindiPrayer = """
                हे दयालु प्रभु,
                जिन लोगों ने मुझे दुःख पहुँचाया है, उन्हें क्षमा करने की शक्ति दीजिए। मेरे हृदय से क्रोध और कटुता दूर कर दीजिए।
            """.trimIndent(),
            englishPrayer = """
                O merciful Lord,
                Grant me the strength to forgive those who have hurt me. Remove anger and bitterness from my heart.
            """.trimIndent(),
            malayalamTitle = "നാലാം ദിവസം – ക്ഷമ",
            malayalamPrayer = """
                കരുണാമയനായ കർത്താവേ,
                എന്നെ വേദനിപ്പിച്ചവരോട് ക്ഷമിക്കാനുള്ള ശക്തി എനിക്ക് നൽകണമേ. എന്റെ ഹൃദയത്തിൽ നിന്ന് കോപവും വിദ്വേഷവും നീക്കിക്കളയണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 5,
            hindiTitle = "पाँचवाँ दिन – परिवार",
            englishTitle = "Day 5 – Family",
            hindiPrayer = """
                हे प्रभु,
                मेरे परिवार को प्रेम, एकता और शांति का आशीर्वाद दीजिए। हमारे घर को आपकी उपस्थिति से भर दीजिए।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                Bless my family with love, unity, and peace. Fill our home with Your presence.
            """.trimIndent(),
            malayalamTitle = "അഞ്ചാം ദിവസം – കുടുംബം",
            malayalamPrayer = """
                കർത്താവേ,
                സ്നേഹവും സമാധാനവും നൽകി എന്റെ കുടുംബത്തെ അനുഗ്രഹിക്കണമേ. അങ്ങയുടെ സാന്നിധ്യം കൊണ്ട് ഞങ്ങളുടെ ഭവനം നിറയ്ക്കണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 6,
            hindiTitle = "छठा दिन – साहस",
            englishTitle = "Day 6 – Courage",
            hindiPrayer = """
                हे प्रभु,
                मुझे सत्य के मार्ग पर चलने का साहस दीजिए। कठिन समय में भी मेरा विश्वास अडिग बना रहे।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                Grant me the courage to walk on the path of truth. May my faith remain steadfast even in difficult times.
            """.trimIndent(),
            malayalamTitle = "ആറാം ദിവസം – ധൈര്യം",
            malayalamPrayer = """
                കർത്താവേ,
                സത്യത്തിന്റെ വഴിയിലൂടെ നടക്കാൻ എനിക്ക് ധൈര്യം നൽകണമേ. കഷ്ടപ്പാടുകളുടെ സമയത്തും എന്റെ വിശ്വാസം ദൃഢമായിരിക്കട്ടെ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 7,
            hindiTitle = "सातवाँ दिन – सेवा",
            englishTitle = "Day 7 – Service",
            hindiPrayer = """
                हे प्रभु,
                मुझे विनम्र सेवक बनाइए ताकि मैं अपने शब्दों और कार्यों से आपके प्रेम का साक्षी बन सकूँ।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                Make me a humble servant so that through my words and actions I may bear witness to Your love.
            """.trimIndent(),
            malayalamTitle = "ഏഴാം ദിവസം – സേവനം",
            malayalamPrayer = """
                കർത്താവേ,
                എന്റെ വാക്കുകളിലൂടെയും പ്രവൃത്തികളിലൂടെയും അങ്ങയുടെ സ്നേഹത്തിന് സാക്ഷ്യം വഹിക്കുവാൻ എന്നെ ഒരു വിനീത ദാസനാക്കണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 8,
            hindiTitle = "आठवाँ दिन – धैर्य",
            englishTitle = "Day 8 – Patience",
            hindiPrayer = """
                हे प्रभु,
                परीक्षाओं और कठिनाइयों में मुझे धैर्य, विवेक और आपकी इच्छा स्वीकार करने की कृपा दीजिए।
            """.trimIndent(),
            englishPrayer = """
                O Lord,
                Grant me patience, wisdom, and the grace to accept Your will amidst trials and difficulties.
            """.trimIndent(),
            malayalamTitle = "എട്ടാം ദിവസം – ക്ഷമ",
            malayalamPrayer = """
                കർത്താവേ,
                പരീക്ഷണങ്ങളുടെ നടുവിൽ അങ്ങയുടെ തിരുവിഷ്ടം സ്വീകരിക്കാനുള്ള ക്ഷമയും വിവേകവും കൃപയും എനിക്ക് നൽകണമേ.
            """.trimIndent()
        ),
        NovenaDay(
            dayNumber = 9,
            hindiTitle = "नौवाँ दिन – पूर्ण समर्पण",
            englishTitle = "Day 9 – Total Surrender",
            hindiPrayer = """
                हे प्रभु यीशु,
                आज मैं अपना जीवन, अपना परिवार, अपना भविष्य और अपनी सभी चिंताएँ आपके चरणों में समर्पित करता हूँ। मेरी नहीं, आपकी पवित्र इच्छा पूरी हो।
            """.trimIndent(),
            englishPrayer = """
                O Lord Jesus,
                Today I surrender my life, my family, my future, and all my worries at Your feet. Not my will, but Your holy will be done.
            """.trimIndent(),
            malayalamTitle = "ഒൻപതാം ദിവസം – പൂർണ്ണ സമർപ്പണം",
            malayalamPrayer = """
                കർത്താവായ ഈശോയേ,
                ഇന്ന് എന്റെ ജീവിതവും എന്റെ കുടുംബവും എന്റെ ഭാവിയും എന്റെ എല്ലാ ഉത്കണ്ഠകളും അങ്ങയുടെ പാദങ്ങളിൽ ഞാൻ സമർപ്പിക്കുന്നു. എന്റെ ഇഷ്ടമല്ല, അങ്ങയുടെ തിരുമനസ്സ് നിറവേറട്ടെ.
            """.trimIndent()
        )
    )

    fun getDay(dayNumber: Int): NovenaDay {
        return days.firstOrNull { it.dayNumber == dayNumber } ?: days.first()
    }
}
