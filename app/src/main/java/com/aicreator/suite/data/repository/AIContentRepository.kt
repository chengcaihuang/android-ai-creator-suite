package com.aicreator.suite.data.repository

import com.aicreator.suite.data.api.*
import com.aicreator.suite.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI鍐呭浠撳簱
 *
 * 缁熶竴绠＄悊AI鍐呭鐢熸垚鐩稿叧鏁版嵁鎿嶄綔
 * 鏀寔鏈湴(Ollama)鍜屼簯绔?OpenAI/Claude)娣峰悎鏂规
 */
@Singleton
class AIContentRepository @Inject constructor(
    private val aiServiceApi: AIServiceApi,
    private val contentDao: ContentDao
) {
    /**
     * 鐢熸垚鏂囨
     */
    suspend fun generateText(
        prompt: String,
        style: String,
        length: Int = 500,
        keywords: List<String> = emptyList()
    ): Result<GenerateTextResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateTextRequest(
                prompt = prompt,
                style = style,
                length = length,
                keywords = keywords
            )
            
            val response = aiServiceApi.generateText(request)
            
            if (response.isSuccessful && response.body() != null) {
                // 淇濆瓨鍒版湰鍦版暟鎹簱
                val content = Content(
                    id = java.util.UUID.randomUUID().toString(),
                    type = ContentType.TEXT,
                    title = prompt,
                    body = response.body()!!.text,
                    platform = style,
                    createdAt = System.currentTimeMillis(),
                    status = ContentStatus.DRAFT
                )
                contentDao.insertContent(content)
                
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("鐢熸垚澶辫触: ${response.code()}"))
            }
        } catch (e: Exception) {
            // 闄嶇骇鍒版湰鍦版ā鍨嬫垨杩斿洖閿欒
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
            val request = GenerateTitlesRequest(
                topic = topic,
                style = style,
                count = count
            )
            
            val response = aiServiceApi.generateTitles(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.titles)
            } else {
                Result.failure(Exception("鐢熸垚澶辫触: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 浼樺寲鏂囨
     */
    suspend fun optimizeText(
        text: String,
        type: OptimizationType,
        platform: String
    ): Result<OptimizeTextResponse> = withContext(Dispatchers.IO) {
        try {
            val request = OptimizeTextRequest(
                text = text,
                optimizationType = type,
                targetPlatform = platform
            )
            
            val response = aiServiceApi.optimizeText(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("浼樺寲澶辫触: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 鑾峰彇鍘嗗彶璁板綍
     */
    suspend fun getHistory(limit: Int = 50): List<Content> = withContext(Dispatchers.IO) {
        contentDao.getRecentContent(limit)
    }
    
    /**
     * 淇濆瓨鍐呭
     */
    suspend fun saveContent(content: Content) = withContext(Dispatchers.IO) {
        contentDao.insertContent(content)
    }
    
    /**
     * 鍒犻櫎鍐呭
     */
    suspend fun deleteContent(contentId: String) = withContext(Dispatchers.IO) {
        contentDao.deleteContent(contentId)
    }
}
