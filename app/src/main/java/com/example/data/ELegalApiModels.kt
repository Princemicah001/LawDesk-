package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

// ==========================================================
// 1. Search Precedents & Documents Models (Matches eLegal JSON)
// ==========================================================
@JsonClass(generateAdapter = true)
data class ELegalSearchResponse(
    @field:Json(name = "query") val query: String? = null,
    @field:Json(name = "source") val source: String? = null,
    @field:Json(name = "classification") val classification: ELegalClassification? = null,
    @field:Json(name = "results") val results: List<ELegalSearchItem>? = emptyList(),
    @field:Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class ELegalClassification(
    @field:Json(name = "jurisdiction") val jurisdiction: String? = null,
    @field:Json(name = "confidence") val confidence: Double? = null,
    @field:Json(name = "reasoning") val reasoning: String? = null,
    @field:Json(name = "legalDomain") val legalDomain: String? = null,
    @field:Json(name = "isPdfDocumentTarget") val isPdfDocumentTarget: Boolean? = false,
    @field:Json(name = "modelType") val modelType: String? = null,
    @field:Json(name = "matchedTokensCount") val matchedTokensCount: Int? = null,
    @field:Json(name = "suggestedQueries") val suggestedQueries: List<String>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class ELegalSearchItem(
    @field:Json(name = "id") val id: String? = null,
    @field:Json(name = "title") val title: String? = null,
    @field:Json(name = "label") val label: String? = null,
    @field:Json(name = "citation") val citation: String? = null,
    @field:Json(name = "court") val court: String? = null,
    @field:Json(name = "date") val date: String? = null,
    @field:Json(name = "year") val year: String? = null,
    @field:Json(name = "type") val type: String? = null, // "Legislation", "Precedent", etc.
    @field:Json(name = "source") val source: String? = null,
    @field:Json(name = "score") val score: Double? = null,
    @field:Json(name = "summary") val summary: String? = null,
    @field:Json(name = "url") val url: String? = null, // Direct PDF or HTML document URL
    @field:Json(name = "readUrl") val readUrl: String? = null,
    @field:Json(name = "snippets") val snippets: List<String>? = emptyList(),
    @field:Json(name = "cachedAt") val cachedAt: String? = null,
    @field:Json(name = "cloudinaryMetaUrl") val cloudinaryMetaUrl: String? = null,
    @field:Json(name = "isPdf") val isPdf: Boolean? = false,
    @field:Json(name = "fileType") val fileType: String? = null, // "PDF", "DOC", etc.
    @field:Json(name = "isDocument") val isDocument: Boolean? = false,
    @field:Json(name = "pdfUrl") val legacyPdfUrl: String? = null
) {
    /**
     * Extracts the canonical document filepath or downloadable URL.
     */
    fun extractFilePathOrUrl(): String? {
        return when {
            !url.isNullOrBlank() -> url
            !legacyPdfUrl.isNullOrBlank() -> legacyPdfUrl
            !readUrl.isNullOrBlank() -> {
                // Extract sourceUrl from readUrl query parameters if present
                val match = Regex("sourceUrl=([^&]+)").find(readUrl)
                if (match != null) {
                    try {
                        java.net.URLDecoder.decode(match.groupValues[1], "UTF-8")
                    } catch (e: Exception) {
                        readUrl
                    }
                } else {
                    readUrl
                }
            }
            !snippets.isNullOrEmpty() && snippets.first().startsWith("http") -> snippets.first()
            else -> null
        }
    }
}

// ==========================================================
// 2. Citation Resolver Models
// ==========================================================
@JsonClass(generateAdapter = true)
data class ELegalResolveResponse(
    @field:Json(name = "citation") val citation: String? = null,
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "case") val caseDetail: ELegalResolvedCase? = null
)

@JsonClass(generateAdapter = true)
data class ELegalResolvedCase(
    @field:Json(name = "title") val title: String? = null,
    @field:Json(name = "court") val court: String? = null,
    @field:Json(name = "decisionDate") val decisionDate: String? = null,
    @field:Json(name = "neutralCitation") val neutralCitation: String? = null,
    @field:Json(name = "pdfDownloadUrl") val pdfDownloadUrl: String? = null,
    @field:Json(name = "url") val url: String? = null
)

// ==========================================================
// 3. Statutory Library Models
// ==========================================================
@JsonClass(generateAdapter = true)
data class ELegalLibraryResponse(
    @field:Json(name = "act") val act: String? = null,
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "pdfUrl") val pdfUrl: String? = null,
    @field:Json(name = "provisions") val provisions: List<ELegalProvision>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class ELegalProvision(
    @field:Json(name = "section") val section: String? = null,
    @field:Json(name = "heading") val heading: String? = null,
    @field:Json(name = "text") val text: String? = null
)

// ==========================================================
// 4. System Health Model
// ==========================================================
@JsonClass(generateAdapter = true)
data class ELegalHealthResponse(
    @field:Json(name = "status") val status: String? = null,
    @field:Json(name = "timestamp") val timestamp: String? = null,
    @field:Json(name = "latencyMs") val latencyMs: Long? = null,
    @field:Json(name = "service") val service: String? = null
)

// ==========================================================
// 5. Retrofit Interface
// ==========================================================
interface ELegalApiService {
    @GET("api/search")
    suspend fun searchDocuments(
        @Header("X-API-Key") apiKey: String,
        @Header("Accept") accept: String = "application/json",
        @Query("q") query: String,
        @Query("source") source: String = "all",
        @Query("category") category: String? = null,
        @Query("limit") limit: Int = 25
    ): Response<ELegalSearchResponse>

    @GET("api/resolve")
    suspend fun resolveCitation(
        @Header("X-API-Key") apiKey: String,
        @Header("Accept") accept: String = "application/json",
        @Query("citation") citation: String
    ): Response<ELegalResolveResponse>

    @GET("api/library")
    suspend fun fetchStatute(
        @Header("X-API-Key") apiKey: String,
        @Header("Accept") accept: String = "application/json",
        @Query("act") act: String,
        @Query("year") year: Int? = null
    ): Response<ELegalLibraryResponse>

    @GET("api/health")
    suspend fun checkHealth(): Response<ELegalHealthResponse>

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}
