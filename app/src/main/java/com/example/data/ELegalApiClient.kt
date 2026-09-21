package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object ELegalApiClient {
    private const val TAG = "ELegalApiClient"
    
    // Developer API endpoint as documented (supports live proxy & production)
    private const val BASE_URL = "https://www.lexshub.xyz/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val apiLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val method = request.method
        val url = request.url.toString()
        val headers = mutableMapOf<String, String>()
        for (i in 0 until request.headers.size) {
            headers[request.headers.name(i)] = request.headers.value(i)
        }
        
        val startTime = System.currentTimeMillis()
        val logId = ApiLogManager.logRequest(
            method = method,
            url = url,
            headers = headers
        )
        
        try {
            val response = chain.proceed(request)
            val latency = System.currentTimeMillis() - startTime
            val peekBody = try {
                response.peekBody(512 * 1024).string()
            } catch (e: Exception) {
                "[Binary / Streamed Body]"
            }
            
            ApiLogManager.logResponse(
                id = logId,
                statusCode = response.code,
                statusMessage = response.message,
                latencyMs = latency,
                responseBody = peekBody,
                isError = !response.isSuccessful
            )
            response
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            ApiLogManager.logResponse(
                id = logId,
                statusCode = 0,
                statusMessage = e.message ?: "Connection Failure",
                latencyMs = latency,
                responseBody = "Network Error: ${e.message}",
                isError = true,
                summary = "Failure: ${e.message}"
            )
            throw e
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(apiLoggingInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ELegalApiService = retrofit.create(ELegalApiService::class.java)

    // Dual developer API keys with automatic failover & load balancing
    private val DEFAULT_API_KEYS = listOf(
        "el_42457354e48adff9d5193fc9f6c00588",
        "el_1e2179ab2937c295d084010b37317796"
    )

    /**
     * Resolves the list of developer API Keys from BuildConfig / env and defaults.
     */
    fun getApiKeys(): List<String> {
        val keys = mutableListOf<String>()
        try {
            val buildConfigKey = BuildConfig::class.java.getField("ELEGAL_API_KEY").get(null) as? String
            if (!buildConfigKey.isNullOrBlank() && buildConfigKey != "MY_ELEGAL_API_KEY") {
                keys.add(buildConfigKey)
            }
        } catch (_: Exception) {}
        keys.addAll(DEFAULT_API_KEYS)
        return keys.distinct()
    }

    /**
     * Resolves the primary active developer API Key.
     */
    fun getApiKey(): String {
        return getApiKeys().firstOrNull() ?: "el_42457354e48adff9d5193fc9f6c00588"
    }

    /**
     * Searches legal precedents & documents (Statutes, Acts, Precedents) from eLegal API.
     * Uses automatic key rotation and fallback if a key encounters rate limits or auth failures.
     */
    suspend fun searchDocuments(
        query: String,
        source: String = "all",
        category: String? = null,
        limit: Int = 25
    ): List<ELegalSearchItem> {
        return withContext(Dispatchers.IO) {
            val keys = getApiKeys()
            for (apiKey in keys) {
                try {
                    val response = apiService.searchDocuments(
                        apiKey = apiKey,
                        query = query,
                        source = source,
                        category = category,
                        limit = limit
                    )
                    if (response.isSuccessful) {
                        return@withContext response.body()?.results ?: emptyList()
                    } else {
                        Log.w(TAG, "searchDocuments failed with key ${apiKey.take(8)}... HTTP ${response.code()}")
                        // Continue to next key if 401, 403, or 429
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Search failover on key ${apiKey.take(8)}... (${e.javaClass.simpleName}: ${e.message})")
                }
            }
            emptyList()
        }
    }

    /**
     * Resolves a case citation using eLegal API with multi-key failover.
     */
    suspend fun resolveCitation(citation: String): ELegalResolvedCase? {
        return withContext(Dispatchers.IO) {
            val keys = getApiKeys()
            for (apiKey in keys) {
                try {
                    val response = apiService.resolveCitation(apiKey = apiKey, citation = citation)
                    if (response.isSuccessful) {
                        return@withContext response.body()?.caseDetail
                    } else {
                        Log.w(TAG, "resolveCitation failed with key ${apiKey.take(8)}... HTTP ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Citation resolution failover on key ${apiKey.take(8)}...: ${e.message}")
                }
            }
            null
        }
    }

    /**
     * Fetches a full statute / Act from eLegal Statutory Library API with multi-key failover.
     */
    suspend fun fetchStatute(act: String, year: Int? = null): ELegalLibraryResponse? {
        return withContext(Dispatchers.IO) {
            val keys = getApiKeys()
            for (apiKey in keys) {
                try {
                    val response = apiService.fetchStatute(apiKey = apiKey, act = act, year = year)
                    if (response.isSuccessful) {
                        return@withContext response.body()
                    } else {
                        Log.w(TAG, "fetchStatute failed with key ${apiKey.take(8)}... HTTP ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Fetch statute failover on key ${apiKey.take(8)}...: ${e.message}")
                }
            }
            null
        }
    }

    /**
     * Downloads an Act PDF or case report PDF locally to app's cache/files dir.
     * Returns the absolute local file path on success, or null on failure.
     */
    suspend fun downloadPdfLocally(context: Context, pdfUrl: String, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val safeFileName = fileName.replace(Regex("[^a-zA-Z0-9_.-]"), "_") + ".pdf"
                val pdfDir = File(context.filesDir, "statute_pdfs")
                if (!pdfDir.exists()) {
                    pdfDir.mkdirs()
                }
                val targetFile = File(pdfDir, safeFileName)
                
                // If already downloaded and valid size, reuse it
                if (targetFile.exists() && targetFile.length() > 500) {
                    return@withContext targetFile.absolutePath
                }

                val response = apiService.downloadFile(pdfUrl)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    body.byteStream().use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d(TAG, "PDF downloaded successfully: ${targetFile.absolutePath} (${targetFile.length()} bytes)")
                    targetFile.absolutePath
                } else {
                    Log.w(TAG, "Failed to download PDF HTTP ${response.code()} from $pdfUrl")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading PDF from $pdfUrl: ${e.message}")
                null
            }
        }
    }
}
