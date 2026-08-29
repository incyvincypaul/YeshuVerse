package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.YeshuVerseApplication
import com.example.data.AppLanguageManager
import com.example.data.RosaryPrayers
import com.example.data.database.UserProgress
import com.example.model.LanguageEnum
import com.example.model.MysteryType
import com.example.model.RosaryBeadStep
import com.example.service.RosaryAudioService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SoloRosaryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as YeshuVerseApplication).database
    private val userProgressDao = database.userProgressDao()

    private val _currentLanguage = MutableStateFlow(AppLanguageManager.currentLanguage.value)
    val currentLanguage: StateFlow<LanguageEnum> = _currentLanguage.asStateFlow()

    private val _rosarySequence = MutableStateFlow<List<RosaryBeadStep>>(emptyList())
    val rosarySequence: StateFlow<List<RosaryBeadStep>> = _rosarySequence.asStateFlow()

    private val _currentMysteryType = MutableStateFlow(RosaryPrayers.getTodayDefaultMystery())
    val currentMysteryType: StateFlow<MysteryType> = _currentMysteryType.asStateFlow()

    private val _litCandlesCount = MutableStateFlow(0)
    val litCandlesCount: StateFlow<Int> = _litCandlesCount.asStateFlow()

    private val _audioService = MutableStateFlow<RosaryAudioService?>(null)
    val audioService: StateFlow<RosaryAudioService?> = _audioService.asStateFlow()

    init {
        updateRosarySequence(_currentMysteryType.value)
        
        viewModelScope.launch {
            AppLanguageManager.currentLanguage.collect { lang ->
                if (_currentLanguage.value != lang) {
                    _currentLanguage.value = lang
                    _audioService.value?.setupRosarySession(_currentMysteryType.value, lang, _audioService.value?.currentStepIndex?.value ?: 0)
                }
            }
        }

        viewModelScope.launch {
            val savedProgress = userProgressDao.getProgress().first()
            if (savedProgress != null) {
                // Potential to load saved mystery/step index here
            }
        }
    }

    fun bindAudioService(service: RosaryAudioService) {
        _audioService.value = service
        // Do not automatically alter service or autoplay on binding
    }

    fun prepareSoloSession() {
        _audioService.value?.let { service ->
            service.pause()
            service.setLiveMode(false)
            service.setHostMode(true)
            service.setupRosarySession(_currentMysteryType.value, _currentLanguage.value, 0)
            service.pause()
        }
    }

    fun stopSoloSession() {
        _audioService.value?.let { service ->
            if (!service.isLiveMode.value) {
                service.pause()
            }
        }
    }

    private fun updateRosarySequence(mysteryType: MysteryType) {
        _rosarySequence.value = RosaryPrayers.buildRosarySequence(mysteryType)
    }

    fun setLanguage(language: LanguageEnum) {
        _currentLanguage.value = language
        AppLanguageManager.setLanguage(getApplication(), language)
        _audioService.value?.setupRosarySession(_currentMysteryType.value, language, _audioService.value?.currentStepIndex?.value ?: 0)
    }

    fun changeMystery(mysteryType: MysteryType) {
        _currentMysteryType.value = mysteryType
        updateRosarySequence(mysteryType)
        _audioService.value?.setupRosarySession(mysteryType, _currentLanguage.value, 0)
    }

    fun lightCandle() {
        if (_litCandlesCount.value < 4) {
            _litCandlesCount.value += 1
        } else {
            _litCandlesCount.value = 0
        }
    }

    fun hostStartRosary() {
        _audioService.value?.play()
    }

    fun hostPauseRosary() {
        _audioService.value?.pause()
    }

    fun hostNextStep() {
        _audioService.value?.nextBead()
    }

    fun hostPreviousStep() {
        _audioService.value?.previousBead()
    }

    fun jumpToStep(stepIndex: Int) {
        _audioService.value?.jumpToStep(stepIndex)
    }

    fun jumpToDecade(decade: Int) {
        val sequence = _rosarySequence.value
        val targetIndex = sequence.indexOfFirst { it.decadeIndex == decade }
            .takeIf { it != -1 } ?: (8 + (decade - 1) * 13)
        jumpToStep(targetIndex)
    }
    
    fun saveSoloProgress(stepIndex: Int) {
        viewModelScope.launch {
            userProgressDao.saveProgress(
                UserProgress(id = 0, lastStepIndex = stepIndex, mysteryType = _currentMysteryType.value.name)
            )
        }
    }
}
