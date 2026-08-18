package com.example.data

import android.content.Context
import com.example.model.LanguageEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppLanguageManager {
    private const val PREFS_NAME = "app_language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    private val _currentLanguage = MutableStateFlow(LanguageEnum.HINDI)
    val currentLanguage: StateFlow<LanguageEnum> = _currentLanguage.asStateFlow()

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedLangCode = prefs.getString(KEY_LANGUAGE, LanguageEnum.HINDI.code) ?: LanguageEnum.HINDI.code
        val lang = LanguageEnum.values().find { it.code == savedLangCode } ?: LanguageEnum.HINDI
        _currentLanguage.value = lang
    }

    fun setLanguage(context: Context, language: LanguageEnum) {
        _currentLanguage.value = language
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}
