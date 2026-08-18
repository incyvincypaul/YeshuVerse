package com.example.data

import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.PrayerType
import com.example.model.RosaryBeadStep
import com.example.model.RosaryMystery
import java.util.Calendar

object RosaryPrayers {

    fun getTodayDefaultMystery(): MysteryType {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY, Calendar.SATURDAY -> MysteryType.JOYFUL
            Calendar.TUESDAY, Calendar.FRIDAY -> MysteryType.SORROWFUL
            Calendar.WEDNESDAY, Calendar.SUNDAY -> MysteryType.GLORIOUS
            Calendar.THURSDAY -> MysteryType.LUMINOUS
            else -> MysteryType.JOYFUL
        }
    }

    // Prayer texts in English and Hindi
    fun getPrayerText(prayerType: PrayerType, language: LanguageEnum): String {
        return when (language) {
            LanguageEnum.ENGLISH -> when (prayerType) {
                PrayerType.SIGN_OF_THE_CROSS ->
                    "In the name of the Father, and of the Son, and of the Holy Spirit. Amen."

                PrayerType.APOSTLES_CREED ->
                    "I believe in God, the Father Almighty, Creator of heaven and earth, and in Jesus Christ, His only Son, our Lord, who was conceived by the Holy Spirit, born of the Virgin Mary, suffered under Pontius Pilate, was crucified, died and was buried; He descended into hell; on the third day He rose again from the dead; He ascended into heaven, and is seated at the right hand of God the Father Almighty; from there He will come to judge the living and the dead.\n\nI believe in the Holy Spirit, the holy catholic Church, the communion of saints, the forgiveness of sins, the resurrection of the body, and life everlasting. Amen."

                PrayerType.OUR_FATHER ->
                    "Our Father, who art in heaven, hallowed be Thy name; Thy kingdom come; Thy will be done on earth as it is in heaven. Give us this day our daily bread; and forgive us our trespasses as we forgive those who trespass against us; and lead us not into temptation, but deliver us from evil. Amen."

                PrayerType.HAIL_MARY ->
                    "Hail Mary, full of grace, the Lord is with thee; blessed art thou among women, and blessed is the fruit of thy womb, Jesus. Holy Mary, Mother of God, pray for us sinners, now and at the hour of our death. Amen."

                PrayerType.GLORY_BE ->
                    "Glory be to the Father, and to the Son, and to the Holy Spirit. As it was in the beginning, is now, and ever shall be, world without end. Amen."

                PrayerType.FATIMA_PRAYER ->
                    "O my Jesus, forgive us our sins, save us from the fires of hell, lead all souls to Heaven, especially those most in need of Thy mercy. Amen."

                PrayerType.HAIL_HOLY_QUEEN ->
                    "Hail, Holy Queen, Mother of Mercy, our life, our sweetness, and our hope. To thee do we cry, poor banished children of Eve. To thee do we send up our sighs, mourning and weeping in this valley of tears. Turn then, most gracious advocate, thine eyes of mercy toward us, and after this our exile, show unto us the blessed fruit of thy womb, Jesus. O clement, O loving, O sweet Virgin Mary.\n\nPray for us, O Holy Mother of God, that we may be made worthy of the promises of Christ. Amen."

                PrayerType.MEMORARE ->
                    "Remember, O most gracious Virgin Mary, that never was it known that anyone who fled to thy protection, implored thy help, or sought thine intercession was left unaided. Inspired by this confidence, I fly unto thee, O Virgin of virgins, my Mother; to thee do I come, before thee I stand, sinful and sorrowful. O Mother of the Word Incarnate, despise not my petitions, but in thy mercy hear and answer me. Amen."

                PrayerType.LITANY_OF_LORETO ->
                    "Lord, have mercy. Christ, have mercy. Lord, have mercy.\nHoly Mary, pray for us.\nHoly Mother of God, pray for us.\nHoly Virgin of virgins, pray for us.\nMother of Christ, pray for us.\nMother of divine grace, pray for us.\nQueen of Angels, pray for us.\nQueen of Apostles, pray for us.\nQueen of Peace, pray for us.\nLamb of God, who takest away the sins of the world, spare us, O Lord. Amen."

                PrayerType.CONCLUDING_PRAYER ->
                    "O God, whose only begotten Son, by His life, death, and resurrection, has purchased for us the rewards of eternal life, grant, we beseech Thee, that meditating upon these mysteries of the Most Holy Rosary of the Blessed Virgin Mary, we may imitate what they contain and obtain what they promise, through the same Christ our Lord. Amen."
                
                PrayerType.INTRO_PRAYER ->
                    "Let us sit in a spirit of prayer and focus on the Lord. We are beginning our prayer in the name of the Lord Jesus. Amen."
                    

            }

            
        LanguageEnum.MALAYALAM -> when (prayerType) {
            PrayerType.SIGN_OF_THE_CROSS -> "പിതാവിന്റെയും പുത്രന്റെയും പരിശുദ്ധാത്മാവിന്റെയും നാമത്തിൽ. ആമ്മേൻ."
            PrayerType.APOSTLES_CREED -> "സർവ്വശക്തനായ പിതാവായ ദൈവത്തിൽ ഞാൻ വിശ്വസിക്കുന്നു. അവിടുത്തെ ഏകപുത്രനും ഞങ്ങളുടെ കർത്താവുമായ ഈശോമിശിഹായിലും ഞാൻ വിശ്വസിക്കുന്നു. ഈ പുത്രൻ പരിശുദ്ധാത്മാവിനാൽ ഗർഭസ്ഥനായി കന്യാമറിയത്തിൽനിന്നു ജനിച്ചു. പീലാത്തോസിന്റെ ഭരണത്തിൻകീഴിൽ പീഡകൾ അനുഭവിച്ചു, കുരിശിൽ തറയ്ക്കപ്പെട്ട് മരിച്ചു അടക്കപ്പെട്ടു. പാതാളത്തിൽ ഇറങ്ങി മരിച്ചവരുടെ ഇടയിൽനിന്ന് മൂന്നാം നാൾ ഉയിർത്തു. സ്വർഗ്ഗത്തിലേക്ക് എഴുന്നള്ളി സർവ്വശക്തനായ പിതാവായ ദൈവത്തിന്റെ വലത്തുഭാഗത്ത് ഇരിക്കുന്നു. അവിടെനിന്ന് ജീവിക്കുന്നവരെയും മരിച്ചവരെയും വിധിക്കാൻ വരുമെന്നും ഞാൻ വിശ്വസിക്കുന്നു. പരിശുദ്ധാത്മാവിലും ഞാൻ വിശ്വസിക്കുന്നു. വിശുദ്ധ കത്തോലിക്കാ സഭയിലും, പുണ്യവാന്മാരുടെ ഐക്യത്തിലും പാപങ്ങളുടെ മോചനത്തിലും ശരീരത്തിന്റെ ഉയിർപ്പിലും നിത്യമായ ജീവിതത്തിലും ഞാൻ വിശ്വസിക്കുന്നു. ആമ്മേൻ."
            PrayerType.OUR_FATHER -> "സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ, അങ്ങയുടെ നാമം പൂജിതമാകണമേ. അങ്ങയുടെ രാജ്യം വരണമേ. അങ്ങയുടെ തിരുമനസ്സ് സ്വർഗ്ഗത്തിലെപ്പോലെ ഭൂമിയിലുമാകണമേ. അന്നന്നു വേണ്ട ആഹാരം ഇന്ന് ഞങ്ങൾക്ക് തരണമേ. ഞങ്ങളോടു തെറ്റുചെയ്യുന്നവരോട് ഞങ്ങൾ ക്ഷമിക്കുന്നതുപോലെ ഞങ്ങളുടെ തെറ്റുകൾ ഞങ്ങളോടും ക്ഷമിക്കണമേ. ഞങ്ങളെ പ്രലോഭനത്തിൽ ഉൾപ്പെടുത്തരുതേ. തിന്മയിൽനിന്ന് ഞങ്ങളെ രക്ഷിക്കണമേ. ആമ്മേൻ."
            PrayerType.HAIL_MARY -> "നന്മ നിറഞ്ഞ മറിയമേ സ്വസ്തി! കർത്താവ് അങ്ങയോടുകൂടെ. സ്ത്രീകളിൽ അങ്ങ് അനുഗ്രഹിക്കപ്പെട്ടവളാകുന്നു. അങ്ങയുടെ ഉദരത്തിൽ ഫലമായ ഈശോ അനുഗ്രഹിക്കപ്പെട്ടവനാകുന്നു. പരിശുദ്ധ മറിയമേ തമ്പുരാന്റെ അമ്മേ, പാപികളായ ഞങ്ങൾക്കുവേണ്ടി ഇപ്പോഴും ഞങ്ങളുടെ മരണസമയത്തും തമ്പുരാനോട് അപേക്ഷിക്കണമേ. ആമ്മേൻ."
            PrayerType.GLORY_BE -> "പിതാവിനും പുത്രനും പരിശുദ്ധാത്മാവിനും സ്തുതി. ആദിയിലെപ്പോലെ ഇപ്പോഴും എപ്പോഴും എന്നേക്കും ആമ്മേൻ."
            PrayerType.FATIMA_PRAYER -> "ഓ എന്റെ ഈശോയേ, ഞങ്ങളുടെ പാപങ്ങൾ ക്ഷമിക്കണമേ. നരകാഗ്നിയിൽനിന്ന് ഞങ്ങളെ രക്ഷിക്കണമേ. എല്ലാ ആത്മാക്കളെയും പ്രത്യേകിച്ച് അങ്ങയുടെ സഹായം കൂടുതൽ ആവശ്യമുള്ളവരെയും സ്വർഗ്ഗത്തിലേക്ക് ആനയിക്കണമേ. ആമ്മേൻ."
            PrayerType.HAIL_HOLY_QUEEN -> "പരിശുദ്ധ രാജ്ഞീ, കരുണയുടെ മാതാവേ സ്വസ്തി! ഞങ്ങളുടെ ജീവനും മാധുര്യവും ശരണവുമേ സ്വസ്തി! ഹവ്വായുടെ പുറന്തള്ളപ്പെട്ട മക്കളായ ഞങ്ങൾ അങ്ങേപ്പക്കൽ നിലവിളിക്കുന്നു. കണ്ണുനീരിന്റെ ഈ താഴ്‌വരയിൽ നിന്ന് വിങ്ങിക്കരഞ്ഞ് അങ്ങേപ്പക്കൽ ഞങ്ങൾ നെടുവീർപ്പിടുന്നു. ആകയാൽ ഞങ്ങളുടെ മദ്ധ്യസ്ഥേ, അങ്ങയുടെ കരുണയുള്ള കണ്ണുകൾ ഞങ്ങളുടെ നേരെ തിരിക്കണമേ. ഞങ്ങളുടെ ഈ പ്രവാസത്തിനുശേഷം അങ്ങയുടെ ഉദരത്തിന്റെ അനുഗൃഹീത ഫലമായ ഈശോയെ ഞങ്ങൾക്ക് കാണിച്ചുതരണമേ. കരുണയും വാത്സല്യവും മാധുര്യവും നിറഞ്ഞ കന്യാമറിയമേ! ആമ്മേൻ."
            PrayerType.MEMORARE -> "എത്രയും ദയയുള്ള മാതാവേ, അങ്ങയുടെ സങ്കേതത്തിൽ ഓടിവന്ന് അങ്ങയുടെ സഹായം തേടി അങ്ങയുടെ മാദ്ധ്യസ്ഥം അപേക്ഷിച്ചവരിൽ ഒരുവനെയെങ്കിലും അങ്ങ് ഉപേക്ഷിച്ചതായി കേട്ടിട്ടില്ല എന്ന് അങ്ങ് ഓർക്കണമേ. കന്യകകളുടെ രാജ്ഞിയായ കന്യകേ, ദയയുള്ള മാതാവേ, ഈ വിശ്വാസത്തിൽ ധൈര്യപ്പെട്ട് അങ്ങയുടെ തൃപ്പാദത്തിങ്കൽ ഞാൻ അണയുന്നു. വിലപിച്ചുകൊണ്ട് പാപിയായ ഞാൻ അങ്ങയുടെ സന്നിധിയിൽ നിൽക്കുന്നു. അവതരിച്ച വചനത്തിന്റെ മാതാവേ, എന്റെ അപേക്ഷകൾ ഉപേക്ഷിക്കാതെ ദയാപൂർവ്വം കേട്ടരുളണമേ. ആമ്മേൻ."
            PrayerType.LITANY_OF_LORETO -> "(മാതാവിന്റെ ലുത്തിനിയ)\nകർത്താവേ കനിയണമേ. മിശിഹായെ കനിയണമേ.\nകർത്താവേ കനിയണമേ...\nക്രിസ്തുവേ ഞങ്ങളുടെ പ്രാർത്ഥന കേൾക്കണമേ...\nസ്വർഗ്ഗസ്ഥനായ പിതാവായ ദൈവമേ... (ഞങ്ങളെ അനുഗ്രഹിക്കണമേ)\nഭൂലോകരക്ഷകനായ പുത്രനായ ദൈവമേ... (ഞങ്ങളെ അനുഗ്രഹിക്കണമേ)\nപരിശുദ്ധാത്മാവായ ദൈവമേ... (ഞങ്ങളെ അനുഗ്രഹിക്കണമേ)\nഏകദൈവമായ പരിശുദ്ധ ത്രിത്വമേ... (ഞങ്ങളെ അനുഗ്രഹിക്കണമേ)\nപരിശുദ്ധ മറിയമേ... (ഞങ്ങൾക്കുവേണ്ടി അപേക്ഷിക്കണമേ)\nദൈവത്തിന്റെ പരിശുദ്ധ മാതാവേ...\nകന്യകകൾക്കു മകുടമായ നിർമ്മല കന്യകേ...\nമിശിഹായുടെ മാതാവേ...\nദൈവവരപ്രസാദത്തിന്റെ മാതാവേ..."
            PrayerType.CONCLUDING_PRAYER -> "സർവ്വശക്തനായ ദൈവമേ, ഈശോമിശിഹായുടെ ജീവിതവും മരണവും ഉയിർപ്പും വഴി ഞങ്ങൾക്ക് നിത്യരക്ഷ നൽകിയല്ലോ. പരിശുദ്ധ കന്യാമറിയത്തിന്റെ ജപമാലയിലെ ഈ രഹസ്യങ്ങളെക്കുറിച്ച് ധ്യാനിക്കുന്ന ഞങ്ങൾ ഇവയിൽ അടങ്ങിയിരിക്കുന്നവ അനുകരിക്കാനും വാഗ്ദാനം ചെയ്യപ്പെട്ടിരിക്കുന്നവ പ്രാപിക്കാനും അനുഗ്രഹം നൽകണമേ. ഞങ്ങളുടെ കർത്താവായ ഈശോമിശിഹാവഴി ഞങ്ങൾ അപേക്ഷിക്കുന്നു. ആമ്മേൻ."
            PrayerType.INTRO_PRAYER -> "നമുക്ക് പ്രാർത്ഥിക്കാം..."
        }

        LanguageEnum.HINDI -> when (prayerType) {
                PrayerType.SIGN_OF_THE_CROSS ->
                    "पिता और पुत्र और पवित्र आत्मा के नाम पर। आमीन।"

                PrayerType.APOSTLES_CREED ->
                    "मैं सर्वशक्तिमान पिता परमेश्वर पर, जो आकाश और पृथ्वी का सृजनहार है, विश्वास करता हूँ। और उसके इकलौते पुत्र, हमारे प्रभु येसु मसीह पर, जो पवित्र आत्मा के द्वारा गर्भ में आये, और कुंवारी मरियम से जन्म लिया। उन्होंने पोंतियुस पिलातुस के समय दुःख भोगा, क्रूस पर चढ़ाये गये, मर गये और दफनाये गये। वे अधोलोक में उतरे, और तीसरे दिन जी उठे। वे स्वर्ग पर चढ़ गये, और सर्वशक्तिमान पिता परमेश्वर के दाहिने विराजमान हैं। वहाँ से वे जीवितों और मृतकों का न्याय करने आयेंगे।\n\nमैं पवित्र आत्मा, पवित्र काथलिक कलीसिया, धर्मात्माओं के सहभाग, पापों की क्षमा, देह के पुनरुत्थान और अनन्त जीवन पर विश्वास करता हूँ। आमीन।"

                PrayerType.OUR_FATHER ->
                    "हे हमारे पिता, तू जो स्वर्ग में है, तेरा नाम पवित्र किया जावे, तेरा राज्य आवे, तेरी इच्छा जैसे स्वर्ग में है वैसे इस पृथ्वी पर भी पूरी हो। हमारा प्रतिदिन का आहार आज हमें दे, और हमारे अपराध हमें क्षमा कर, जैसे हम भी अपने अपराधियों को क्षमा करते हैं। और हमें परीक्षा में न डाल, परन्तु बुराई से बचा। आमीन।"

                PrayerType.HAIL_MARY ->
                    "प्रणाम मरिया, कृपा से परिपूर्ण, प्रभु तेरे साथ है।\nधन्य तू स्त्रियों में, और धन्य तेरे गर्भ का फल, येसु।\nहे संत मरिया, परमेश्वर की माता, प्रार्थना कर हम पापियों के लिए,\nअब और हमारे मरने के समय। आमीन।"

                PrayerType.GLORY_BE ->
                    "पिता और पुत्र और पवित्र आत्मा की महिमा हो, जैसे वह आदि में थी, अब है और अनन्त काल तक रहेगी। आमीन।"

                PrayerType.FATIMA_PRAYER ->
                    "ओ मेरे येसु, हमारे पापों को क्षमा कर, हमें नरक की आग से बचा, और सभी आत्माओं को स्वर्ग में ले चल, विशेषकर उन्हें जिन्हें तेरी दया की सबसे अधिक आवश्यकता है। आमीन।"

                PrayerType.HAIL_HOLY_QUEEN ->
                    "हे पवित्र रानी, दया की माता, हमारा जीवन, हमारी मधुरता और हमारी आशा, तुझे प्रणाम। हम कृपा की निर्वासित संतान तुझे पुकारते हैं। इस अश्रु-घाटी में रोते और बिलखते हुए हम तेरी ओर आहें भरते हैं। इसलिए हे हमारी वकील, अपनी दया दृष्टि हमारी ओर फेर, और इस निर्वासन के बाद अपने गर्भ का धन्य फल येसु हमें दिखा। हे कृपालु, हे दयालु, हे कोमल कुंवारी मरियम।\n\nहे परमेश्वर की पवित्र माता, हमारे लिए प्रार्थना कर, कि हम मसीह की प्रतिज्ञाओं के योग्य बन जाएँ। आमीन।"

                PrayerType.MEMORARE ->
                    "हे अति दयालु कुंवारी मरियम! याद कर कि यह कभी सुनने में नहीं आया कि कोई तेरी शरण में आया हो, तेरी सहायता मांगी हो और तेरी मध्यस्थता चाही हो और तूने उसे छोड़ दिया हो। इसी विश्वास से प्रेरित होकर, हे कुंवाइयों की कुंवारी, हे मेरी माता! मैं तेरे पास आता हूँ, और रोते-बिलखते हुए तेरे सामने खड़ा होता हूँ। हे देहधारी शब्द की माता, मेरी विनती की अवहेलना न कर, बल्कि दयापूर्वक मेरी सुन और स्वीकार कर। आमीन।"

                PrayerType.LITANY_OF_LORETO ->
                    "हे प्रभु, हम पर दया कर। हे मसीह, हम पर दया कर। हे प्रभु, हम पर दया कर।\nहे संत मरिया, हमारे लिए प्रार्थना कर।\nहे परमेश्वर की पवित्र माता, हमारे लिए प्रार्थना कर।\nहे कुंवाइयों की पवित्र कुंवारी, हमारे लिए प्रार्थना कर।\nहे मसीह की माता, हमारे लिए प्रार्थना कर।\nहे स्वर्गदूतों की रानी, हमारे लिए प्रार्थना कर।\nहे शांति की रानी, हमारे लिए प्रार्थना कर।\nहे ईश्वर के मेमने, जो संसार के पाप हर लेता है, हे प्रभु हमें क्षमा कर। आमीन।"

                PrayerType.CONCLUDING_PRAYER ->
                    "हे ईश्वर, जिसके इकलौते पुत्र ने अपने जीवन, मृत्यु और पुनरुत्थान के द्वारा हमारे लिए अनन्त जीवन का पुरस्कार प्राप्त किया है, हम तुझसे विनती करते हैं कि माता मरियम की अति पवित्र माला के इन भेदों पर ध्यान करते हुए, हम उनकी शिक्षाओं का अनुकरण करें और उनकी प्रतिज्ञाओं को प्राप्त करें। उसी हमारे प्रभु मसीह के द्वारा। आमीन।"
                
                PrayerType.INTRO_PRAYER ->
                    "सब लोग प्रार्थना की स्थिति में बैठ जाएं और प्रभु पे ध्यान लगाएं। हम प्रार्थना शुरू कर रहे हैं प्रभु यीशु के नाम से। आमेन।"


            }
        }
    }

    // Phonetic Romanized Hindi prayer text for smooth TTS reading when Hindi TTS engine is not present
    fun getRomanizedHindiPrayerText(prayerType: PrayerType): String {
        return when (prayerType) {
            PrayerType.SIGN_OF_THE_CROSS ->
                "Pita aur Putra aur Pavitra Aatma ke naam par. Amen."

            PrayerType.APOSTLES_CREED ->
                "Main sarvashaktiman Pita Parmeshwar par, jo aakash aur prithvi ka srijanhaar hai, vishwas karta hoon. Aur uske iklaute Putra, hamare Prabhu Yeshu Masih par, jo Pavitra Aatma ke dwara garbha mein aaye, aur Kuwari Mariya se janma liya. Unhone Pontius Pilatus ke samay dukh bhoga, krus par chadhaye gaye, mar gaye aur dafnaye gaye. Ve adholok mein utare, aur teesre din jee uthe. Ve swarg par chadh gaye, aur sarvashaktiman Pita Parmeshwar ke dahine virajmaan hain. Wahan se ve jeeviton aur mritakon ka nyay karne aayenge. Main Pavitra Aatma, pavitra Catholic Kaleesiya, dharmatmaon ke sahbhag, paapon ki kshama, deh ke punarutthan aur anant jeevan par vishwas karta hoon. Amen."

            PrayerType.OUR_FATHER ->
                "He hamare Pita, tu jo swarg mein hai, tera naam pavitra kiya jaave, tera rajya aave, teri ichha jaise swarg mein hai waise is prithvi par bhi poori ho. Hamara pratidin ka aahar aaj hame de, aur hamare aparadh hame kshama kar, jaise hum bhi apne aparadhiyon ko kshama karte hain. Aur hame pariksha mein na daal, parantu burai se bacha. Amen."

            PrayerType.HAIL_MARY ->
                "Pranam Mariya, kripa se paripurna, Prabhu tere saath hai. Dhanya tu striyon mein, aur dhanya tere garbh ka phal, Yeshu. He Sant Mariya, Parmeshwar ki Mata, prarthana kar hum paapiyon ke liye, ab aur hamare marne ke samay. Amen."

            PrayerType.GLORY_BE ->
                "Pita aur Putra aur Pavitra Aatma ki mahima ho, jaise vah aadi mein thi, ab hai aur anant kaal tak rahegi. Amen."

            PrayerType.FATIMA_PRAYER ->
                "O mere Yeshu, hamare paapon ko kshama kar, hame narak ki aag se bacha, aur sabhi aatmaon ko swarg mein le chal, visheshkar unhe jinhe teri daya ki sabse adhik aavashyakta hai. Amen."

            PrayerType.HAIL_HOLY_QUEEN ->
                "He Pavitra Rani, daya ki Mata, hamara jeevan, hamari madhurta aur hamari aasha, tujhe pranam. Hum kripa ki nirvasit santan tujhe pukarte hain. Is ashru ghati mein rote aur bilakhte hue hum teri aur aahein bharte hain. Isliye he hamari vakil, apni daya drishti hamari aur pher, aur is nirvasan ke baad apne garbh ka dhanya phal Yeshu hame dikha. He kripalu, he dayalu, he komal Kuwari Mariya. He Parmeshwar ki pavitra Mata, hamare liye prarthana kar, ki hum Masih ki pratijnaon ke yogya ban jayein. Amen."

            PrayerType.MEMORARE ->
                "He ati dayalu Kuwari Mariya! Yaad kar ki yeh kabhi sunne mein nahi aaya ki koi teri sharan mein aaya ho, teri sahayata maangi ho aur teri madhyasthata chaahi ho aur tune use chhod diya ho. Isi vishwas se prerit hokar, he kuwaiyon ki kuwari, he meri Mata! Main tere paas aata hoon, aur rote-bilakhte hue tere samne khada hota hoon. He dehchhari Shabd ki Mata, meri vinati ki avhelna na kar, balki dayapoorvak meri sun aur sweekar kar. Amen."

            PrayerType.LITANY_OF_LORETO ->
                "He Prabhu, hum par daya kar. He Masih, hum par daya kar. He Prabhu, hum par daya kar.\nHe Sant Mariya, hamare liye prarthana kar.\nHe Parmeshwar ki pavitra Mata, hamare liye prarthana kar.\nHe Kuwaiyon ki pavitra Kuwari, hamare liye prarthana kar.\nHe Masih ki Mata, hamare liye prarthana kar.\nHe Swargduton ki Rani, hamare liye prarthana kar.\nHe Shanti ki Rani, hamare liye prarthana kar.\nHe Ishwar ke Memne, jo sansar ke paap har leta hai, he Prabhu hame kshama kar. Amen."

            PrayerType.CONCLUDING_PRAYER ->
                "He Ishwar, jiske iklaute Putra ne apne jeevan, mrityu aur punarutthan ke dwara hamare liye anant jeevan ka puraskar prapt kiya hai, hum tujhse vinati karte hain ki Mata Mariya ki ati pavitra mala ke in bhedon par dhyan karte hue, hum unki shikshaon ka anukaran karein aur unki pratijnaon ko prapt karein. Usi hamare Prabhu Masih ke dwara. Amen."
            
            PrayerType.INTRO_PRAYER ->
                "Sab log prarthana ki sthiti mein baith jayein aur Prabhu pe dhyan lagayein. Hum prarthana shuru kar rahe hain Prabhu Yeshu ke naam se. Amen."
                

        }
    }

        fun getMysteriesForType(type: MysteryType): List<RosaryMystery> {
        return when (type) {
            MysteryType.JOYFUL -> listOf(
                RosaryMystery(
                    type, 1,
                    "1st Joyful Mystery: The Annunciation", "आनंद का पहला भेद: स्वर्गदूत का संदेश", "ഒന്നാം സന്തോഷ രഹസ്യം: മംഗളവാർത്ത",
                    "The Archangel Gabriel announces to Mary that she will conceive Jesus.",
                    "स्वर्गदूत गाब्रिएल माता मरियम को संदेश देते हैं कि वह मुक्तिदाता की माँ बनेंगी।",
                    "ഗബ്രിയേൽ മാലാഖ പരിശുദ്ധ കന്യകാമറിയത്തെ മംഗളവാർത്ത അറിയിക്കുന്നു.",
                    "Humility", "विनम्रता", "വിനയം"
                ),
                RosaryMystery(
                    type, 2,
                    "2nd Joyful Mystery: The Visitation", "आनंद का दूसरा भेद: माता मरियम की भेंट", "രണ്ടാം സന്തോഷ രഹസ്യം: എലിസബത്തിനെ സന്ദർശിക്കുന്നത്",
                    "Mary visits her cousin Elizabeth to help her in her pregnancy.",
                    "माता मरियम अपनी रिश्तेदार एलीज़ाबेथ से मिलने जाती हैं।",
                    "പരിശുദ്ധ കന്യകാമറിയം എലിസബത്തിനെ സന്ദർശിക്കുന്നു.",
                    "Love of Neighbor", "पड़ोसी से प्रेम", "അയൽസ്നേഹം"
                ),
                RosaryMystery(
                    type, 3,
                    "3rd Joyful Mystery: The Nativity", "आनंद का तीसरा भेद: येसु का जन्म", "മൂന്നാം സന്തോഷ രഹസ്യം: യേശുവിന്റെ ജനനം",
                    "Jesus is born in a humble stable in Bethlehem.",
                    "प्रभु येसु का जन्म बेथलेहेम की एक गौशाला में होता है।",
                    "യേശുക്രിസ്തു ബെത്‌ലഹേമിലെ കാലിത്തൊഴുത്തിൽ ജനിക്കുന്നു.",
                    "Poverty of Spirit", "सादगी और गरीबी", "ദാരിദ്ര്യം"
                ),
                RosaryMystery(
                    type, 4,
                    "4th Joyful Mystery: The Presentation", "आनंद का चौथा भेद: येसु का मंदिर में अर्पण", "നാലാം സന്തോഷ രഹസ്യം: യേശുവിനെ ദേവാലയത്തിൽ സമർപ്പിക്കുന്നത്",
                    "Mary and Joseph present the child Jesus in the Temple to God.",
                    "माता मरियम और योसेफ बाल येसु को मंदिर में ईश्वर को अर्पित करते हैं।",
                    "പരിശുദ്ധ കന്യകാമറിയവും യൗസേപ്പിതാവും യേശുവിനെ ദേവാലയത്തിൽ സമർപ്പിക്കുന്നു.",
                    "Purity of Mind & Body", "आज्ञापालन", "അനുസരണം"
                ),
                RosaryMystery(
                    type, 5,
                    "5th Joyful Mystery: Finding in the Temple", "आनंद का पाँचवाँ भेद: येसु का मंदिर में मिलना", "അഞ്ചാം സന്തോഷ രഹസ്യം: യേശുവിനെ ദേവാലയത്തിൽ കണ്ടെത്തുന്നത്",
                    "Mary and Joseph find the boy Jesus teaching in the Temple after being lost.",
                    "तीन दिन बाद माता मरियम और योसेफ बाल येसु को मंदिर में धर्मशास्त्रियों के बीच पाते हैं।",
                    "കാണാതായ യേശുവിനെ മൂന്നു ദിവസങ്ങൾക്ക് ശേഷം ദേവാലയത്തിൽ കണ്ടെത്തുന്നു.",
                    "Joy in Finding Jesus", "येसु की खोज", "യേശുവിനെ കണ്ടെത്താനുള്ള ആഗ്രഹം"
                )
            )
            MysteryType.SORROWFUL -> listOf(
                RosaryMystery(
                    type, 1,
                    "1st Sorrowful Mystery: Agony in the Garden", "दुःख का पहला भेद: गेथसेमनी बाड़ी में येसु का रक्त पसीना", "ഒന്നാം ദുഃഖ രഹസ്യം: ഗെത്സെമനി തോട്ടത്തിലെ പ്രാർത്ഥന",
                    "Jesus prays in the Garden of Gethsemane and sweats blood for our sins.",
                    "येसु गेथसेमनी बाड़ी में घोर पीड़ा और रक्त का पसीना बहाते हुए पिता से प्रार्थना करते हैं।",
                    "യേശുക്രിസ്തു ഗെത്സെമനി തോട്ടത്തിൽ രക്തം വിയർത്തു പ്രാർത്ഥിക്കുന്നു.",
                    "Sorrow for Sin", "पापों के लिए पश्चाताप", "പാപങ്ങളെക്കുറിച്ചുള്ള അനുതാപം"
                ),
                RosaryMystery(
                    type, 2,
                    "2nd Sorrowful Mystery: Scourging at the Pillar", "दुःख का दूसरा भेद: येसु को कोड़े मारा जाना", "രണ്ടാം ദുഃഖ രഹസ്യം: യേശുവിനെ ചമ്മട്ടികൊണ്ട് അടിക്കുന്നത്",
                    "Jesus is brutally scourged and whipped by the Roman soldiers.",
                    "रोमन सिपाही येसु को खंभे से बांधकर बेरहमी से कोड़े मारते हैं।",
                    "യേശുക്രിസ്തുവിനെ തൂണിൽ കെട്ടി ചമ്മട്ടികൊണ്ട് അടിക്കുന്നു.",
                    "Purity", "शारीरिक कष्टों में धैर्य", "ഇന്ദ്രിയനിഗ്രഹം"
                ),
                RosaryMystery(
                    type, 3,
                    "3rd Sorrowful Mystery: Crowning with Thorns", "दुःख का तीसरा भेद: येसु को कांटों का मुकुट पहनाना", "മൂന്നാം ദുഃഖ രഹസ്യം: യേശുവിനെ മുൾമുടി ധരിപ്പിക്കുന്നത്",
                    "Soldiers weave a crown of sharp thorns and press it onto the head of Jesus.",
                    "सिपाही कांटों का मुकुट बनाकर येसु के सिर पर पहनाते हैं।",
                    "പടയാളികൾ യേശുക്രിസ്തുവിനെ മുൾമുടി ധരിപ്പിക്കുന്നു.",
                    "Moral Courage", "धैर्य और आत्म-नियंत्रण", "ധൈര്യം"
                ),
                RosaryMystery(
                    type, 4,
                    "4th Sorrowful Mystery: Carrying of the Cross", "दुःख का चौथा भेद: येसु का क्रूस उठाना", "നാലാം ദുഃഖ രഹസ്യം: യേശു കുരിശ് ചുമക്കുന്നത്",
                    "Jesus carries the heavy wooden cross up the hill to Calvary.",
                    "येसु भारी क्रूस को अपने कांधे पर उठाकर कलवारी पर्वत की ओर चलते हैं।",
                    "യേശുക്രിസ്തു കാൽവരി മലയിലേക്ക് കുരിശ് ചുമന്നുകൊണ്ടുപോകുന്നു.",
                    "Patience in Suffering", "कष्टों में धैर्य", "സഹനശക്തി"
                ),
                RosaryMystery(
                    type, 5,
                    "5th Sorrowful Mystery: The Crucifixion", "दुःख का पाँचवाँ भेद: येसु का क्रूस मरण", "അഞ്ചാം ദുഃഖ രഹസ്യം: യേശുവിന്റെ കുരിശുമരണം",
                    "Jesus is nailed to the Cross and dies for the salvation of humanity.",
                    "येसु को क्रूस पर ठोका जाता है और वे मानव जाति के उद्धार के लिए प्राण त्यागते हैं।",
                    "യേശുക്രിസ്തു കുരിശിൽ തറയ്ക്കപ്പെട്ടു മരിക്കുന്നു.",
                    "Self-Sacrifice & Forgiveness", "क्षमाशीलता और बलिदान", "ക്ഷമയും ത്യാഗവും"
                )
            )
            MysteryType.GLORIOUS -> listOf(
                RosaryMystery(
                    type, 1,
                    "1st Glorious Mystery: The Resurrection", "महिमा का पहला भेद: प्रभु येसु का पुनरुत्थान", "ഒന്നാം മഹിമ രഹസ്യം: യേശുവിന്റെ ഉയിർപ്പ്",
                    "Jesus conquers death and rises gloriously from the tomb on Easter Sunday.",
                    "येसु मृत्यु पर विजय प्राप्त कर जी उठते हैं।",
                    "യേശുക്രിസ്തു മരിച്ചവരിൽനിന്ന് ഉയിർത്തെഴുന്നേൽക്കുന്നു.",
                    "Faith", "विश्वास", "വിശ്വാസം"
                ),
                RosaryMystery(
                    type, 2,
                    "2nd Glorious Mystery: The Ascension", "महिमा का दूसरा भेद: येसु का स्वर्गारोहण", "രണ്ടാം മഹിമ രഹസ്യം: യേശുവിന്റെ സ്വർഗ്ഗാരോഹണം",
                    "Jesus ascends into Heaven 40 days after His Resurrection.",
                    "येसु अपने शिष्यों के सामने स्वर्गारोहण करते हैं।",
                    "യേശുക്രിസ്തു സ്വർഗ്ഗാരോഹണം ചെയ്യുന്നു.",
                    "Hope & Desire for Heaven", "स्वर्ग की आशा", "പ്രത്യാശ"
                ),
                RosaryMystery(
                    type, 3,
                    "3rd Glorious Mystery: Descent of the Holy Spirit", "महिमा का तीसरा भेद: पवित्र आत्मा का आगमन", "മൂന്നാം മഹിമ രഹസ്യം: പരിശുദ്ധാത്മാവിന്റെ എഴുന്നള്ളത്ത്",
                    "The Holy Spirit descends as tongues of fire upon Mary and the Apostles at Pentecost.",
                    "पवित्र आत्मा आग की जीभों के रूप में माता मरियम और प्रेरितों पर उतरता है।",
                    "പരിശുദ്ധാത്മാവ് ശ്ലീഹന്മാരുടെ മേൽ എഴുന്നള്ളിവരുന്നു.",
                    "Love of God & Wisdom", "पवित्र आत्मा का दान", "ദൈവസ്നേഹം"
                ),
                RosaryMystery(
                    type, 4,
                    "4th Glorious Mystery: The Assumption", "महिमा का चौथा भेद: माता मरियम का स्वर्गारोहण", "നാലാം മഹിമ രഹസ്യം: പരിശുദ്ധ കന്യകാമറിയത്തിന്റെ സ്വർഗ്ഗാരോപണം",
                    "Mary is taken up body and soul into Heavenly glory.",
                    "माता मरियम को सशरीर स्वर्ग में उठा लिया जाता है।",
                    "പരിശുദ്ധ കന്യകാമറിയം സ്വർഗ്ഗത്തിലേക്ക് എടുക്കപ്പെടുന്നു.",
                    "Grace of a Happy Death", "मरण की कृपा", "നല്ല മരണത്തിനുള്ള കൃപ"
                ),
                RosaryMystery(
                    type, 5,
                    "5th Glorious Mystery: Coronation of Mary", "महिमा का पाँचवाँ भेद: माता मरियम का मुकुटधारण", "അഞ്ചാം മഹിമ രഹസ്യം: പരിശുദ്ധ കന്യകാമറിയത്തിന്റെ മുടിചൂടൽ",
                    "Mary is crowned Queen of Heaven and Earth by Jesus.",
                    "माता मरियम को स्वर्ग और पृथ्वी की रानी का मुकुट पहनाया जाता है।",
                    "പരിശുദ്ധ കന്യകാമറിയത്തെ സ്വർഗ്ഗത്തിന്റെയും ഭൂമിയുടെയും രാജ്ഞിയായി മുടിചൂടിക്കുന്നു.",
                    "Trust in Mary's Intercession", "माता मरियम की मध्यस्थता", "മാതാവിന്റെ മദ്ധ്യസ്ഥതത്തിലുള്ള ആശ്രയം"
                )
            )
            MysteryType.LUMINOUS -> listOf(
                RosaryMystery(
                    type, 1,
                    "1st Luminous Mystery: Baptism in the Jordan", "प्रकाश का पहला भेद: येसु का बपतिस्मा", "ഒന്നാം പ്രകാശ രഹസ്യം: യേശുവിന്റെ മാമ്മോദീസ",
                    "Jesus is baptized by John in the Jordan River and the Father proclaims Him Beloved Son.",
                    "येसु यरदन नदी में बपतिस्मा लेते हैं और ईश्वर की वाणी सुनाई देती है।",
                    "യേശുക്രിസ്തു ജോർദാൻ നദിയിൽ വെച്ച് മാമ്മോദീസ സ്വീകരിക്കുന്നു.",
                    "Openness to Holy Spirit", "पवित्र जीवन की लालसा", "പരിശുദ്ധാത്മാവിനോടുള്ള തുറവി"
                ),
                RosaryMystery(
                    type, 2,
                    "2nd Luminous Mystery: Miracle at Cana", "प्रकाश का दूसरा भेद: काना का विवाह चमत्कार", "രണ്ടാം പ്രകാശ രഹസ്യം: കാനായിലെ കല്യാണം",
                    "Jesus changes water into wine at the wedding feast through Mary's intercession.",
                    "माता मरियम के कहने पर येसु पानी को दाखारस में बदलते हैं।",
                    "കാനായിലെ കല്യാണവിരുന്നിൽ യേശു വെള്ളം വീഞ്ഞാക്കുന്നു.",
                    "Fidelity & Marriage", "मरियम के द्वारा येसु तक पहुँचना", "മാതാവിലൂടെ യേശുവിലേക്ക്"
                ),
                RosaryMystery(
                    type, 3,
                    "3rd Luminous Mystery: Proclamation of Kingdom", "प्रकाश का तीसरा भेद: ईश्वर के राज्य की घोषणा", "മൂന്നാം പ്രകാശ രഹസ്യം: ദൈവരാജ്യ പ്രഖ്യാപനം",
                    "Jesus proclaims the coming of the Kingdom of God and calls all to repentance.",
                    "येसु ईश्वर के राज्य की घोषणा करते हैं और पश्चाताप का आह्वान करते हैं।",
                    "യേശുക്രിസ്തു ദൈവരാജ്യം പ്രഖ്യാപിക്കുന്നു.",
                    "Repentance & Trust in Gospel", "हृदय परिवर्तन", "മാനസാന്തരം"
                ),
                RosaryMystery(
                    type, 4,
                    "4th Luminous Mystery: The Transfiguration", "प्रकाश का चौथा भेद: येसु का रूप-रूपांतरण", "നാലാം പ്രകാശ രഹസ്യം: യേശുവിന്റെ രൂപാന്തരീകരണം",
                    "Jesus is transfigured in divine light before Peter, James, and John on Mount Tabor.",
                    "ताबोर पर्वत पर येसु का चेहरा सूर्य की तरह चमक उठता है।",
                    "താബോർ മലയിൽ വെച്ച് യേശു രൂപാന്തരപ്പെടുന്നു.",
                    "Desire for Holiness", "ईश्वर की महिमा का दर्शन", "വിശുദ്ധിക്കായുള്ള ആഗ്രഹം"
                ),
                RosaryMystery(
                    type, 5,
                    "5th Luminous Mystery: Institution of Eucharist", "प्रकाश का पाँचवाँ भेद: परम प्रसाद की स्थापना", "അഞ്ചാം പ്രകാശ രഹസ്യം: വിശുദ്ധ കുർബാനയുടെ സ്ഥാപനം",
                    "At the Last Supper, Jesus offers His Body and Blood in the Holy Eucharist.",
                    "अंतिम बयारी में येसु अपने शरीर और रक्त को परम प्रसाद के रूप में अर्पित करते हैं।",
                    "അന്ത്യഅത്താഴവേളയിൽ യേശുക്രിസ്തു വിശുദ്ധ കുർബാന സ്ഥാപിക്കുന്നു.",
                    "Love for Holy Mass", "परम प्रसाद के प्रति भक्ति", "വിശുദ്ധ കുർബാനയോടുള്ള ഭക്തി"
                )
            )
        }
    }

    // Build complete sequence of 60 steps for the Rosary
    fun buildRosarySequence(mysteryType: MysteryType): List<RosaryBeadStep> {
        val steps = mutableListOf<RosaryBeadStep>()
        val mysteries = getMysteriesForType(mysteryType)

        // Step 0: Intro Prayer
        steps.add(
            RosaryBeadStep(
                stepIndex = 0, decadeIndex = 0, beadInDecade = 0,
                prayerType = PrayerType.INTRO_PRAYER,
                isLargeBead = true,
                labelEnglish = "Introduction",
                labelHindi = "प्रार्थना की शुरुआत",
                labelMalayalam = "പ്രാരംഭ പ്രാർത്ഥന"
            )
        )

        // Step 1: Sign of the Cross
        steps.add(
            RosaryBeadStep(
                stepIndex = 1, decadeIndex = 0, beadInDecade = 0,
                prayerType = PrayerType.SIGN_OF_THE_CROSS,
                isLargeBead = true,
                labelEnglish = "Sign of the Cross",
                labelHindi = "क्रूस का चिन्ह",
                labelMalayalam = "കുരിശടയാളം"
            )
        )

        // Step 2: Apostles' Creed (Crucifix)
        steps.add(
            RosaryBeadStep(
                stepIndex = 2, decadeIndex = 0, beadInDecade = 0,
                prayerType = PrayerType.APOSTLES_CREED,
                isLargeBead = true,
                labelEnglish = "Apostles' Creed (Crucifix)",
                labelHindi = "प्रेरितों का धर्मसार (क्रूस)",
                labelMalayalam = "വിശ്വാസപ്രമാണം"
            )
        )

        // Step 3: Our Father (First Large Bead)
        steps.add(
            RosaryBeadStep(
                stepIndex = 3, decadeIndex = 0, beadInDecade = 0,
                prayerType = PrayerType.OUR_FATHER,
                isLargeBead = true,
                labelEnglish = "Our Father (Intro)",
                labelHindi = "हे हमारे पिता (प्रारंभ)",
                labelMalayalam = "സ്വർഗ്ഗസ്ഥനായ ഞങ്ങളുടെ പിതാവേ"
            )
        )

        // Steps 4, 5, 6: 3 Hail Marys (Faith, Hope, Charity)
        steps.add(
            RosaryBeadStep(
                stepIndex = 4, decadeIndex = 0, beadInDecade = 1,
                prayerType = PrayerType.HAIL_MARY,
                labelEnglish = "Hail Mary (Faith)",
                labelHindi = "प्रणाम मरीया (विश्वास)",
                labelMalayalam = "നന്മ നിറഞ്ഞ മറിയമേ (വിശ്വാസം)"
            )
        )
        steps.add(
            RosaryBeadStep(
                stepIndex = 5, decadeIndex = 0, beadInDecade = 2,
                prayerType = PrayerType.HAIL_MARY,
                labelEnglish = "Hail Mary (Hope)",
                labelHindi = "प्रणाम मरीया (आशा)",
                labelMalayalam = "നന്മ നിറഞ്ഞ മറിയമേ (പ്രത്യാശ)"
            )
        )
        steps.add(
            RosaryBeadStep(
                stepIndex = 6, decadeIndex = 0, beadInDecade = 3,
                prayerType = PrayerType.HAIL_MARY,
                labelEnglish = "Hail Mary (Charity)",
                labelHindi = "प्रणाम मरीया (प्रेम)",
                labelMalayalam = "നന്മ നിറഞ്ഞ മറിയമേ (സ്നേഹം)"
            )
        )

        // Step 7: Glory Be (Intro)
        steps.add(
            RosaryBeadStep(
                stepIndex = 7, decadeIndex = 0, beadInDecade = 0,
                prayerType = PrayerType.GLORY_BE,
                labelEnglish = "Glory Be (Intro)",
                labelHindi = "पिता और पुत्र... (प्रारंभ)",
                labelMalayalam = "ത്രിത്വസ്തുതി (ആമുഖം)"
            )
        )

        // 5 Decades (Steps 8 to 57)
        var currentStep = 8
        for (decade in 1..5) {
            val mystery = mysteries[decade - 1]

            // Our Father & Mystery Announcement
            steps.add(
                RosaryBeadStep(
                    stepIndex = currentStep++,
                    decadeIndex = decade,
                    beadInDecade = 0,
                    prayerType = PrayerType.OUR_FATHER,
                    mysteryIndex = decade,
                    isLargeBead = true,
                    labelEnglish = "Decade $decade: ${mystery.englishTitle} - Our Father",
                    labelHindi = "भेद $decade: ${mystery.hindiTitle} - हे हमारे पिता",
                    labelMalayalam = "രഹസ്യം $decade: ${mystery.malayalamTitle} - സ്വർഗ്ഗസ്ഥനായ..."
                )
            )

            // 10 Hail Marys
            for (hail in 1..10) {
                steps.add(
                    RosaryBeadStep(
                        stepIndex = currentStep++,
                        decadeIndex = decade,
                        beadInDecade = hail,
                        prayerType = PrayerType.HAIL_MARY,
                        mysteryIndex = decade,
                        labelEnglish = "Decade $decade - Hail Mary #$hail",
                        labelHindi = "दशक $decade - प्रणाम मरीया #$hail",
                        labelMalayalam = "രഹസ്യം $decade - നന്മ നിറഞ്ഞ മറിയമേ #$hail"
                    )
                )
            }

            // Glory Be
            steps.add(
                RosaryBeadStep(
                    stepIndex = currentStep++,
                    decadeIndex = decade,
                    beadInDecade = 11,
                    prayerType = PrayerType.GLORY_BE,
                    mysteryIndex = decade,
                    labelEnglish = "Decade $decade - Glory Be",
                    labelHindi = "दशक $decade - पिता और पुत्र...",
                    labelMalayalam = "രഹസ്യം $decade - ത്രിത്വസ്തുതി"
                )
            )

            // Fatima Prayer
            steps.add(
                RosaryBeadStep(
                    stepIndex = currentStep++,
                    decadeIndex = decade,
                    beadInDecade = 12,
                    prayerType = PrayerType.FATIMA_PRAYER,
                    mysteryIndex = decade,
                    labelEnglish = "Decade $decade - Fatima Prayer",
                    labelHindi = "दशक $decade - ओ मेरे येसु...",
                    labelMalayalam = "രഹസ്യം $decade - ഓ എന്റെ ഈശോയേ..."
                )
            )
        }

        // Hail Holy Queen
        steps.add(
            RosaryBeadStep(
                stepIndex = currentStep++,
                decadeIndex = 6,
                beadInDecade = 0,
                prayerType = PrayerType.HAIL_HOLY_QUEEN,
                isLargeBead = true,
                labelEnglish = "Hail Holy Queen",
                labelHindi = "हे पवित्र रानी",
                labelMalayalam = "പരിശുദ്ധ രാജ്ഞീ"
            )
        )

        // Memorare
        steps.add(
            RosaryBeadStep(
                stepIndex = currentStep++,
                decadeIndex = 6,
                beadInDecade = 0,
                prayerType = PrayerType.MEMORARE,
                isLargeBead = true,
                labelEnglish = "The Memorare",
                labelHindi = "हे अति दयालु कुंवारी मरियम",
                labelMalayalam = "എത്രയും ദയയുള്ള മാതാവേ"
            )
        )

        // Litany of Loreto
        steps.add(
            RosaryBeadStep(
                stepIndex = currentStep++,
                decadeIndex = 6,
                beadInDecade = 0,
                prayerType = PrayerType.LITANY_OF_LORETO,
                isLargeBead = true,
                labelEnglish = "Litany of Loreto",
                labelHindi = "माता मरियम की लितनियाँ",
                labelMalayalam = "മാതാവിന്റെ ലുത്തിനിയ"
            )
        )

        // Concluding Prayer
        steps.add(
            RosaryBeadStep(
                stepIndex = currentStep++,
                decadeIndex = 6,
                beadInDecade = 0,
                prayerType = PrayerType.CONCLUDING_PRAYER,
                isLargeBead = true,
                labelEnglish = "Concluding Prayer",
                labelHindi = "समापन प्रार्थना",
                labelMalayalam = "സമാപന പ്രാർത്ഥന"
            )
        )

        // Final Sign of the Cross
        steps.add(
            RosaryBeadStep(
                stepIndex = currentStep++,
                decadeIndex = 6,
                beadInDecade = 0,
                prayerType = PrayerType.SIGN_OF_THE_CROSS,
                isLargeBead = true,
                labelEnglish = "Final Sign of the Cross",
                labelHindi = "अंतिम क्रूस का चिन्ह",
                labelMalayalam = "അവസാന കുരിശടയാളം"
            )
        )



        return steps
    }
}
