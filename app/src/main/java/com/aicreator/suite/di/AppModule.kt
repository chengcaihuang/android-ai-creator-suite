package com.aicreator.suite.di

import android.content.Context
import androidx.room.Room
import com.aicreator.suite.data.api.AIServiceApi
import com.aicreator.suite.data.local.*
import com.aicreator.suite.data.repository.AIContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt渚濊禆娉ㄥ叆妯″潡
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // ==================== 鏁版嵁搴?====================
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ai_creator_suite_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideContentDao(database: AppDatabase): ContentDao {
        return database.contentDao()
    }
    
    @Provides
    fun provideTemplateDao(database: AppDatabase): TemplateDao {
        return database.templateDao()
    }
    
    @Provides
    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }
    
    @Provides
    fun provideEarningDao(database: AppDatabase): EarningDao {
        return database.earningDao()
    }
    
    // ==================== 缃戠粶璇锋眰 ====================
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // 榛樿浣跨敤Ollama鏈湴绔偣
        // 鍙互閫氳繃璁剧疆鍒囨崲鍒癘penAI鎴栧叾浠栨湇鍔?        return Retrofit.Builder()
            .baseUrl("http://localhost:11434/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAIServiceApi(retrofit: Retrofit): AIServiceApi {
        return retrofit.create(AIServiceApi::class.java)
    }
    
    // ==================== 浠撳簱 ====================
    
    @Provides
    @Singleton
    fun provideAIContentRepository(
        aiServiceApi: AIServiceApi,
        contentDao: ContentDao
    ): AIContentRepository {
        return AIContentRepository(aiServiceApi, contentDao)
    }
}
