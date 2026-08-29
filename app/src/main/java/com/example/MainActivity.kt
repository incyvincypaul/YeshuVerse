package com.example

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.service.RosaryAudioService
import com.example.ui.dialogs.FirebaseSetupDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveRoomScreen
import com.example.ui.screens.NovenaScreen
import com.example.ui.screens.SoloPrayerScreen
import com.example.ui.screens.WatchVideosScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.RosaryViewModel
import com.example.viewmodel.SoloRosaryViewModel

import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {

    private val viewModel: RosaryViewModel by viewModels()
    private val soloViewModel: SoloRosaryViewModel by viewModels()
    private var audioService: RosaryAudioService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RosaryAudioService.LocalBinder
            audioService = binder.getService()
            audioService?.let {
                viewModel.bindAudioService(it)
                soloViewModel.bindAudioService(it)
            }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
    }

    private fun extractDeepLinkVideoId(intent: Intent?): String? {
        val data = intent?.data ?: return null
        // Case 1: yeshuverse://video?id=VIDEO_ID or yeshuverse://video/VIDEO_ID
        if (data.scheme == "yeshuverse") {
            val queryId = data.getQueryParameter("id")
            if (!queryId.isNullOrBlank()) return queryId
            val path = data.path?.trimStart('/')
            if (!path.isNullOrBlank()) return path
        }
        // Case 2: https://yeshuverse.app/video/VIDEO_ID or https://yeshuverse.app/video?id=VIDEO_ID
        if (data.host == "yeshuverse.app") {
            val queryId = data.getQueryParameter("id")
            if (!queryId.isNullOrBlank()) return queryId
            val pathSegments = data.pathSegments
            if (pathSegments.size >= 2 && pathSegments[0] == "video") {
                return pathSegments[1]
            }
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLinkVideoId = extractDeepLinkVideoId(intent)

        // Request POST_NOTIFICATIONS permission dynamically on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // Start and bind RosaryAudioService for background playback
        val intent = Intent(this, RosaryAudioService::class.java)
        try {
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                val startDest = if (!deepLinkVideoId.isNullOrBlank()) {
                    "watch_videos?videoId=$deepLinkVideoId"
                } else {
                    "home"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDest,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        viewModel.setLiveSyncEnabled(false)
                        HomeScreen(
                            viewModel = viewModel,
                            onJoinLiveRoom = {
                                viewModel.setLiveSyncEnabled(true)
                                navController.navigate("live_room")
                            },
                            onStartSoloPrayer = {
                                viewModel.setLiveSyncEnabled(false)
                                navController.navigate("solo_prayer")
                            },
                            onOpenNovena = {
                                navController.navigate("novena")
                            },
                            onOpenWatchVideos = {
                                navController.navigate("watch_videos")
                            },
                            onOpenAdmin = {
                                navController.navigate("admin_panel")
                            },
                            onOpenAbout = {
                                navController.navigate("about")
                            }
                        )
                    }

                    composable(
                        route = "watch_videos?videoId={videoId}",
                        arguments = listOf(
                            navArgument("videoId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val initialVidId = backStackEntry.arguments?.getString("videoId")
                        val currentLang by viewModel.currentLanguage.collectAsState()
                        WatchVideosScreen(
                            viewModel = viewModel,
                            currentLanguage = currentLang,
                            initialVideoId = initialVidId,
                            onBackClick = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            },
                            onOpenAdmin = {
                                navController.navigate("admin_panel")
                            }
                        )
                    }

                    composable("about") {
                        val currentLang by viewModel.currentLanguage.collectAsState()
                        AboutScreen(
                            currentLanguage = currentLang,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onOpenAdmin = {
                                navController.navigate("admin_panel")
                            }
                        )
                    }

                    composable("novena") {
                        val currentLang by viewModel.currentLanguage.collectAsState()
                        NovenaScreen(
                            currentLanguage = currentLang,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("live_room") {
                        LiveRoomScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.setLiveSyncEnabled(false)
                                navController.popBackStack()
                            },
                            onAdminClick = {
                                navController.navigate("admin_panel")
                            },
                            onStartSoloPrayer = {
                                viewModel.setLiveSyncEnabled(false)
                                navController.navigate("solo_prayer")
                            }
                        )
                    }
                    
                    composable("solo_prayer") {
                        SoloPrayerScreen(
                            viewModel = soloViewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("admin_panel") {
                        AdminPanelScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}
