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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

                NavHost(
                    navController = navController,
                    startDestination = "home",
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

                    composable("watch_videos") {
                        val currentLang by viewModel.currentLanguage.collectAsState()
                        WatchVideosScreen(
                            viewModel = viewModel,
                            currentLanguage = currentLang,
                            onBackClick = {
                                navController.popBackStack()
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
