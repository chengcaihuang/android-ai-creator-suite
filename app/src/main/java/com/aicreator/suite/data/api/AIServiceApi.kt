package com.aicreator.suite.data.api

import com.aicreator.suite.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AI鏈嶅姟API鎺ュ彛
 *
 * 鏀寔澶氱AI鍚庣锛? * - Ollama (鏈湴)
 * - OpenAI (浜戠)
 * - Claude (浜戠)
 */
interface AIServiceApi {
    
    /**
     * 鐢熸垚鏂囨
     */
    @POST("api/generate")
    suspend fun generateText(@Body request: GenerateTextRequest): Response<GenerateTextResponse>
    
    /**
     * 鐢熸垚鏍囬
     */
    @POST("api/generate-titles")
    suspend fun generateTitles(@Body request: GenerateTitlesRequest): Response<GenerateTitlesResponse>
    
    /**
     * 浼樺寲鏂囨
     */
    @POST("api/optimize")
    suspend fun optimizeText(@Body request: OptimizeTextRequest): Response<OptimizeTextResponse>
}

/**
 * 鐢熸垚鏂囨璇锋眰
 */
data class GenerateTextRequest(
    val prompt: String,
    val style: String = "灏忕孩涔?,
    val length: Int = 500,
    val keywords: List<String> = emptyList(),
    val temperature: Float = 0.7f
)

/**
 * 鐢熸垚鏂囨鍝嶅簲
 */
data class GenerateTextResponse(
    val success: Boolean,
    val text: String,
    val wordCount: Int,
    val suggestions: List<String> = emptyList()
)

/**
 * 鐢熸垚鏍囬璇锋眰
 */
data class GenerateTitlesRequest(
    val topic: String,
    val style: String = "灏忕孩涔?,
    val count: Int = 10,
    val keywords: List<String> = emptyList()
)

/**
 * 鐢熸垚鏍囬鍝嶅簲
 */
data class GenerateTitlesResponse(
    val success: Boolean,
    val titles: List<TitleSuggestion>
)

/**
 * 鏍囬寤鸿
 */
data class TitleSuggestion(
    val title: String,
    val score: Float, // 0-1 棰勬祴鐐瑰嚮鐜?    val reason: String
)

/**
 * 浼樺寲鏂囨璇锋眰
 */
data class OptimizeTextRequest(
    val text: String,
    val optimizationType: OptimizationType,
    val targetPlatform: String = "灏忕孩涔?
)

/**
 * 浼樺寲绫诲瀷
 */
enum class OptimizationType {
    READABILITY,    // 鍙鎬?    ENGAGEMENT,     // 浜掑姩鎬?    CONVERSION,     // 杞寲鐜?    SEO            // SEO浼樺寲
}

/**
 * 浼樺寲鏂囨鍝嶅簲
 */
data class OptimizeTextResponse(
    val success: Boolean,
    val originalText: String,
    val optimizedText: String,
    val improvements: List<String>,
    val score: OptimizationScore
)

/**
 * 浼樺寲璇勫垎
 */
data class OptimizationScore(
    val readability: Float,
    val engagement: Float,
    val conversion: Float,
    val overall: Float
)
