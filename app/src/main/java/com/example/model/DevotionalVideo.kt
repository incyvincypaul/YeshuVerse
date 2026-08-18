package com.example.model

import java.util.UUID
import java.util.regex.Pattern

enum class VideoCategory(
    val englishName: String,
    val hindiName: String,
    val malayalamName: String
) {
    ALL("All", "सभी", "എല്ലാം"),
    LIVE_STREAM("Live Stream", "🔴 लाइव प्रसारण", "🔴 തത്സമയം"),
    ROSARY("Holy Rosary", "📿 पवित्र रोज़री", "📿 പരിശുദ്ധ ജപമാല"),
    HOLY_MASS("Holy Mass", "✝️ पवित्र मिस्सा", "✝️ വിശുദ്ധ കുർബാന"),
    HYMNS("Devotional Hymns", "🎵 भक्ति गीत व भजन", "🎵 ഭക്തിഗാനങ്ങൾ"),
    SERMON("Gospel & Teachings", "📖 सुसमाचार व प्रवचन", "📖 വചനസന്ദേശം"),
    NOVENA("Novena Prayers", "🙏 नोवेना प्रार्थनाएं", "🙏 നൊവേന പ്രാർത്ഥനകൾ")
}

data class DevotionalVideo(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val youtubeUrl: String = "",
    val category: VideoCategory = VideoCategory.ROSARY,
    val language: LanguageEnum = LanguageEnum.HINDI,
    val isLive: Boolean = false,
    val isFeatured: Boolean = false,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Extracts YouTube Video ID from various URL formats:
     * - https://www.youtube.com/watch?v=dQw4w9WgXcQ
     * - https://youtu.be/dQw4w9WgXcQ
     * - https://www.youtube.com/live/dQw4w9WgXcQ
     * - https://www.youtube.com/embed/dQw4w9WgXcQ
     * - dQw4w9WgXcQ (direct ID)
     */
    val videoId: String
        get() = extractYouTubeVideoId(youtubeUrl)

    val thumbnailUrl: String
        get() {
            val vId = videoId
            return if (vId.isNotBlank()) {
                "https://img.youtube.com/vi/$vId/hqdefault.jpg"
            } else {
                ""
            }
        }

    val maxResThumbnailUrl: String
        get() {
            val vId = videoId
            return if (vId.isNotBlank()) {
                "https://img.youtube.com/vi/$vId/maxresdefault.jpg"
            } else {
                ""
            }
        }

    companion object {
        fun extractYouTubeVideoId(url: String): String {
            val trimmed = url.trim()
            if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains("?") && !trimmed.contains(".")) {
                return trimmed
            }
            val pattern = "(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/(?:watch\\?v=|embed\\/|v\\/|live\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
            val matcher = Pattern.compile(pattern).matcher(trimmed)
            return if (matcher.find()) {
                matcher.group(1) ?: ""
            } else {
                // Fallback for query param 'v'
                if (trimmed.contains("v=")) {
                    val startIndex = trimmed.indexOf("v=") + 2
                    val endIndex = trimmed.indexOf("&", startIndex).let { if (it != -1) it else trimmed.length }
                    val possibleId = trimmed.substring(startIndex, endIndex)
                    if (possibleId.length == 11) possibleId else ""
                } else {
                    ""
                }
            }
        }
    }
}
