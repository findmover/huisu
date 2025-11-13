package com.app.huisu.data.network

import com.app.huisu.data.api.HotSearchApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络客户端单例对象
 * 提供Retrofit实例和API接口
 */
object NetworkClient {

    private const val BASE_URL = "https://api.codelife.cc/"

    // API认证Token (有效期到2027年)
    private const val TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MjE0YWU5MjhmODk3ZDNhZGZjZjkwZTYiLCJpYXQiOjE3MzUxNzk5NzgsImV4cCI6MTc5NzM4Nzk3OH0.DDgHUHZ9kXp5_1cJ2pA-a_ON0kUMv7AKY1LtejJ8kSQ"

    // API签名密钥
    private const val SIGNATURE_KEY = "U2FsdGVkX1+lkSSk7qM9110tQ2KZKA8dk6AQ7j8d0cc="

    /**
     * OkHttp客户端配置
     */
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("accept", "application/json, text/plain, */*")
                .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("cache-control", "no-cache")
                .header("token", TOKEN)
                .header("signaturekey", SIGNATURE_KEY)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Retrofit实例配置
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 热搜API接口实例
     */
    val hotSearchApi: HotSearchApi = retrofit.create(HotSearchApi::class.java)
}
