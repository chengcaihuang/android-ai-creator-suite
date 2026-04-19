package com.aicreator.suite.data.service

import com.aicreator.suite.data.api.*
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

/**
 * Ollama鏈湴AI鏈嶅姟閫傞厤鍣? *
 * 杩炴帴鏈湴Ollama鏈嶅姟锛屼娇鐢≦wen/Llama绛夊紑婧愭ā鍨? * 浼樺娍锛氬畬鍏ㄥ厤璐癸紝闅愮瀹夊叏
 */
class OllamaServiceAdapter(
    private val baseUrl: String = "http://localhost:11434"
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()
    
    /**
     * 鐢熸垚鏂囨
     */
    suspend fun generateText(
        prompt: String,
        model: String = "qwen2.5:7b",
        systemPrompt: String = DEFAULT_SYSTEM_PROMPT
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = OllamaGenerateRequest(
                model = model,
                prompt = prompt,
                system = systemPrompt,
                stream = false
            )
            
            val requestBody = gson.toJson(request).toRequestBody(jsonMediaType)
            
            val httpRequest = Request.Builder()
                .url("$baseUrl/api/generate")
                .post(requestBody)
                .build()
            
            val response = client.newCall(httpRequest).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val ollamaResponse = gson.fromJson(responseBody, OllamaGenerateResponse::class.java)
                Result.success(ollamaResponse.response)
            } else {
                Result.failure(Exception("Ollama璇锋眰澶辫触: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 鐢熸垚鏍囬
     */
    suspend fun generateTitles(
        topic: String,
        style: String,
        count: Int = 10
    ): Result<List<TitleSuggestion>> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                璇蜂负浠ヤ笅涓婚鐢熸垚$count涓惛寮曚汉鐨勬爣棰橈紝閫傜敤浜?style骞冲彴銆?                姣忎釜鏍囬涓€琛岋紝鏍煎紡锛氭爣棰榺棰勬祴鐐瑰嚮鐜?0-1)|鎺ㄨ崘鐞嗙敱
                
                涓婚锛?topic
            """.trimIndent()
            
            val result = generateText(prompt)
            
            result.map { response ->
                parseTitleResponse(response, count)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 妫€鏌llama鏈嶅姟鐘舵€?     */
    suspend fun checkServiceStatus(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tags")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 鑾峰彇鍙敤妯″瀷鍒楄〃
     */
    suspend fun getAvailableModels(): List<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/tags")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val tagsResponse = gson.fromJson(responseBody, OllamaTagsResponse::class.java)
                tagsResponse.models.map { it.name }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * 瑙ｆ瀽鏍囬鍝嶅簲
     */
    private fun parseTitleResponse(response: String, expectedCount: Int): List<TitleSuggestion> {
        val lines = response.split("\n").filter { it.isNotBlank() }
        return lines.take(expectedCount).mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size >= 3) {
                TitleSuggestion(
                    title = parts[0].trim(),
                    score = parts[1].trim().toFloatOrNull() ?: 0.5f,
                    reason = parts[2].trim()
                )
            } else if (parts.isNotEmpty()) {
                TitleSuggestion(
                    title = parts[0].trim(),
                    score = 0.5f,
                    reason = ""
                )
            } else {
                null
            }
        }
    }
    
    companion object {
        const val DEFAULT_SYSTEM_PROMPT = """
            浣犳槸涓€涓笓涓氱殑鍐呭鍒涗綔鍔╂墜锛屾搮闀夸负鍚勭绀句氦濯掍綋骞冲彴鍒涗綔鍚稿紩浜虹殑鍐呭銆?            浣犻渶瑕佹牴鎹敤鎴锋彁渚涚殑涓婚鍜岃姹傦紝鐢熸垚楂樿川閲忋€佹湁鍒涙剰鐨勫唴瀹广€?            
            瑕佹眰锛?            1. 鍐呭瑕佹湁鍚稿紩鍔涳紝鑳藉紩鍙戠敤鎴蜂簰鍔?            2. 绗﹀悎鐩爣骞冲彴鐨勯鏍煎拰鐢ㄦ埛涔犳儻
            3. 璇█鐢熷姩锛岄伩鍏嶈繃浜庡畼鏂瑰寲
            4. 閫傚綋浣跨敤emoji澧炲姞瓒ｅ懗鎬?        """
    }
}

/**
 * Ollama鐢熸垚璇锋眰
 */
data class OllamaGenerateRequest(
    val model: String,
    val prompt: String,
    val system: String? = null,
    val stream: Boolean = false,
    val options: Map<String, Any>? = null
)

/**
 * Ollama鐢熸垚鍝嶅簲
 */
data class OllamaGenerateResponse(
    @SerializedName("model") val model: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("response") val response: String,
    @SerializedName("done") val done: Boolean,
    @SerializedName("total_duration") val totalDuration: Long? = null,
    @SerializedName("eval_count") val evalCount: Int? = null
)

/**
 * Ollama鏍囩鍝嶅簲
 */
data class OllamaTagsResponse(
    @SerializedName("models") val models: List<OllamaModel>
)

data class OllamaModel(
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Long,
    @SerializedName("modified_at") val modifiedAt: String
)
