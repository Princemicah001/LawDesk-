package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiNetworkClient {
    // We are replacing the backend engine with Groq API as instructed.
    private const val GROQ_BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
    
    // Primary Groq models requested by the user
    private const val MODEL_PRIMARY = "meta-llama/llama-prompt-guard-2-86m"
    private const val MODEL_SECONDARY = "openai/gpt-oss-120b"
    
    // Safe standard fallback in case the custom experimental models are busy/not yet registered
    private const val MODEL_LLAMA_FALLBACK = "llama-3.1-8b-instant"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        // Return the requested Groq API Key
        return "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"
    }

    suspend fun queryGemini(prompt: String, systemInstruction: String = ""): String = withContext(Dispatchers.IO) {
        // Enforce robust network monitoring before firing any API requests
        NetworkMonitor.verifyConnectivity()

        val apiKey = getApiKey()
        val sysInstruct = if (systemInstruction.isNotEmpty()) {
            systemInstruction
        } else {
            "You are LawDesk AI, an authoritative, elite helper and professional assistant on the Kenyan Legal System. Give highly premium, professional answers in plain explanation, real-life examples, and why it matters. Support offline fallbacks elegantly."
        }

        // Try the user's primary/secondary models first, fallback if it errors out
        val candidateModels = listOf(MODEL_PRIMARY, MODEL_SECONDARY, MODEL_LLAMA_FALLBACK, "llama3-8b-8192")
        var lastException: Exception? = null

        for (modelToTry in candidateModels) {
            try {
                return@withContext executeChatCompletion(modelToTry, sysInstruct, prompt, apiKey)
            } catch (e: Exception) {
                lastException = e
                // Continue to try next fallback model
            }
        }

        throw IOException("Groq API request failed across all candidate models. Last error: ${lastException?.localizedMessage}", lastException)
    }

    private fun executeChatCompletion(modelName: String, systemInstruction: String, prompt: String, apiKey: String): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        
        val requestJson = JSONObject()
        requestJson.put("model", modelName)
        requestJson.put("temperature", 0.1) // Low temperature for high precision legal text
        
        val messagesArray = JSONArray()
        
        val systemMessage = JSONObject()
        systemMessage.put("role", "system")
        systemMessage.put("content", systemInstruction)
        messagesArray.put(systemMessage)

        val userMessage = JSONObject()
        userMessage.put("role", "user")
        userMessage.put("content", prompt)
        messagesArray.put(userMessage)

        requestJson.put("messages", messagesArray)

        val requestBody = requestJson.toString().toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(GROQ_BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP error ${response.code}: ${response.body?.string()}")
            }
            
            val responseBody = response.body?.string() ?: throw IOException("Empty response body from Groq API")
            val rawObj = JSONObject(responseBody)
            val choices = rawObj.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val messageObj = firstChoice.optJSONObject("message")
                if (messageObj != null) {
                    return messageObj.optString("content", "")
                }
            }
            throw IOException("Malformed Groq response: $responseBody")
        }
    }
}
