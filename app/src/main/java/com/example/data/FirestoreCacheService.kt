package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class DailyBriefingPayload(
    val articleId: String,
    val aiInterpretation: String,
    val practicalExample: String,
    val whyItMatters: String,
    val examTip: String,
    
    val caseId: String,
    val caseTitle: String,
    val caseCitation: String,
    val caseFacts: String,
    val caseIssues: String,
    val caseHolding: String,
    val caseWhyItMatters: String,
    val caseExamTip: String
)

object FirestoreCacheService {
    private const val FIREBASE_API_KEY = "AIzaSyCJZ4IW1Vc5TbDMY_Om9UO36bazN22RlYI"
    private const val PROJECT_ID = "roast-me-5d4e7"
    private const val BASE_URL = "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun getDailyBriefing(dateStr: String): DailyBriefingPayload? = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/daily_briefing_logs/$dateStr?key=$FIREBASE_API_KEY"
        
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
            
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext null
                }
                val bodyStr = response.body?.string() ?: return@withContext null
                val root = JSONObject(bodyStr)
                val fields = root.optJSONObject("fields") ?: return@withContext null
                
                val getStringField = { key: String ->
                    fields.optJSONObject(key)?.optString("stringValue", "") ?: ""
                }
                
                return@withContext DailyBriefingPayload(
                    articleId = getStringField("articleId"),
                    aiInterpretation = getStringField("aiInterpretation"),
                    practicalExample = getStringField("practicalExample"),
                    whyItMatters = getStringField("whyItMatters"),
                    examTip = getStringField("examTip"),
                    
                    caseId = getStringField("caseId"),
                    caseTitle = getStringField("caseTitle"),
                    caseCitation = getStringField("caseCitation"),
                    caseFacts = getStringField("caseFacts"),
                    caseIssues = getStringField("caseIssues"),
                    caseHolding = getStringField("caseHolding"),
                    caseWhyItMatters = getStringField("caseWhyItMatters"),
                    caseExamTip = getStringField("caseExamTip")
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Error checking Firestore daily cache: ${e.localizedMessage}")
            null
        }
    }

    suspend fun saveDailyBriefing(dateStr: String, payload: DailyBriefingPayload) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/daily_briefing_logs/$dateStr?key=$FIREBASE_API_KEY"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        
        val fieldsJson = JSONObject()
        val putStringField = { key: String, value: String ->
            fieldsJson.put(key, JSONObject().put("stringValue", value))
        }
        
        putStringField("articleId", payload.articleId)
        putStringField("aiInterpretation", payload.aiInterpretation)
        putStringField("practicalExample", payload.practicalExample)
        putStringField("whyItMatters", payload.whyItMatters)
        putStringField("examTip", payload.examTip)
        
        putStringField("caseId", payload.caseId)
        putStringField("caseTitle", payload.caseTitle)
        putStringField("caseCitation", payload.caseCitation)
        putStringField("caseFacts", payload.caseFacts)
        putStringField("caseIssues", payload.caseIssues)
        putStringField("caseHolding", payload.caseHolding)
        putStringField("caseWhyItMatters", payload.caseWhyItMatters)
        putStringField("caseExamTip", payload.caseExamTip)
        
        val requestBodyJson = JSONObject()
        requestBodyJson.put("fields", fieldsJson)
        
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()
            
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("FirestoreCacheService", "Error writing to Firestore cache: ${response.code} ${response.body?.string()}")
                } else {
                    Log.d("FirestoreCacheService", "Successfully saved daily briefing in Firestore for $dateStr")
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Exception writing to Firestore cache: ${e.localizedMessage}")
        }
    }

    suspend fun getGuidedQueries(dateStr: String): List<String>? = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/guided_queries_logs/$dateStr?key=$FIREBASE_API_KEY"
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyStr = response.body?.string() ?: return@withContext null
                val root = JSONObject(bodyStr)
                val fields = root.optJSONObject("fields") ?: return@withContext null
                val queriesObj = fields.optJSONObject("queries") ?: return@withContext null
                val arrayValue = queriesObj.optJSONObject("arrayValue") ?: return@withContext null
                val values = arrayValue.optJSONArray("values") ?: return@withContext null
                val list = mutableListOf<String>()
                for (i in 0 until values.length()) {
                    val valueObj = values.getJSONObject(i)
                    list.add(valueObj.optString("stringValue", ""))
                }
                return@withContext list
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Error checking Firestore guided queries: ${e.localizedMessage}")
            null
        }
    }

    suspend fun saveGuidedQueries(dateStr: String, queries: List<String>) = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/guided_queries_logs/$dateStr?key=$FIREBASE_API_KEY"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        
        val fieldsJson = JSONObject()
        val valuesArray = org.json.JSONArray()
        queries.forEach { q ->
            valuesArray.put(JSONObject().put("stringValue", q))
        }
        val arrayValue = JSONObject().put("values", valuesArray)
        fieldsJson.put("queries", JSONObject().put("arrayValue", arrayValue))
        
        val requestBodyJson = JSONObject()
        requestBodyJson.put("fields", fieldsJson)
        
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).patch(requestBody).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("FirestoreCacheService", "Error writing guided queries to Firestore: ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Exception writing guided quries: ${e.localizedMessage}")
        }
    }

    suspend fun uploadContribution(id: String, type: String, fields: Map<String, String>): Boolean = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/contributed_materials/$id?key=$FIREBASE_API_KEY"
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val fieldsJson = JSONObject()
        fieldsJson.put("type", JSONObject().put("stringValue", type))
        fields.forEach { (k, v) ->
            fieldsJson.put(k, JSONObject().put("stringValue", v))
        }

        val requestBodyJson = JSONObject()
        requestBodyJson.put("fields", fieldsJson)

        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).patch(requestBody).build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.d("FirestoreCacheService", "Successfully uploaded contribution: $id")
                    return@withContext true
                } else {
                    Log.e("FirestoreCacheService", "Error uploading contribution to Firestore: ${response.code} ${response.body?.string()}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Exception uploading contribution: ${e.localizedMessage}")
            return@withContext false
        }
    }

    suspend fun fetchContributions(): List<Pair<String, Map<String, String>>> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/contributed_materials?key=$FIREBASE_API_KEY"
        val request = Request.Builder().url(url).get().build()
        val list = mutableListOf<Pair<String, Map<String, String>>>()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("FirestoreCacheService", "Error fetching contributions from Firestore: ${response.code}")
                    return@withContext emptyList()
                }
                val bodyStr = response.body?.string() ?: return@withContext emptyList()
                val root = JSONObject(bodyStr)
                val documents = root.optJSONArray("documents") ?: return@withContext emptyList()
                
                for (i in 0 until documents.length()) {
                    val docObj = documents.getJSONObject(i)
                    val fieldsObj = docObj.optJSONObject("fields") ?: continue
                    
                    val map = mutableMapOf<String, String>()
                    val keys = fieldsObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val valObj = fieldsObj.getJSONObject(key)
                        val stringValue = valObj.optString("stringValue", "")
                        map[key] = stringValue
                    }
                    
                    val type = map["type"] ?: ""
                    list.add(Pair(type, map))
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreCacheService", "Exception fetching contributions: ${e.localizedMessage}")
        }
        return@withContext list
    }
}
