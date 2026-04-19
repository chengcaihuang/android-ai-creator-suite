package com.aicreator.suite.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 鍐呭绫诲瀷
 */
enum class ContentType {
    TEXT,       // 鏂囨
    IMAGE,      // 鍥剧墖
    VIDEO,      // 瑙嗛
    SCRIPT      // 鑴氭湰
}

/**
 * 鍐呭鐘舵€? */
enum class ContentStatus {
    DRAFT,      // 鑽夌
    PUBLISHED,  // 宸插彂甯?    ARCHIVED    // 宸插綊妗?}

/**
 * 鍐呭瀹炰綋
 */
@Entity(tableName = "contents")
data class Content(
    @PrimaryKey
    val id: String,
    val type: ContentType,
    val title: String,
    val body: String,
    val platform: String,
    val createdAt: Long,
    val status: ContentStatus,
    val tags: String = "",
    val wordCount: Int = 0,
    val thumbnailUrl: String? = null
)

/**
 * 妯℃澘
 */
@Entity(tableName = "templates")
data class Template(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val promptTemplate: String,
    val usageCount: Long = 0,
    val isPremium: Boolean = false,
    val previewImage: String? = null
)

/**
 * 鐢ㄦ埛璁剧疆
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: String = "default",
    val preferredStyle: String = "灏忕孩涔?,
    val defaultLength: Int = 500,
    val aiProvider: String = "ollama", // ollama, openai, claude
    val apiKey: String? = null,
    val ollamaEndpoint: String = "http://localhost:11434",
    val theme: String = "system",
    val language: String = "zh-CN"
)

/**
 * 鍙樼幇璁板綍
 */
@Entity(tableName = "earnings")
data class Earning(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val source: String,
    val platform: String,
    val date: Long,
    val note: String = ""
)
