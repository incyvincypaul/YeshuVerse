package com.example.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.YeshuVerseApplication
import com.example.data.AppLanguageManager
import com.example.data.RosaryPrayers
import com.example.data.FirebaseSyncRepository
import com.example.data.database.UserProgress
import com.example.model.LanguageEnum
import com.example.model.LiveRoomState
import com.example.model.MysteryType
import com.example.model.RosaryBeadStep
import com.example.service.RosaryAudioService
import com.example.ui.screens.LeaderNames
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class RosaryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as YeshuVerseApplication).database
    private val userProgressDao = database.userProgressDao()
    private val repository = FirebaseSyncRepository()

    private val _roomState = MutableStateFlow(
        LiveRoomState(
            currentMysteryType = RosaryPrayers.getTodayDefaultMystery()
        )
    )
    val roomState: StateFlow<LiveRoomState> = _roomState.asStateFlow()

    private val _isHostMode = MutableStateFlow(false)
    val isHostMode: StateFlow<Boolean> = _isHostMode.asStateFlow()

    private val _isLiveSyncEnabled = MutableStateFlow(false)
    val isLiveSyncEnabled: StateFlow<Boolean> = _isLiveSyncEnabled.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguageManager.currentLanguage.value)
    val currentLanguage: StateFlow<LanguageEnum> = _currentLanguage.asStateFlow()

    private val _rosarySequence = MutableStateFlow<List<RosaryBeadStep>>(emptyList())
    val rosarySequence: StateFlow<List<RosaryBeadStep>> = _rosarySequence.asStateFlow()

    private val _showFirebaseDialog = MutableStateFlow(false)
    val showFirebaseDialog: StateFlow<Boolean> = _showFirebaseDialog.asStateFlow()

    private val _selectedTopTab = MutableStateFlow(0) // 0: Live Community, 1: Private Devotions
    val selectedTopTab: StateFlow<Int> = _selectedTopTab.asStateFlow()

    private val _selectedSubTab = MutableStateFlow(1) // 0: Community Altar, 1: Private Devotion
    val selectedSubTab: StateFlow<Int> = _selectedSubTab.asStateFlow()

    private val _litCandlesCount = MutableStateFlow(0) // default matches 0 candles
    val litCandlesCount: StateFlow<Int> = _litCandlesCount.asStateFlow()

    private val _participantCount = MutableStateFlow(384)
    val participantCount: StateFlow<Int> = _participantCount.asStateFlow()

    private val _showPrayingCount = MutableStateFlow(true)
    val showPrayingCount: StateFlow<Boolean> = _showPrayingCount.asStateFlow()

    private val _prayerIntention = MutableStateFlow("")
    val prayerIntention: StateFlow<String> = _prayerIntention.asStateFlow()

    private val _isLocallyPaused = MutableStateFlow(false)
    val isLocallyPaused: StateFlow<Boolean> = _isLocallyPaused.asStateFlow()

    private val _savedUserName = MutableStateFlow("")
    val savedUserName: StateFlow<String> = _savedUserName.asStateFlow()

    fun setSavedUserName(name: String) {
        if (name.isNotBlank()) {
            _savedUserName.value = name.trim()
        }
    }

    fun claimLeaderSlot(sessionName: String, leaderName: String) {
        val cleanName = leaderName.trim().ifBlank { "Prayer Leader" }
        setSavedUserName(cleanName)
        val currentSched = (_schedule.value ?: com.example.model.RosarySchedule()).getSanitizedSchedule()
        val newClaims = currentSched.slotClaims.toMutableMap()
        newClaims[sessionName] = cleanName
        val updatedSched = currentSched.copy(
            slotClaims = newClaims,
            claimsDate = currentSched.getTodayDateString()
        )
        _schedule.value = updatedSched
        repository.saveSchedule(updatedSched)

        val active = updatedSched.getCurrentActiveSession()
        if (active != null && active.name == sessionName) {
            val updatedRoom = _roomState.value.copy(hostName = cleanName)
            _roomState.value = updatedRoom
            setHostMode(true)
            repository.updateLiveRoomState(updatedRoom)
        }
    }

    fun cancelLeaderSlot(sessionName: String) {
        val currentSched = _schedule.value ?: com.example.model.RosarySchedule()
        val newClaims = currentSched.slotClaims.toMutableMap()
        newClaims.remove(sessionName)
        val updatedSched = currentSched.copy(slotClaims = newClaims)
        _schedule.value = updatedSched
        repository.saveSchedule(updatedSched)

        val active = updatedSched.getCurrentActiveSession()
        if (active != null && active.name == sessionName) {
            val defaultHost = updatedSched.getEffectiveHostForSession(sessionName)
            val updatedRoom = _roomState.value.copy(hostName = defaultHost)
            _roomState.value = updatedRoom
            setHostMode(false)
            repository.updateLiveRoomState(updatedRoom)
        }
    }

    fun setPrayerIntention(intention: String) {
        _prayerIntention.value = intention
    }

    // Solo Persistence
    fun saveSoloProgress(stepIndex: Int, mysteryType: MysteryType) {
        viewModelScope.launch {
            userProgressDao.saveProgress(
                UserProgress(id = 0, lastStepIndex = stepIndex, mysteryType = mysteryType.name)
            )
        }
    }

    fun setSelectedTopTab(tab: Int) {
        _selectedTopTab.value = tab
        _selectedSubTab.value = tab
    }

    fun setSelectedSubTab(tab: Int) {
        _selectedSubTab.value = tab
    }

    fun lightCandle() {
        if (_litCandlesCount.value < 4) {
            _litCandlesCount.value += 1
        }
    }

    private val _audioService = MutableStateFlow<RosaryAudioService?>(null)
    val audioService: StateFlow<RosaryAudioService?> = _audioService.asStateFlow()
    private var serviceStepObservationJob: Job? = null
    private var servicePlayObservationJob: Job? = null

    private val _schedule = MutableStateFlow<com.example.model.RosarySchedule?>(null)
    val schedule: StateFlow<com.example.model.RosarySchedule?> = _schedule.asStateFlow()

    private val _nextUpcomingSession = MutableStateFlow<com.example.model.SessionItem?>(null)
    val nextUpcomingSession: StateFlow<com.example.model.SessionItem?> = _nextUpcomingSession.asStateFlow()
    
    private val _loopWaitTimeRemaining = MutableStateFlow(0)
    val loopWaitTimeRemaining: StateFlow<Int> = _loopWaitTimeRemaining.asStateFlow()


    private val _activeSession = MutableStateFlow<com.example.model.SessionItem?>(null)
    val activeSession: StateFlow<com.example.model.SessionItem?> = _activeSession.asStateFlow()

    init {
        // Initialize sequence with the current roomState's default mystery
        updateRosarySequence(_roomState.value.currentMysteryType)

        // Observe Schedule settings from Firestore & run periodic schedule check loop
        viewModelScope.launch {
            repository.getSchedule().collect { sched ->
                val rawSched = sched ?: com.example.model.RosarySchedule()
                val sanitized = rawSched.getSanitizedSchedule()
                if (sanitized != rawSched) {
                    repository.saveSchedule(sanitized)
                }
                _schedule.value = sanitized
                _showPrayingCount.value = sanitized.showPrayingCount
                checkScheduleActive()
            }
        }

        viewModelScope.launch {
            while (isActive) {
                checkScheduleActive()
                delay(1000L)
            }
        }

        // Load solo progress
        viewModelScope.launch {
            val savedProgress = userProgressDao.getProgress().first()
            if (savedProgress != null) {
                // We don't restore automatically, but we hold it for when user enters Solo mode
                // This is a design decision for simplicity.
            }
        }
        // Start Live Chat Simulator
        viewModelScope.launch {
            LiveChatSimulator.startSimulation(_currentLanguage, _participantCount)
        }

        viewModelScope.launch {
            AppLanguageManager.currentLanguage.collect { lang ->
                if (_currentLanguage.value != lang) {
                    _currentLanguage.value = lang
                    if (_isLiveSyncEnabled.value) {
                        val updated = _roomState.value.copy(language = lang)
                        _roomState.value = updated
                        _audioService.value?.setupLiveRosarySession(updated.currentMysteryType, lang, updated.currentStepIndex, getLiveSeekFraction(updated))
                    } else {
                        _audioService.value?.let { service ->
                            service.setupRosarySession(service.currentMysteryType.value, lang, service.currentStepIndex.value)
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val period = 10 * 60 * 1000L // 10 minutes
                val cycle = now / period
                val progress = (now % period).toDouble() / period
                
                val currentSched = _schedule.value ?: com.example.model.RosarySchedule()
                val baseVal = currentSched.basePrayingCount
                val minFluc = currentSched.minPrayingCount.coerceAtLeast(0)
                val maxFluc = currentSched.maxPrayingCount.coerceAtLeast(minFluc)
                val flucRange = (maxFluc - minFluc).coerceAtLeast(0)

                val fluc = if (flucRange > 0) {
                    val randStart = java.util.Random(cycle)
                    val randEnd = java.util.Random(cycle + 1)
                    val startVal = minFluc + randStart.nextInt(flucRange + 1)
                    val endVal = minFluc + randEnd.nextInt(flucRange + 1)
                    val smoothProgress = (1.0 - Math.cos(progress * Math.PI)) / 2.0
                    (startVal + (endVal - startVal) * smoothProgress).toInt()
                } else {
                    minFluc
                }
                
                val noiseRand = java.util.Random(now / 5000L) // changes every 5s
                val noise = noiseRand.nextInt(6) - 3 // -3 to +2
                
                _participantCount.value = (baseVal + fluc + noise).coerceAtLeast(0)
                delay(5000L)
            }
        }


        // Observe real-time state from repository
        viewModelScope.launch {
            repository.observeLiveRoom().collect { newState ->
                val currentSched = _schedule.value ?: com.example.model.RosarySchedule()
                val isCurrentlyLive = currentSched.getCurrentActiveSession() != null
                
                // Only sync with Firestore if Live Sync is enabled
                if (_isLiveSyncEnabled.value) {
                    _currentLanguage.value = newState.language
                    updateRosarySequence(newState.currentMysteryType)
                    
                    if (!_isHostMode.value) {
                        val currentLocalStep = _audioService.value?.currentStepIndex?.value ?: _roomState.value.currentStepIndex
                        val isAudioPlaying = _audioService.value?.isPlaying?.value == true
                        val targetStep = if (isCurrentlyLive) {
                            val elapsed = currentSched.getElapsedSecondsForActiveSession() ?: 0
                            calculateStepIndexForElapsed(elapsed, _rosarySequence.value)
                        } else {
                            newState.currentStepIndex
                        }

                        // Internal Check: avoid interrupting playback if local step is within 1 bead of live target
                        val isDrifting = kotlin.math.abs(currentLocalStep - targetStep) > 1
                        val mysteryChanged = _roomState.value.currentMysteryType != newState.currentMysteryType

                        val stepToApply = if (!isAudioPlaying || isDrifting || mysteryChanged) targetStep else currentLocalStep

                        val mergedRoom = newState.copy(
                            currentStepIndex = stepToApply,
                            isPlaying = newState.isPlaying,
                            isLive = true
                        )
                        _roomState.value = mergedRoom
                        
                        if (!_isLocallyPaused.value) {
                            _audioService.value?.let { service ->
                                val wasPlaying = service.isPlaying.value
                                if (isDrifting || mysteryChanged || !wasPlaying) {
                                    service.setupLiveRosarySession(
                                        mergedRoom.currentMysteryType,
                                        mergedRoom.language,
                                        stepToApply,
                                        getLiveSeekFraction(mergedRoom)
                                    )
                                }
                                if (mergedRoom.isPlaying && !wasPlaying) {
                                    service.play(getLiveSeekFraction(mergedRoom))
                                } else if (!mergedRoom.isPlaying && wasPlaying) {
                                    service.pause()
                                }
                            }
                        }
                    } else {
                        // Host mode: keep our local/Firestore state
                        _roomState.value = newState
                    }
                } else {
                    // When not joined yet, keep the preview screen's mystery/language/participants in sync with Firestore,
                    // and internal check sets the target step so joining connects instantly without confusion!
                    if (isCurrentlyLive) {
                        val elapsed = currentSched.getElapsedSecondsForActiveSession() ?: 0
                        val targetStep = calculateStepIndexForElapsed(elapsed, _rosarySequence.value)
                        _roomState.value = newState.copy(
                            currentStepIndex = targetStep,
                            isLive = true
                        )
                    } else {
                        _roomState.value = newState
                    }
                }
            }
        }
    }

    fun bindAudioService(service: RosaryAudioService) {
        _audioService.value = service
        val currentState = _roomState.value
        service.setLiveMode(_isLiveSyncEnabled.value)
        service.setHostMode(_isHostMode.value || !_isLiveSyncEnabled.value)
        if (_isLiveSyncEnabled.value) {
            service.setupLiveRosarySession(currentState.currentMysteryType, currentState.language, currentState.currentStepIndex, getLiveSeekFraction(currentState))
            if (currentState.isPlaying && !_isLocallyPaused.value && !service.isPlaying.value) {
                service.play(getLiveSeekFraction(currentState))
            }
        } else {
            service.setupRosarySession(currentState.currentMysteryType, currentState.language, currentState.currentStepIndex)
        }
        
        // Observe service's step index to sync with Firebase / local state
        serviceStepObservationJob?.cancel()
        serviceStepObservationJob = viewModelScope.launch {
            service.currentStepIndex.collect { stepIndex ->
                if (_isLiveSyncEnabled.value) {
                    if (_roomState.value.currentStepIndex != stepIndex) {
                        val updated = _roomState.value.copy(currentStepIndex = stepIndex)
                        _roomState.value = updated
                        if (_isHostMode.value) {
                            repository.updateLiveRoomState(updated)
                        }
                    }
                }
            }
        }

        // Observe service's playing state to sync with Firebase / local state
        servicePlayObservationJob?.cancel()
        servicePlayObservationJob = viewModelScope.launch {
            service.isPlaying.collect { isPlaying ->
                if (_isLiveSyncEnabled.value) {
                    if (_roomState.value.isPlaying != isPlaying) {
                        val updated = _roomState.value.copy(isPlaying = isPlaying)
                        _roomState.value = updated
                        if (_isHostMode.value) {
                            repository.updateLiveRoomState(updated)
                        }
                    }
                }
            }
        }
    }

    private fun updateRosarySequence(mysteryType: MysteryType) {
        _rosarySequence.value = RosaryPrayers.buildRosarySequence(mysteryType)
    }

    fun setLiveSyncEnabled(enabled: Boolean) {
        _isLiveSyncEnabled.value = enabled
        _audioService.value?.setLiveMode(enabled)
        _audioService.value?.setHostMode(_isHostMode.value || !enabled)
        
        if (enabled) {
            val currentState = _roomState.value
            _currentLanguage.value = currentState.language
            updateRosarySequence(currentState.currentMysteryType)
            
            _audioService.value?.let { service ->
                service.setupLiveRosarySession(currentState.currentMysteryType, currentState.language, currentState.currentStepIndex, getLiveSeekFraction(currentState))
                if (currentState.isPlaying) {
                    service.play(getLiveSeekFraction(currentState))
                } else {
                    service.pause()
                }
            }
        } else {
            // Turning off sync, user is lead of local audio
            _audioService.value?.setHostMode(true)
        }
    }

    fun setHostMode(enabled: Boolean) {
        _isHostMode.value = enabled
        _audioService.value?.setHostMode(enabled || !_isLiveSyncEnabled.value)
    }

    fun toggleHostMode() {
        val nextMode = !_isHostMode.value
        setHostMode(nextMode)
    }

    fun takeHostRole(hostName: String) {
        val updated = _roomState.value.copy(hostName = hostName)
        _roomState.value = updated
        setHostMode(true)
        repository.updateLiveRoomState(updated)
    }

    fun setLanguage(language: LanguageEnum) {
        _currentLanguage.value = language
        AppLanguageManager.setLanguage(getApplication(), language)
        if (_isLiveSyncEnabled.value) {
            val updated = _roomState.value.copy(language = language)
            _roomState.value = updated
            if (_isHostMode.value) {
                repository.updateLiveRoomState(updated)
            }
            _audioService.value?.setupLiveRosarySession(updated.currentMysteryType, language, updated.currentStepIndex, getLiveSeekFraction(updated))
        } else {
            _audioService.value?.let { service ->
                service.setupRosarySession(service.currentMysteryType.value, language, service.currentStepIndex.value)
            }
        }
    }

    fun changeMystery(mysteryType: MysteryType) {
        updateRosarySequence(mysteryType)
        if (_isLiveSyncEnabled.value) {
            val updated = _roomState.value.copy(
                currentMysteryType = mysteryType,
                currentStepIndex = 0,
                isPlaying = false
            )
            _roomState.value = updated
            if (_isHostMode.value) {
                repository.updateLiveRoomState(updated)
            }
            _audioService.value?.setupLiveRosarySession(mysteryType, _currentLanguage.value, 0)
        } else {
            _audioService.value?.setupRosarySession(mysteryType, _currentLanguage.value, 0)
        }
    }

    fun joinLivePrayer(): Boolean {
        _isLiveSyncEnabled.value = true
        _isLocallyPaused.value = false
        _audioService.value?.setLiveMode(true)

        val currentSched = _schedule.value ?: com.example.model.RosarySchedule()
        val elapsed = currentSched.getElapsedSecondsForActiveSession()
        val targetStep = if (elapsed != null) {
            calculateStepIndexForElapsed(elapsed, _rosarySequence.value)
        } else {
            _roomState.value.currentStepIndex
        }

        val updatedRoom = _roomState.value.copy(
            currentStepIndex = targetStep,
            isPlaying = true,
            isLive = true
        )
        _roomState.value = updatedRoom

        _audioService.value?.let { service ->
            service.setHostMode(_isHostMode.value)
            service.setupLiveRosarySession(
                updatedRoom.currentMysteryType,
                updatedRoom.language,
                targetStep,
                getLiveSeekFraction(updatedRoom)
            )
            service.play(getLiveSeekFraction(updatedRoom))
        }
        return true
    }

    fun leaveLivePrayer() {
        _isLocallyPaused.value = true
        _audioService.value?.pause()
    }

    fun participantTogglePlayPause() {
        if (!_isLiveSyncEnabled.value) {
            joinLivePrayer()
        } else if (_isLocallyPaused.value) {
            _isLocallyPaused.value = false
            _audioService.value?.play(getLiveSeekFraction(_roomState.value))
        } else {
            _isLocallyPaused.value = true
            _audioService.value?.pause()
        }
    }

    fun hostStartRosary() {
        _isLiveSyncEnabled.value = true
        _isLocallyPaused.value = false
        _audioService.value?.setLiveMode(true)
        val updated = _roomState.value.copy(isPlaying = true, isLive = true)
        _roomState.value = updated
        if (_isHostMode.value) {
            repository.updateLiveRoomState(updated)
        }
        _audioService.value?.let { service ->
            service.setHostMode(true)
            service.setupLiveRosarySession(updated.currentMysteryType, updated.language, updated.currentStepIndex)
            service.play()
        }
    }

    fun hostPauseRosary() {
        val updated = _roomState.value.copy(isPlaying = false)
        _roomState.value = updated
        if (_isHostMode.value) {
            repository.updateLiveRoomState(updated)
        }
        _audioService.value?.pause()
    }

    fun hostStopRosary() {
        val updated = _roomState.value.copy(isPlaying = false, isLive = false, currentStepIndex = 0)
        _roomState.value = updated
        if (_isHostMode.value) {
            repository.updateLiveRoomState(updated)
        }
        _audioService.value?.pause()
        _isLiveSyncEnabled.value = false
    }

    fun hostNextStep() {
        val maxIndex = _rosarySequence.value.lastIndex
        if (maxIndex < 0) return
        val nextIndex = (_roomState.value.currentStepIndex + 1).coerceAtMost(maxIndex)
        if (_isLiveSyncEnabled.value) {
            val updated = _roomState.value.copy(currentStepIndex = nextIndex)
            _roomState.value = updated
            if (_isHostMode.value) {
                repository.updateLiveRoomState(updated)
            }
            _audioService.value?.jumpToStep(nextIndex)
        } else {
            _audioService.value?.nextBead()
        }
    }

    fun hostPreviousStep() {
        val prevIndex = (_roomState.value.currentStepIndex - 1).coerceAtLeast(0)
        if (_isLiveSyncEnabled.value) {
            val updated = _roomState.value.copy(currentStepIndex = prevIndex)
            _roomState.value = updated
            if (_isHostMode.value) {
                repository.updateLiveRoomState(updated)
            }
            _audioService.value?.jumpToStep(prevIndex)
        } else {
            _audioService.value?.previousBead()
        }
    }

    fun jumpToStep(stepIndex: Int) {
        if (_isLiveSyncEnabled.value) {
            val validIndex = stepIndex.coerceIn(0, (_rosarySequence.value.lastIndex).coerceAtLeast(0))
            val updated = _roomState.value.copy(currentStepIndex = validIndex)
            _roomState.value = updated
            if (_isHostMode.value) {
                repository.updateLiveRoomState(updated)
            }
            _audioService.value?.jumpToStep(validIndex)
        } else {
            _audioService.value?.jumpToStep(stepIndex)
        }
    }

    fun jumpToDecade(decade: Int) {
        val sequence = _rosarySequence.value
        val targetIndex = sequence.indexOfFirst { it.decadeIndex == decade }
            .takeIf { it != -1 } ?: (8 + (decade - 1) * 13)
        jumpToStep(targetIndex)
    }

    fun setShowFirebaseDialog(show: Boolean) {
        _showFirebaseDialog.value = show
    }

    fun triggerLiveStartPushNotification(context: Context) {
        val channelId = "rosary_push_notifications"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Live Rosary Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when Live Rosary session begins"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 101, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val currentMystery = _roomState.value.currentMysteryType
        val isHindi = _currentLanguage.value == LanguageEnum.HINDI
        val title = if (isHindi) "लाइव रोज़री प्रारंभ हो रही है! 📿" else "Live Rosary is Starting Now! 📿"
        val body = if (isHindi)
            "दुनिया भर के मसीही विश्वासी अभी ' ${currentMystery.hindiTitle} ' के लिए एक साथ प्रार्थना कर रहे हैं। अभी जुड़ें!"
        else
            "Faithful from around the world are praying '${currentMystery.englishTitle}' together. Tap to join live room!"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(9001, notification)
    }

    fun syncLiveWithScheduleNow() {
        checkScheduleActive()
    }

    private fun checkScheduleActive() {
        val currentSched = _schedule.value ?: com.example.model.RosarySchedule()
        val active = currentSched.getCurrentActiveSession()
        _activeSession.value = active
        _nextUpcomingSession.value = currentSched.getNextUpcomingSession()
        val isCurrentlyLive = active != null

        val elapsed = currentSched.getElapsedSecondsForActiveSession()
        
        if (isCurrentlyLive && elapsed != null && active != null) {
            val effectiveHost = currentSched.getEffectiveHostForSession(active.name)
            val currentRoomHost = _roomState.value.hostName
            val isMyBookedSlot = _savedUserName.value.isNotBlank() && currentSched.isSlotClaimed(active.name) && _savedUserName.value.equals(effectiveHost, ignoreCase = true)

            if (currentRoomHost.isBlank() || (currentRoomHost != effectiveHost && !_isHostMode.value)) {
                _roomState.value = _roomState.value.copy(hostName = effectiveHost)
                if (isMyBookedSlot) {
                    setHostMode(true)
                } else {
                    setHostMode(false)
                }
            }

            val totalRosaryDuration = _rosarySequence.value.sumOf { it.prayerType.durationSeconds }.coerceAtLeast(1)
            val loopElapsed = elapsed % totalRosaryDuration
            val targetStep = calculateStepIndexForElapsed(loopElapsed, _rosarySequence.value)
            
            _loopWaitTimeRemaining.value = 0
            val room = _roomState.value
            val currentLocalStep = _audioService.value?.currentStepIndex?.value ?: room.currentStepIndex
            val isAudioPlaying = _audioService.value?.isPlaying?.value == true

            val isDrifting = kotlin.math.abs(currentLocalStep - targetStep) > 1

            if (!room.isLive || (isDrifting && !_isHostMode.value) || room.isPlaying != true) {
                val stepToApply = if (_isLiveSyncEnabled.value && isAudioPlaying && !isDrifting) currentLocalStep else targetStep
                val updated = room.copy(
                    isLive = true,
                    currentStepIndex = stepToApply,
                    isPlaying = true
                )
                _roomState.value = updated
                
                if (_isHostMode.value) {
                    repository.updateLiveRoomState(updated)
                }

                if (_isLiveSyncEnabled.value && !_isLocallyPaused.value) {
                    _audioService.value?.let { service ->
                        if (isDrifting || !service.isPlaying.value) {
                            service.setupLiveRosarySession(
                                updated.currentMysteryType,
                                updated.language,
                                stepToApply
                            )
                        }
                        if (!service.isPlaying.value) {
                            service.play(getLiveSeekFraction(updated))
                        }
                    }
                }
            }
        } else {
            _loopWaitTimeRemaining.value = 0
            val room = _roomState.value
            if (room.isLive || room.isPlaying) {
                val updated = room.copy(
                    isLive = false,
                    isPlaying = false,
                    hostName = "Available"
                )
                _roomState.value = updated
                if (_isHostMode.value) {
                    repository.updateLiveRoomState(updated)
                }
                _audioService.value?.pause()
            }
            // If live session is inactive and user is not praying solo, ensure audio service is stopped
            if (_isLiveSyncEnabled.value) {
                _isLiveSyncEnabled.value = false
                _audioService.value?.pause()
            }
        }
    }

    private fun getLiveSeekFraction(state: LiveRoomState): Float {
        val currentStep = _rosarySequence.value.getOrNull(state.currentStepIndex) ?: return 0f
        val elapsedMs = System.currentTimeMillis() - state.lastUpdatedTimestamp
        val durationMs = currentStep.prayerType.durationSeconds * 1000.0
        if (durationMs <= 0) return 0f
        return (elapsedMs / durationMs).toFloat().coerceIn(0f, 0.99f)
    }

    private fun calculateStepIndexForElapsed(elapsedSeconds: Int, sequence: List<com.example.model.RosaryBeadStep>): Int {
        if (sequence.isEmpty()) return 0
        val totalRosaryDuration = sequence.sumOf { it.prayerType.durationSeconds }
        if (totalRosaryDuration == 0) return 0
        
        val loopElapsed = elapsedSeconds % totalRosaryDuration
        var accumulated = 0
        for ((index, step) in sequence.withIndex()) {
            accumulated += step.prayerType.durationSeconds
            if (loopElapsed < accumulated) {
                return index
            }
        }
        return sequence.lastIndex
    }
}
