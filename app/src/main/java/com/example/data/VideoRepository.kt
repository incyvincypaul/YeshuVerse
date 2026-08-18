package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.DevotionalVideo
import com.example.model.LanguageEnum
import com.example.model.VideoCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class VideoRepository(private val context: Context? = null) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("VideoRepository", "Firestore not available: ${e.message}")
            null
        }
    }

    companion object {
        private val defaultVideos = listOf(
            DevotionalVideo(
                id = "default_live_rosary_hindi",
                title = "पवित्र रोज़री माला (Holy Rosary Hindi Live)",
                youtubeUrl = "https://www.youtube.com/watch?v=Nn1W8cZ2DqA",
                category = VideoCategory.ROSARY,
                language = LanguageEnum.HINDI,
                isLive = true,
                isFeatured = true,
                description = "दैनिक लाइव पवित्र रोज़री माला प्रार्थना एवं मनन चिंतन।"
            ),
            DevotionalVideo(
                id = "default_rosary_malayalam",
                title = "വിശുദ്ധ ജപമാല (Holy Rosary Malayalam)",
                youtubeUrl = "https://www.youtube.com/watch?v=Gk7F8QzG2pY",
                category = VideoCategory.ROSARY,
                language = LanguageEnum.MALAYALAM,
                isLive = false,
                isFeatured = true,
                description = "സമ്പൂർണ്ണ മലയാളം ജപമാല പ്രാർത്ഥനകൾ."
            ),
            DevotionalVideo(
                id = "default_divine_mercy",
                title = "ईश्वरीय करुणा की माला (Divine Mercy Chaplet)",
                youtubeUrl = "https://www.youtube.com/watch?v=p5TGfisOKMM",
                category = VideoCategory.ROSARY,
                language = LanguageEnum.HINDI,
                isLive = false,
                isFeatured = false,
                description = "अपरान्ह 3:00 बजे ईश्वरीय करुणा की शक्तिशाली प्रार्थना।"
            ),
            DevotionalVideo(
                id = "default_holy_mass_hindi",
                title = "दैनिक पवित्र मिस्सा बलिदान (Daily Holy Mass)",
                youtubeUrl = "https://www.youtube.com/watch?v=k4V3Dg0x9hE",
                category = VideoCategory.HOLY_MASS,
                language = LanguageEnum.HINDI,
                isLive = true,
                isFeatured = false,
                description = "पवित्र यूखरिस्त एवं सुसमाचार प्रवचन।"
            ),
            DevotionalVideo(
                id = "default_hymns_hindi",
                title = "यीशु मेरे साथ है (Best Hindi Christian Devotional Hymns)",
                youtubeUrl = "https://www.youtube.com/watch?v=wXhXbO5Q8x4",
                category = VideoCategory.HYMNS,
                language = LanguageEnum.HINDI,
                isLive = false,
                isFeatured = false,
                description = "हृदयस्पर्शी मसीही भजन एवं स्तुति आराधना।"
            ),
            DevotionalVideo(
                id = "default_hymns_malayalam",
                title = "പരിശുദ്ധ മറിയമേ ഭക്തിഗാനങ്ങൾ (Malayalam Marian Hymns)",
                youtubeUrl = "https://www.youtube.com/watch?v=v9U0kL1Zq8c",
                category = VideoCategory.HYMNS,
                language = LanguageEnum.MALAYALAM,
                isLive = false,
                isFeatured = false,
                description = "ഹൃദയസ്പർശിയായ മാതൃഭക്തി ഗാനങ്ങൾ."
            )
        )

        private val inMemoryVideos = MutableStateFlow<List<DevotionalVideo>>(defaultVideos)
    }

    init {
        loadFromLocalStorage()
    }

    private fun loadFromLocalStorage() {
        val ctx = context ?: return
        try {
            val prefs = ctx.getSharedPreferences("yeshuverse_videos_prefs", Context.MODE_PRIVATE)
            val jsonStr = prefs.getString("saved_videos_list", null)
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<DevotionalVideo>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val lang = try {
                        LanguageEnum.valueOf(obj.optString("language", LanguageEnum.HINDI.name))
                    } catch (e: Exception) {
                        LanguageEnum.HINDI
                    }
                    val cat = try {
                        VideoCategory.valueOf(obj.optString("category", VideoCategory.ROSARY.name))
                    } catch (e: Exception) {
                        VideoCategory.ROSARY
                    }
                    list.add(
                        DevotionalVideo(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            youtubeUrl = obj.optString("youtubeUrl"),
                            category = cat,
                            language = lang,
                            isLive = obj.optBoolean("isLive", false),
                            isFeatured = obj.optBoolean("isFeatured", false),
                            description = obj.optString("description", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    inMemoryVideos.value = list
                }
            }
        } catch (e: Exception) {
            Log.e("VideoRepository", "Error loading local videos: ${e.message}")
        }
    }

    private fun saveToLocalStorage(videos: List<DevotionalVideo>) {
        val ctx = context ?: return
        try {
            val array = JSONArray()
            videos.forEach { v ->
                val obj = JSONObject().apply {
                    put("id", v.id)
                    put("title", v.title)
                    put("youtubeUrl", v.youtubeUrl)
                    put("category", v.category.name)
                    put("language", v.language.name)
                    put("isLive", v.isLive)
                    put("isFeatured", v.isFeatured)
                    put("description", v.description)
                    put("timestamp", v.timestamp)
                }
                array.put(obj)
            }
            ctx.getSharedPreferences("yeshuverse_videos_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("saved_videos_list", array.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("VideoRepository", "Error saving to local storage: ${e.message}")
        }
    }

    fun observeVideos(): Flow<List<DevotionalVideo>> = callbackFlow {
        val db = firestore
        if (db == null) {
            val job = kotlinx.coroutines.MainScope().launch {
                inMemoryVideos.collect {
                    trySend(it)
                }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("devotional_videos")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("VideoRepository", "Firestore videos error: ${error.message}")
                        trySend(inMemoryVideos.value)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                val catName = doc.getString("category") ?: VideoCategory.ROSARY.name
                                val category = try { VideoCategory.valueOf(catName) } catch (e: Exception) { VideoCategory.ROSARY }
                                val langName = doc.getString("language") ?: LanguageEnum.HINDI.name
                                val language = try { LanguageEnum.valueOf(langName) } catch (e: Exception) { LanguageEnum.HINDI }

                                DevotionalVideo(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    youtubeUrl = doc.getString("youtubeUrl") ?: "",
                                    category = category,
                                    language = language,
                                    isLive = doc.getBoolean("isLive") ?: false,
                                    isFeatured = doc.getBoolean("isFeatured") ?: false,
                                    description = doc.getString("description") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (list.isNotEmpty()) {
                            inMemoryVideos.value = list
                            saveToLocalStorage(list)
                            trySend(list)
                        } else {
                            trySend(inMemoryVideos.value)
                        }
                    } else {
                        // If collection empty in firestore, initialize with default videos
                        defaultVideos.forEach { v ->
                            saveVideo(v)
                        }
                        trySend(inMemoryVideos.value)
                    }
                }
        } catch (e: Exception) {
            trySend(inMemoryVideos.value)
        }

        awaitClose {
            listener?.remove()
        }
    }

    fun saveVideo(video: DevotionalVideo) {
        val currentList = inMemoryVideos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == video.id }
        if (index >= 0) {
            currentList[index] = video
        } else {
            currentList.add(0, video)
        }
        inMemoryVideos.value = currentList
        saveToLocalStorage(currentList)

        val db = firestore ?: return
        try {
            val map = mapOf(
                "id" to video.id,
                "title" to video.title,
                "youtubeUrl" to video.youtubeUrl,
                "category" to video.category.name,
                "language" to video.language.name,
                "isLive" to video.isLive,
                "isFeatured" to video.isFeatured,
                "description" to video.description,
                "timestamp" to video.timestamp
            )
            db.collection("devotional_videos").document(video.id).set(map)
                .addOnSuccessListener {
                    Log.d("VideoRepository", "Video saved to Firestore: ${video.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("VideoRepository", "Failed to save video to Firestore: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("VideoRepository", "Firestore write error: ${e.message}")
        }
    }

    fun deleteVideo(videoId: String) {
        val currentList = inMemoryVideos.value.filter { it.id != videoId }
        inMemoryVideos.value = currentList
        saveToLocalStorage(currentList)

        val db = firestore ?: return
        try {
            db.collection("devotional_videos").document(videoId).delete()
                .addOnSuccessListener {
                    Log.d("VideoRepository", "Video deleted: $videoId")
                }
                .addOnFailureListener { e ->
                    Log.e("VideoRepository", "Failed to delete from Firestore: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("VideoRepository", "Error deleting video: ${e.message}")
        }
    }
}
