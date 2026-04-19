package com.aicreator.suite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicreator.suite.data.api.*
import com.aicreator.suite.data.model.*
import com.aicreator.suite.data.repository.AIContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 鍒涗綔椤甸潰ViewModel
 *
 * 绠＄悊AI鍐呭鐢熸垚鐨刄I鐘舵€佸拰涓氬姟閫昏緫
 */
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val aiContentRepository: AIContentRepository
) : ViewModel() {
    
    // UI鐘舵€?    private val _uiState = MutableStateFlow<CreateUiState>(CreateUiState.Idle)
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()
    
    // 鐢熸垚鐨勬枃妗?    private val _generatedText = MutableStateFlow("")
    val generatedText: StateFlow<String> = _generatedText.asStateFlow()
    
    // 鐢熸垚鐨勬爣棰樺垪琛?    private val _generatedTitles = MutableStateFlow<List<TitleSuggestion>>(emptyList())
    val generatedTitles: StateFlow<List<TitleSuggestion>> = _generatedTitles.asStateFlow()
    
    // 浼樺寲缁撴灉
    private val _optimizedText = MutableStateFlow<OptimizeTextResponse?>(null)
    val optimizedText: StateFlow<OptimizeTextResponse?> = _optimizedText.asStateFlow()
    
    // 褰撳墠閫変腑鐨勯鏍?    private val _selectedStyle = MutableStateFlow("灏忕孩涔?)
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()
    
    // 鍘嗗彶璁板綍
    private val _history = MutableStateFlow<List<Content>>(emptyList())
    val history: StateFlow<List<Content>> = _history.asStateFlow()
    
    /**
     * 鐢熸垚鏂囨
     */
    fun generateText(
        prompt: String,
        style: String = _selectedStyle.value,
        length: Int = 500,
        keywords: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Loading
            
            val result = aiContentRepository.generateText(
                prompt = prompt,
                style = style,
                length = length,
                keywords = keywords
            )
            
            result.fold(
                onSuccess = { response ->
                    _generatedText.value = response.text
                    _uiState.value = CreateUiState.Success("鏂囨鐢熸垚鎴愬姛锛?)
                },
                onFailure = { error ->
                    _uiState.value = CreateUiState.Error(error.message ?: "鐢熸垚澶辫触")
                }
            )
        }
    }
    
    /**
     * 鐢熸垚鏍囬
     */
    fun generateTitles(
        topic: String,
        style: String = _selectedStyle.value,
        count: Int = 10
    ) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Loading
            
            val result = aiContentRepository.generateTitles(
                topic = topic,
                style = style,
                count = count
            )
            
            result.fold(
                onSuccess = { titles ->
                    _generatedTitles.value = titles
                    _uiState.value = CreateUiState.Success("鐢熸垚${titles.size}涓爣棰?)
                },
                onFailure = { error ->
                    _uiState.value = CreateUiState.Error(error.message ?: "鐢熸垚澶辫触")
                }
            )
        }
    }
    
    /**
     * 浼樺寲鏂囨
     */
    fun optimizeText(
        text: String,
        type: OptimizationType,
        platform: String = _selectedStyle.value
    ) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Loading
            
            val result = aiContentRepository.optimizeText(
                text = text,
                type = type,
                platform = platform
            )
            
            result.fold(
                onSuccess = { response ->
                    _optimizedText.value = response
                    _uiState.value = CreateUiState.Success("鏂囨浼樺寲鎴愬姛锛?)
                },
                onFailure = { error ->
                    _uiState.value = CreateUiState.Error(error.message ?: "浼樺寲澶辫触")
                }
            )
        }
    }
    
    /**
     * 璁剧疆椋庢牸
     */
    fun setStyle(style: String) {
        _selectedStyle.value = style
    }
    
    /**
     * 鍔犺浇鍘嗗彶璁板綍
     */
    fun loadHistory() {
        viewModelScope.launch {
            _history.value = aiContentRepository.getHistory()
        }
    }
    
    /**
     * 淇濆瓨鍐呭
     */
    fun saveContent(content: Content) {
        viewModelScope.launch {
            aiContentRepository.saveContent(content)
            loadHistory()
        }
    }
    
    /**
     * 閲嶇疆鐘舵€?     */
    fun resetState() {
        _uiState.value = CreateUiState.Idle
    }
}

/**
 * UI鐘舵€? */
sealed class CreateUiState {
    object Idle : CreateUiState()
    object Loading : CreateUiState()
    data class Success(val message: String) : CreateUiState()
    data class Error(val message: String) : CreateUiState()
}
