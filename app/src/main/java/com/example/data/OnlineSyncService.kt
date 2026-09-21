package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LegalDatabasePayload(
    val statutes: List<Statute>,
    val cases: List<CourtCase>,
    val news: List<LegalNews>
)

object OnlineSyncService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 100% online dynamic data builder using the official Gemini API client
    suspend fun fetchFromOnlineGemini(): LegalDatabasePayload {
        val prompt = """
            Create a highly professional Kenyan Legal Database JSON payload with real information (no placeholders, no mock data, no exam preparations, just pure general counsel guidelines).
            You MUST return ONLY a single valid JSON object containing exactly 3 arrays: "statutes", "cases", and "news" conforming to the following keys:
            
            - "statutes": Each item must have:
                "id": string (e.g. "const_27")
                "category": string (Exactly: "CONSTITUTION", "CRIMINAL", "EVIDENCE", "ADMINISTRATIVE", or "EMPLOYMENT")
                "actName": string (e.g. "Constitution of Kenya 2010" or "Penal Code (Cap 63)")
                "sectionNumber": string (e.g. "Article 27" or "Section 26")
                "title": string (e.g. "Equality and freedom from discrimination")
                "originalText": string (the actual textual content of the article or section)
                "aiInterpretation": string (a comprehensive professional legal summary of this provision)
                "practicalExample": string (a highly practical real-life example of this provision in action)
                "whyItMatters": string (the societal and industrial significance of this law)
                "examTip": string (General guidelines and key practice takeaways for counsel)
                "relevantCases": string (names of actual landmark precedents)
                "isBookmarked": boolean (must be false)
            
            - "cases": Each item must have:
                "id": string (e.g. "case_creaw")
                "title": string (e.g. "CREAW v Attorney General")
                "citation": string (e.g. "[2016] eKLR")
                "articleNumber": string (e.g. "Article 27")
                "facts": string (actual background facts of the landmark case)
                "issues": string (fundamental legal issues determined)
                "holding": string (court's ratio decidendi and decision)
                "whyItMatters": string (significance of the ruling to legal jurisprudence)
                "examTip": string (key practice takeaways for practitioners)
            
            - "news": Each item must have:
                "id": string (e.g. "news_lsk")
                "title": string (e.g. "LSK Issues Advisory on Tax Bill")
                "sourceName": string (e.g. "LSK" or "Judiciary" or "Kenya Gazette")
                "sourceUrl": string (e.g. "https://lsk.or.ke")
                "aiSummary": string (brief description of the recent news event)
                "whyLawyersCare": string (impact of this news on legal practitioners and general public)
                "datePublished": string (e.g. "2026-06-18")

            Provide exactly 2 representative statutes (e.g. Article 27 and Article 50), exactly 1 representative landmark case (e.g. Francis Karioko Muruatetu v Republic), and exactly 1 news item. Keep all string text fields extremely short, concise, and to the point (maximum 1-2 sentence summaries per field) to ensure that the entire raw JSON compilation strictly remains under 1500 tokens, preventing response truncation.
            Do NOT include markdown wrapping like ```json or any conversational text. Return ONLY the raw valid JSON block.
        """.trimIndent()

        val rawResponse = GeminiNetworkClient.queryGemini(
            prompt,
            "You are a strict legal data compiler API which outputs raw JSON only."
        )

        if (rawResponse.contains("offline assistance mode") || rawResponse.contains("Offline Simulator Mode")) {
            throw Exception("Could not initialize LawDesk online legal databases: Gemini API key is not configured or network represents offline.")
        }

        return parseDatabaseJson(rawResponse)
    }

    fun parseDatabaseJson(jsonStr: String): LegalDatabasePayload {
        // Safe cleaning
        var cleanJson = jsonStr.trim()
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.removePrefix("```json")
        }
        if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.removePrefix("```")
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.removeSuffix("```")
        }
        cleanJson = cleanJson.trim()

        val jsonObject = JSONObject(cleanJson)
        
        val statutesList = mutableListOf<Statute>()
        val statutesArr = jsonObject.getJSONArray("statutes")
        for (i in 0 until statutesArr.length()) {
            val sObj = statutesArr.getJSONObject(i)
            statutesList.add(
                Statute(
                    id = sObj.getString("id"),
                    category = sObj.getString("category"),
                    actName = sObj.getString("actName"),
                    sectionNumber = sObj.getString("sectionNumber"),
                    title = sObj.getString("title"),
                    originalText = sObj.getString("originalText"),
                    aiInterpretation = sObj.getString("aiInterpretation"),
                    practicalExample = sObj.getString("practicalExample"),
                    whyItMatters = sObj.getString("whyItMatters"),
                    examTip = sObj.optString("examTip", "Counsel advice: Adhere strictly to procedural fairness and constitutional boundaries."),
                    relevantCases = sObj.getString("relevantCases"),
                    isBookmarked = sObj.optBoolean("isBookmarked", false)
                )
            )
        }

        val casesList = mutableListOf<CourtCase>()
        val casesArr = jsonObject.getJSONArray("cases")
        for (i in 0 until casesArr.length()) {
            val cObj = casesArr.getJSONObject(i)
            casesList.add(
                CourtCase(
                    id = cObj.getString("id"),
                    title = cObj.getString("title"),
                    citation = cObj.getString("citation"),
                    articleNumber = cObj.getString("articleNumber"),
                    facts = cObj.getString("facts"),
                    issues = cObj.getString("issues"),
                    holding = cObj.getString("holding"),
                    whyItMatters = cObj.getString("whyItMatters"),
                    examTip = cObj.optString("examTip", "Key Takeaway: Landmark precedent establishing binding legal guidance.")
                )
            )
        }

        val newsList = mutableListOf<LegalNews>()
        val newsArr = jsonObject.getJSONArray("news")
        for (i in 0 until newsArr.length()) {
            val nObj = newsArr.getJSONObject(i)
            newsList.add(
                LegalNews(
                    id = nObj.getString("id"),
                    title = nObj.getString("title"),
                    sourceName = nObj.getString("sourceName"),
                    sourceUrl = nObj.getString("sourceUrl"),
                    aiSummary = nObj.getString("aiSummary"),
                    whyLawyersCare = nObj.getString("whyLawyersCare"),
                    datePublished = nObj.getString("datePublished")
                )
            )
        }

        return LegalDatabasePayload(statutesList, casesList, newsList)
    }

    suspend fun crawlOnlineForQuery(query: String): LegalDatabasePayload {
        val prompt = """
            You are a live legal crawler and researcher for Kenya Law Reports (eKLR - kenyalaw.org), LSK, and Judiciary of Kenya.
            The user is searching for legal information regarding: "$query".
            You MUST perform live legal research over official, legitimate repositories of Kenyan Law to fetch the real, official statutory section, act, or case precedent matching this query.
            Do NOT return mock or fake information.
            
            Return ONLY a single valid JSON object with exactly three arrays: "statutes", "cases", and "news", matching these detailed schemas:
            "statutes": [
               {
                  "id": "crawled_${System.currentTimeMillis()}",
                  "category": "CONSTITUTION",
                  "actName": "Constitution of Kenya 2010",
                  "sectionNumber": "Article 1",
                  "title": "Title",
                  "originalText": "Full text from Kenya Law eKLR",
                  "aiInterpretation": "Analysis",
                  "practicalExample": "Example",
                  "whyItMatters": "Socio significance",
                  "examTip": "Counsel takeaways",
                  "relevantCases": "Real precedent citation",
                  "isBookmarked": false
               }
            ],
            "cases": [
               {
                  "id": "case_crawled_${System.currentTimeMillis()}",
                  "title": "Real Landmark Case",
                  "citation": "[2026] eKLR",
                  "articleNumber": "Article 1",
                  "facts": "Genuine facts from eKLR",
                  "issues": "Issues",
                  "holding": "Holding",
                  "whyItMatters": "Value",
                  "examTip": "Takeaway"
               }
            ],
            "news": []
            
            Format the returned categories as "CONSTITUTION", "CRIMINAL", "EVIDENCE", "ADMINISTRATIVE", "EMPLOYMENT", "LAND", "COMMERCIAL", "FAMILY", or "CIVIL" to match the actual act.
            Return strictly raw valid JSON. Do not wrap in ```json or ``` markdown guards. Do not add conversational conversational pad.
        """.trimIndent()

        val rawResponse = GeminiNetworkClient.queryGemini(
            prompt,
            "You are a strict, top-tier Kenya Law web scraper and legal compiler. You output clean raw JSON only."
        )

        return parseDatabaseJson(rawResponse)
    }
}
