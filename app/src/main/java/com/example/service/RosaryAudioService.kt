package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.RosaryPrayers
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.PrayerType
import com.example.model.RosaryBeadStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class RosaryAudioService : Service(), TextToSpeech.OnInitListener {

    private val binder = LocalBinder()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var muteTimerJob: Job? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var isHostMode = false

    // Current Active Mode: true for Live session, false for Solo session
    private val _isLiveMode = MutableStateFlow(false)
    val isLiveMode: StateFlow<Boolean> = _isLiveMode

    // Unified Player state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex

    private val _currentLanguage = MutableStateFlow(LanguageEnum.HINDI)
    val currentLanguage: StateFlow<LanguageEnum> = _currentLanguage

    private val _currentMysteryType = MutableStateFlow(MysteryType.JOYFUL)
    val currentMysteryType: StateFlow<MysteryType> = _currentMysteryType

    // For backward compatibility until full refactor
    val liveIsPlaying = _isPlaying
    val liveStepIndex = _currentStepIndex
    val liveLanguage = _currentLanguage
    val liveMysteryType = _currentMysteryType

    private val _speechRate = MutableStateFlow(1.0f) // Natural speech rate
    val speechRate: StateFlow<Float> = _speechRate

    private val _pitch = MutableStateFlow(1.0f) // Natural pitch
    val pitch: StateFlow<Float> = _pitch

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isPlaying.value && !_isLiveMode.value) {
            speakCurrentStep()
        }
    }

    private var rosarySteps: List<RosaryBeadStep> = RosaryPrayers.buildRosarySequence(MysteryType.JOYFUL)

    inner class LocalBinder : Binder() {
        fun getService(): RosaryAudioService = this@RosaryAudioService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            val currentLang = _currentLanguage.value
            setupTtsLanguage(currentLang)
            
            tts?.setSpeechRate(_speechRate.value)
            tts?.setPitch(_pitch.value)
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    serviceScope.launch(Dispatchers.Main) {
                        if (_isPlaying.value) {
                            nextBead()
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}

                override fun onError(utteranceId: String?, errorCode: Int) {
                    super.onError(utteranceId, errorCode)
                }
            })

            if (_isPlaying.value) {
                speakCurrentStep()
            }
        } else {
            isTtsReady = false
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY -> play()
                ACTION_PAUSE -> pause()
                ACTION_NEXT -> nextBead()
                ACTION_PREV -> previousBead()
                ACTION_STOP -> stopService()
            }
        }
        return START_STICKY
    }

    fun setLiveMode(isLive: Boolean) {
        if (_isLiveMode.value != isLive) {
            pause()
            _isLiveMode.value = isLive
        }
    }

    fun setHostMode(isHost: Boolean) {
        this.isHostMode = isHost
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.5f, 2.0f)
        if (isTtsReady) {
            tts?.setSpeechRate(_speechRate.value)
        }
    }

    fun setPitch(p: Float) {
        _pitch.value = p.coerceIn(0.5f, 2.0f)
        if (isTtsReady) {
            tts?.setPitch(_pitch.value)
        }
    }

    fun setupLiveRosarySession(mysteryType: MysteryType, language: LanguageEnum, initialStep: Int = 0, seekFraction: Float = 0f) {
        _currentMysteryType.value = mysteryType
        val languageChanged = _currentLanguage.value != language
        _currentLanguage.value = language
        rosarySteps = RosaryPrayers.buildRosarySequence(mysteryType)
        
        val newStepIndex = initialStep.coerceIn(0, rosarySteps.lastIndex)
        val stepIndexChanged = _currentStepIndex.value != newStepIndex
        _currentStepIndex.value = newStepIndex
        
        if (languageChanged) {
            setupTtsLanguage(language)
        }
        
        updateNotification()
        
        if (_isPlaying.value && (languageChanged || stepIndexChanged)) {
            speakCurrentStep(seekFraction)
        }
    }

    fun setupRosarySession(mysteryType: MysteryType, language: LanguageEnum, initialStep: Int = 0) {
        _currentMysteryType.value = mysteryType
        val languageChanged = _currentLanguage.value != language
        _currentLanguage.value = language
        rosarySteps = RosaryPrayers.buildRosarySequence(mysteryType)
        
        val newStepIndex = initialStep.coerceIn(0, rosarySteps.lastIndex)
        val stepIndexChanged = _currentStepIndex.value != newStepIndex
        _currentStepIndex.value = newStepIndex
        
        if (languageChanged) {
            setupTtsLanguage(language)
        }
        
        updateNotification()
        
        if (_isPlaying.value && (languageChanged || stepIndexChanged)) {
            speakCurrentStep()
        }
    }

    private var isHindiTtsAvailable = false
    var isMalayalamTtsAvailable = false

    private fun setupTtsLanguage(language: LanguageEnum) {
        if (!isTtsReady) return
        val locale = when (language) {
            LanguageEnum.ENGLISH -> Locale("en", "IN")
            LanguageEnum.HINDI -> Locale("hi", "IN")
            LanguageEnum.MALAYALAM -> Locale("ml", "IN")
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.ENGLISH)
            isHindiTtsAvailable = false
            isMalayalamTtsAvailable = false
        } else {
            isHindiTtsAvailable = (language == LanguageEnum.HINDI)
            isMalayalamTtsAvailable = (language == LanguageEnum.MALAYALAM)
        }
    }

    private fun speakCurrentStep(seekFraction: Float = 0f) {
        if (!isTtsReady) return
        
        muteTimerJob?.cancel()

        val steps = rosarySteps
        val index = _currentStepIndex.value
        val lang = _currentLanguage.value
        val mysteryType = _currentMysteryType.value
        
        if (steps.isEmpty()) return
        val currentStep = steps.getOrNull(index) ?: return

        // Get Mystery Announcement if it's the start of a decade (Our Father step)
        val mystery = if (currentStep.mysteryIndex != null && currentStep.mysteryIndex > 0 && currentStep.beadInDecade == 0) {
            RosaryPrayers.getMysteriesForType(mysteryType)
                .getOrNull(currentStep.mysteryIndex - 1)
        } else null
        
        val mysteryTitle = if (mystery != null) {
            when (lang) {
                LanguageEnum.HINDI -> mystery.hindiTitle
                LanguageEnum.MALAYALAM -> mystery.malayalamTitle
                else -> mystery.englishTitle
            }
        } else null
        
        val prayerText = if (lang == LanguageEnum.HINDI && !isHindiTtsAvailable) {
            RosaryPrayers.getRomanizedHindiPrayerText(currentStep.prayerType)
        } else {
            RosaryPrayers.getPrayerText(currentStep.prayerType, lang)
        }

        val fullTextToSpeak = if (!mysteryTitle.isNullOrEmpty()) {
            "$mysteryTitle. $prayerText"
        } else {
            prayerText
        }
        
        var textToSpeak = fullTextToSpeak
        if (seekFraction > 0f && seekFraction < 1f) {
            val charIndex = (fullTextToSpeak.length * seekFraction).toInt()
            val nextSpace = fullTextToSpeak.indexOf(' ', charIndex)
            textToSpeak = if (nextSpace != -1) {
                fullTextToSpeak.substring(nextSpace).trim()
            } else {
                fullTextToSpeak.substring(charIndex)
            }
        }
        
        val vol = if (_isMuted.value) 0.0f else 1.0f
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "rosary_step_$index")
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, vol)
        }
        
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "rosary_step_$index")
    }

    fun play(seekFraction: Float = 0f) {
        if (rosarySteps.isEmpty()) return
        _isPlaying.value = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, buildNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }
        speakCurrentStep(seekFraction)
    }

    fun pause() {
        _isPlaying.value = false
        muteTimerJob?.cancel()
        tts?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun jumpToStep(stepIndex: Int) {
        muteTimerJob?.cancel()
        val newIndex = stepIndex.coerceIn(0, rosarySteps.lastIndex)
        val hasChanged = _currentStepIndex.value != newIndex
        _currentStepIndex.value = newIndex
        updateNotification()
        if (_isPlaying.value) {
            speakCurrentStep()
        } else if (hasChanged) {
            tts?.stop()
        }
    }

    fun nextBead() {
        if (_currentStepIndex.value < rosarySteps.lastIndex) {
            _currentStepIndex.value++
            updateNotification()
            if (_isPlaying.value) {
                speakCurrentStep()
            }
        } else {
            pause()
        }
    }

    fun previousBead() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value--
            updateNotification()
            if (_isPlaying.value) {
                speakCurrentStep()
            }
        }
    }

    private fun stopService() {
        pause()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Rosary Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live rosary prayer progress and controls"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): android.app.Notification {
        val steps = rosarySteps
        val index = _currentStepIndex.value
        val lang = _currentLanguage.value
        val mysteryType = _currentMysteryType.value
        val isPlayingVal = _isPlaying.value

        val currentStep = if (steps.isNotEmpty() && index in steps.indices) {
            steps[index]
        } else null

        val titleText = currentStep?.let {
            when (lang) { LanguageEnum.HINDI -> it.labelHindi; LanguageEnum.MALAYALAM -> it.labelMalayalam; else -> it.labelEnglish }
        } ?: "Live Rosary Prayer"

        val mysteryText = when (lang) { LanguageEnum.HINDI -> mysteryType.hindiTitle; LanguageEnum.MALAYALAM -> mysteryType.malayalamTitle; else -> mysteryType.englishTitle }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlayingVal) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pause",
                getServicePendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Play",
                getServicePendingIntent(ACTION_PLAY)
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("YeshuVerse Live Rosary • $mysteryText")
            .setContentText(titleText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous, "Prev",
                    getServicePendingIntent(ACTION_PREV)
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next, "Next",
                    getServicePendingIntent(ACTION_NEXT)
                )
            )
            .build()
    }

    private fun getServicePendingIntent(action: String): PendingIntent {
        val intent = Intent(this, RosaryAudioService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        muteTimerJob?.cancel()
        serviceJob.cancel()
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        const val CHANNEL_ID = "rosary_live_audio_channel"
        const val NOTIFICATION_ID = 1008

        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREV = "action_prev"
        const val ACTION_STOP = "action_stop"
    }
}
