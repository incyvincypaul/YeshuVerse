package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.DevotionalVideo
import com.example.model.LanguageEnum

private val Gold = Color(0xFFD4AF37)
private val TextWhite = Color(0xFFFFFFFF)
private val DarkBg = Color(0xFF090D14)
private val BorderGold = Color(0xFFD4AF37).copy(alpha = 0.5f)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerDialog(
    video: DevotionalVideo,
    currentLanguage: LanguageEnum,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isReloading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            webViewInstance?.destroy()
            onDismiss()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        containerColor = DarkBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (video.isLive) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE50914),
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = "🔴 LIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Strict Locked",
                                    tint = Gold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        LanguageEnum.HINDI -> "सुरक्षित भक्ति दर्शन"
                                        LanguageEnum.MALAYALAM -> "സുരക്ഷിത ദർശനം"
                                        else -> "Devotional Mode"
                                    },
                                    color = Gold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.title,
                        color = Gold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }

                IconButton(
                    onClick = {
                        webViewInstance?.destroy()
                        onDismiss()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextWhite)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                // Strict Locked Player Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, BorderGold, RoundedCornerShape(12.dp))
                ) {
                    val videoId = video.videoId
                    if (videoId.isNotBlank()) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewInstance = this
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    setBackgroundColor(android.graphics.Color.BLACK)

                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        mediaPlaybackRequiresUserGesture = false
                                        loadWithOverviewMode = true
                                        useWideViewPort = true
                                        setSupportMultipleWindows(false)
                                        javaScriptCanOpenWindowsAutomatically = false
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                    }

                                    webChromeClient = WebChromeClient()

                                    // Strict URL Interceptor: Blocks all external navigation, channel links, and searches
                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            val url = request?.url?.toString() ?: ""
                                            // Allow strictly internal embed and streaming assets
                                            if (url.contains("youtube.com/embed/$videoId") ||
                                                url.contains("googlevideo.com") ||
                                                url.contains("youtube-nocookie.com")
                                            ) {
                                                return false
                                            }
                                            // Block any navigation to youtube.com/watch, search, other videos or channels
                                            return true
                                        }

                                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                            super.onPageStarted(view, url, favicon)
                                            isReloading = false
                                        }
                                    }

                                    val secureHtml = generateSecurePlayerHtml(videoId)
                                    loadDataWithBaseURL("https://www.youtube-nocookie.com", secureHtml, "text/html", "UTF-8", null)
                                }
                            },
                            update = { webView ->
                                webViewInstance = webView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Invisible Top Touch Shield: Blocks clicks on the top 45dp (YouTube title / Watch on YouTube links)
                        // while keeping media control bar at the bottom 100% interactive!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .align(Alignment.TopCenter)
                                .clickable(enabled = false) {}
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (currentLanguage) {
                                    LanguageEnum.HINDI -> "वीडियो उपलब्ध नहीं है"
                                    LanguageEnum.MALAYALAM -> "വീഡിയോ ലഭ്യമല്ല"
                                    else -> "Video not available"
                                },
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Security Note Banner
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Safe Devotional",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "पवित्र ध्यान मोड: कोई विज्ञापन या बाहरी वीडियो नहीं खुलेगा।"
                                LanguageEnum.MALAYALAM -> "ഭക്തി ശ്രദ്ധ മോഡ്: പരസ്യങ്ങളോ മറ്റ് വീഡിയോകളോ ഉണ്ടാകില്ല."
                                else -> "Sacred Focus Mode: Distraction-free playback without external links."
                            },
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (video.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = video.description,
                        color = TextWhite.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons: Replay + Share (No external YouTube link button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val videoId = video.videoId
                            if (videoId.isNotBlank()) {
                                isReloading = true
                                val html = generateSecurePlayerHtml(videoId)
                                webViewInstance?.loadDataWithBaseURL(
                                    "https://www.youtube-nocookie.com",
                                    html,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF1E283A),
                            contentColor = Gold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Replay",
                            modifier = Modifier.size(16.dp),
                            tint = Gold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "पुनः चलाएं"
                                LanguageEnum.MALAYALAM -> "വീണ്ടും കാണുക"
                                else -> "Replay"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            shareVideo(context, video)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF1E283A),
                            contentColor = TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4A6B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp),
                            tint = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                LanguageEnum.HINDI -> "शेयर करें"
                                LanguageEnum.MALAYALAM -> "പങ്കിടുക"
                                else -> "Share"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    webViewInstance?.destroy()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when (currentLanguage) {
                        LanguageEnum.HINDI -> "बंद करें"
                        LanguageEnum.MALAYALAM -> "അടയ്ക്കുക"
                        else -> "Close"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

/**
 * Generates an isolated HTML page using YouTube nocookie embed with strict parameters
 * preventing external navigations, related videos from outside, and search popups.
 */
private fun generateSecurePlayerHtml(videoId: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                body, html {
                    width: 100%;
                    height: 100%;
                    background-color: #000000;
                    overflow: hidden;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    user-select: none;
                    -webkit-user-select: none;
                }
                .video-wrapper {
                    position: relative;
                    width: 100%;
                    height: 100%;
                }
                iframe {
                    border: 0;
                    width: 100%;
                    height: 100%;
                    position: absolute;
                    top: 0;
                    left: 0;
                }
            </style>
        </head>
        <body>
            <div class="video-wrapper">
                <iframe 
                    src="https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&playsinline=1&rel=0&modestbranding=1&controls=1&iv_load_policy=3&disablekb=1&fs=1" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                    sandbox="allow-scripts allow-same-origin allow-presentation"
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
    """.trimIndent()
}

fun shareVideo(context: Context, video: DevotionalVideo) {
    try {
        val videoId = if (video.videoId.isNotBlank()) video.videoId else DevotionalVideo.extractYouTubeVideoId(video.youtubeUrl)
        val appDeepLink = "https://yeshuverse.app/video/$videoId"
        val customSchemeLink = "yeshuverse://video?id=$videoId"
        val youtubeFallback = if (video.youtubeUrl.isNotBlank()) video.youtubeUrl else "https://youtu.be/$videoId"

        val shareMessage = buildString {
            append("✝️ *${video.title}*\n\n")
            if (video.description.isNotBlank()) {
                append("${video.description}\n\n")
            }
            append("📱 *YeshuVerse ऐप में सीधे देखें (विज्ञापन और डिस्ट्रेक्शन-फ्री):*\n")
            append("$appDeepLink\n\n")
            append("▶️ अगर ऐप नहीं है तो यहाँ देखें:\n")
            append(youtubeFallback)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, video.title)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Devotional Video"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

