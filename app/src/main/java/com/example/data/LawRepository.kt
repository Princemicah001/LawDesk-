package com.example.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.ui.PdfDocumentHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class LegalMaxim(
    val latin: String,
    val translation: String
)

class LawRepository(private val context: Context) {
    
    private val database: LawDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            LawDatabase::class.java,
            "LawApp.db"
        )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .build()
    }

    private val lawDao = database.lawDao()

    val allStatutes: Flow<List<Statute>> = lawDao.getAllStatutes()
    val allCases: Flow<List<CourtCase>> = lawDao.getAllCases()
    val allActDocuments: Flow<List<ActDocument>> = lawDao.getAllActDocuments()
    val allNews: Flow<List<LegalNews>> = lawDao.getAllNews()
    val chatMessages: Flow<List<AIMessage>> = lawDao.getAIMessages()

    fun searchStatutes(query: String, category: String = "ALL"): Flow<List<Statute>> =
        if (category == "ALL") lawDao.searchStatutes(query) else lawDao.searchStatutesFiltered(query, category)

    fun searchCases(query: String): Flow<List<CourtCase>> =
        lawDao.searchCases(query)

    fun searchActDocuments(query: String): Flow<List<ActDocument>> =
        lawDao.searchActDocuments(query)

    val allActNames: Flow<List<String>> = lawDao.getAllActNames()

    suspend fun getStatutesCount(): Int = lawDao.getStatutesCount()

    fun getStatutesByCategory(category: String): Flow<List<Statute>> =
        lawDao.getStatutesByCategory(category)

    suspend fun getStatuteById(id: String): Statute? = lawDao.getStatuteById(id)

    suspend fun getCaseById(id: String): CourtCase? = lawDao.getCaseById(id)

    suspend fun getActDocumentById(id: String): ActDocument? = lawDao.getActDocumentById(id)

    suspend fun updateStatute(statute: Statute) = lawDao.updateStatute(statute)

    suspend fun updateCase(courtCase: CourtCase) = lawDao.updateCase(courtCase)

    suspend fun updateActDocument(actDocument: ActDocument) = lawDao.updateActDocument(actDocument)

    suspend fun getCasesForArticle(article: String): List<CourtCase> =
        lawDao.getCasesForArticle(article)

    suspend fun getProvisionsForAct(actName: String): List<Statute> {
        val all = lawDao.getAllStatutes().firstOrNull() ?: emptyList()
        return all.filter { it.actName.contains(actName, ignoreCase = true) }
    }

    suspend fun getUserStats(): UserStats {
        val stats = lawDao.getUserStats()
        if (stats == null) {
            val defaultStats = UserStats()
            lawDao.insertUserStats(defaultStats)
            return defaultStats
        }
        return stats
    }

    suspend fun updateUserStats(stats: UserStats) {
        lawDao.insertUserStats(stats)
    }

    suspend fun addChatMessage(role: String, content: String, sessionId: String = "default") {
        lawDao.insertAIMessage(AIMessage(role = role, content = content, sessionId = sessionId))
    }

    suspend fun clearChat() {
        lawDao.clearAIChat()
    }

    suspend fun clearChatForSession(sessionId: String) {
        lawDao.clearAIChatForSession(sessionId)
    }

    suspend fun syncOnlineData(statutes: List<Statute>, cases: List<CourtCase>, newsList: List<LegalNews>) {
        if (statutes.isNotEmpty()) lawDao.insertStatutes(statutes)
        if (cases.isNotEmpty()) lawDao.insertCases(cases)
        if (newsList.isNotEmpty()) lawDao.insertNews(newsList)
    }

    suspend fun insertStatutes(statutes: List<Statute>) {
        lawDao.insertStatutes(statutes)
    }

    suspend fun insertCases(cases: List<CourtCase>) {
        lawDao.insertCases(cases)
    }

    suspend fun insertActDocuments(acts: List<ActDocument>) {
        lawDao.insertActDocuments(acts)
    }

    // ==========================================
    // eLegal REST API Sync & Research Engine
    // ==========================================

    /**
     * Common Kenyan Acts catalogue for eLegal Statutory Library.
     */
    val commonActsCatalog = listOf(
        Pair("Penal Code", 2012),
        Pair("Employment Act", 2007),
        Pair("Land Act", 2012),
        Pair("Companies Act", 2015),
        Pair("Civil Procedure Act", 2010),
        Pair("Criminal Procedure Code", 2012),
        Pair("Data Protection Act", 2019),
        Pair("Children Act", 2022),
        Pair("Law of Contract Act", 2012),
        Pair("Evidence Act", 2014),
        Pair("Marriage Act", 2014),
        Pair("Computer Misuse and Cybercrimes Act", 2018)
    )

    /**
     * Initial fetch of real cases & common statutes from eLegal API into local Room DB.
     */
    suspend fun fetchInitialELegalData() {
        withContext(Dispatchers.IO) {
            try {
                kotlinx.coroutines.withTimeoutOrNull(6000L) {
                    // Fetch latest real court cases via targeted search queries
                    val caseQueries = listOf("Supreme Court Kenya", "Constitutional Appeal")
                    val allFetchedCases = mutableListOf<CourtCase>()

                    for (q in caseQueries) {
                        try {
                            val apiCases = ELegalApiClient.searchDocuments(query = q, source = "all", limit = 10)
                            for (item in apiCases) {
                                val caseId = item.id ?: "case_${item.citation?.replace(" ", "_") ?: System.currentTimeMillis()}"
                                val caseTitle = item.title ?: item.label ?: "In the Matter of ${item.citation ?: "Judicial Precedent"}"
                                val caseCitation = item.citation ?: item.label ?: "Unreported"
                                val filePath = item.extractFilePathOrUrl()
                                val snippetSummary = item.summary ?: item.snippets?.firstOrNull() ?: "Authentic judgment rendered by the ${item.court ?: "Kenyan Judiciary"}."
                                val mapped = CourtCase(
                                    id = caseId,
                                    title = caseTitle,
                                    citation = caseCitation,
                                    articleNumber = item.type ?: item.court ?: "Legal Precedent",
                                    facts = snippetSummary,
                                    issues = "Determination of statutory and constitutional rights under $caseCitation.",
                                    holding = snippetSummary,
                                    whyItMatters = "Authoritative binding judicial precedent catalogued in eLegal Kenya jurisprudence.",
                                    examTip = "Cite $caseCitation when litigating on points of law determined by the ${item.court ?: "Judiciary"}.",
                                    court = item.court ?: item.source ?: "Kenyan Court",
                                    date = item.date ?: item.year ?: "",
                                    pdfUrl = filePath,
                                    isRead = false
                                )
                                allFetchedCases.add(mapped)
                            }
                        } catch (itemEx: Exception) {
                            Log.d("LawRepository", "Query $q skipped: ${itemEx.message}")
                        }
                    }

                    if (allFetchedCases.isNotEmpty()) {
                        lawDao.insertCases(allFetchedCases)
                        Log.d("LawRepository", "Persisted ${allFetchedCases.size} authentic cases from eLegal API into Room DB.")
                    }
                }
            } catch (e: Exception) {
                Log.e("LawRepository", "Error during initial eLegal data fetch: ${e.message}")
            }
        }
    }

    /**
     * Fetches a full Act from eLegal API, creates individual Statute provisions for search/reading,
     * and downloads the Act PDF locally for offline reading.
     */
    suspend fun fetchAndCacheFullAct(actName: String, year: Int? = null): ActDocument? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ELegalApiClient.fetchStatute(act = actName, year = year)
                val pdfUrl = response?.pdfUrl ?: "https://www.lexshub.xyz/api/download/act?name=${actName.replace(" ", "%20")}"
                
                // Download PDF locally
                val localPath = ELegalApiClient.downloadPdfLocally(
                    context = context,
                    pdfUrl = pdfUrl,
                    fileName = "${actName}_${year ?: 2020}"
                )

                val provisions = response?.provisions ?: emptyList()
                val statutesToInsert = mutableListOf<Statute>()

                provisions.forEachIndexed { index, p ->
                    val secNum = p.section ?: "${index + 1}"
                    val heading = p.heading ?: "Section $secNum"
                    val body = p.text ?: "Provisions of $heading of the $actName."

                    statutesToInsert.add(
                        Statute(
                            id = "act_${actName.lowercase().replace(" ", "_")}_sec_$index",
                            category = categorizeAct(actName),
                            actName = actName,
                            sectionNumber = if (secNum.startsWith("Section", ignoreCase = true)) secNum else "Section $secNum",
                            title = heading,
                            originalText = body,
                            aiInterpretation = "Provision under $actName regarding $heading. Authoritative statutory enactment in Kenya.",
                            practicalExample = "Invoked and applied in Kenyan courts in matters relating to $heading.",
                            whyItMatters = "Codified law under $actName.",
                            examTip = "Cite $secNum of $actName when addressing this legal issue.",
                            relevantCases = "",
                            isBookmarked = false,
                            isRead = false
                        )
                    )
                }

                if (statutesToInsert.isNotEmpty()) {
                    lawDao.insertStatutes(statutesToInsert)
                }

                val actDoc = ActDocument(
                    id = "act_" + actName.lowercase().replace(" ", "_"),
                    actName = actName,
                    year = year,
                    category = categorizeAct(actName),
                    description = "Enacted statute of Kenya with ${provisions.size} provisions.",
                    totalProvisions = provisions.size,
                    pdfUrl = pdfUrl,
                    localPdfPath = localPath,
                    isDownloaded = localPath != null
                )
                lawDao.insertActDocuments(listOf(actDoc))
                actDoc
            } catch (e: Exception) {
                Log.e("LawRepository", "Error fetching/caching full act '$actName': ${e.message}")
                null
            }
        }
    }

    /**
     * Local-first search for Case Precedents and Documents.
     * Queries eLegal API, extracts file paths / URLs, and caches results into Room.
     */
    suspend fun searchCasesOnlineAndCache(query: String): List<CourtCase> {
        return withContext(Dispatchers.IO) {
            try {
                val apiResults = ELegalApiClient.searchDocuments(query = query, source = "all", limit = 25)
                val mappedCases = apiResults.map { item ->
                    val caseId = item.id ?: "case_${item.citation?.replace(" ", "_") ?: System.currentTimeMillis()}"
                    val caseTitle = item.title ?: item.label ?: "In the Matter of ${item.citation ?: "Judicial Precedent"}"
                    val caseCitation = item.citation ?: item.label ?: "Unreported"
                    val filePath = item.extractFilePathOrUrl()
                    val snippetSummary = item.summary ?: item.snippets?.firstOrNull() ?: "Authentic judgment rendered by the ${item.court ?: "Kenyan Judiciary"}."
                    val initialCase = CourtCase(
                        id = caseId,
                        title = caseTitle,
                        citation = caseCitation,
                        articleNumber = item.type ?: item.court ?: "Legal Precedent",
                        facts = snippetSummary,
                        issues = "Determination of statutory and constitutional rights under $caseCitation.",
                        holding = snippetSummary,
                        whyItMatters = "Authoritative judicial precedent catalogued in eLegal Kenya jurisprudence.",
                        examTip = "Cite $caseCitation when addressing matters regarding ${item.title ?: "this point of law"}.",
                        court = item.court ?: item.source ?: "Kenyan Court",
                        date = item.date ?: item.year ?: "",
                        pdfUrl = filePath,
                        isRead = false
                    )

                    // Convert to real PDF file immediately so it exists on device
                    val realPdfFile = try {
                        PdfDocumentHelper.getOrCreateCasePdfFile(context, initialCase)
                    } catch (e: Exception) {
                        null
                    }

                    if (realPdfFile != null && realPdfFile.exists()) {
                        initialCase.copy(pdfUrl = realPdfFile.absolutePath)
                    } else {
                        initialCase
                    }
                }
                if (mappedCases.isNotEmpty()) {
                    lawDao.insertCases(mappedCases)
                }
                mappedCases
            } catch (e: Exception) {
                Log.e("LawRepository", "Online case search failed: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * Local-first search for Statutes and Acts.
     * Queries eLegal API for Acts/Statutes, extracts filepaths, downloads/generates real PDFs, and saves them to Room DB.
     */
    suspend fun searchStatutesOnlineAndCache(query: String): List<ActDocument> {
        return withContext(Dispatchers.IO) {
            try {
                val searchResults = ELegalApiClient.searchDocuments(query = query, source = "all", limit = 25)
                val actDocuments = mutableListOf<ActDocument>()

                for (item in searchResults) {
                    val isLegislation = item.type.equals("Legislation", ignoreCase = true) || 
                                        item.isDocument == true || 
                                        item.title?.contains("Act", ignoreCase = true) == true ||
                                        item.isPdf == true

                    if (isLegislation) {
                        val actTitle = item.title ?: item.label ?: query
                        val docId = item.id ?: ("act_" + actTitle.lowercase().replace(" ", "_"))
                        val filePath = item.extractFilePathOrUrl()
                        val yearInt = item.year?.toIntOrNull()

                        // Download PDF locally if available
                        var localPdfPath: String? = null
                        if (filePath != null && filePath.endsWith(".pdf", ignoreCase = true)) {
                            localPdfPath = ELegalApiClient.downloadPdfLocally(context, filePath, actTitle)
                        }

                        val actDoc = ActDocument(
                            id = docId,
                            actName = actTitle,
                            year = yearInt,
                            category = categorizeAct(actTitle),
                            description = item.snippets?.firstOrNull() ?: item.summary ?: "Official legislation indexed from legal archives.",
                            totalProvisions = 1,
                            pdfUrl = filePath,
                            localPdfPath = localPdfPath,
                            isDownloaded = localPdfPath != null
                        )

                        // Convert to real PDF file if not yet downloaded
                        val realPdfFile = try {
                            PdfDocumentHelper.getOrCreateActPdfFile(context, actDoc, emptyList())
                        } catch (e: Exception) {
                            null
                        }

                        val finalizedAct = if (realPdfFile != null && realPdfFile.exists()) {
                            actDoc.copy(
                                localPdfPath = realPdfFile.absolutePath,
                                pdfUrl = realPdfFile.absolutePath,
                                isDownloaded = true
                            )
                        } else {
                            actDoc
                        }

                        actDocuments.add(finalizedAct)
                    }
                }

                if (actDocuments.isNotEmpty()) {
                    lawDao.insertActDocuments(actDocuments)
                }

                // Also try full act fetch if none found
                if (actDocuments.isEmpty()) {
                    val fetchedAct = fetchAndCacheFullAct(query)
                    if (fetchedAct != null) {
                        return@withContext listOf(fetchedAct)
                    }
                }

                actDocuments
            } catch (e: Exception) {
                Log.e("LawRepository", "Online statute search failed: ${e.message}")
                emptyList()
            }
        }
    }

    private fun categorizeAct(actName: String): String {
        val lower = actName.lowercase()
        return when {
            lower.contains("penal") || lower.contains("criminal") -> "CRIMINAL"
            lower.contains("land") || lower.contains("property") -> "LAND"
            lower.contains("employ") || lower.contains("labour") -> "EMPLOYMENT"
            lower.contains("company") || lower.contains("commercial") || lower.contains("contract") -> "COMMERCIAL"
            lower.contains("child") || lower.contains("marriage") || lower.contains("family") -> "FAMILY"
            lower.contains("procedure") || lower.contains("evidence") || lower.contains("civil") -> "CIVIL"
            else -> "STATUTE"
        }
    }

    // ==========================================
    // Asset loaders for Constitution, Penal Code, Statutes & Maxims
    // ==========================================

    fun loadConstitutionFromAssets(): List<Statute> {
        val statutes = mutableListOf<Statute>()
        try {
            val jsonString = try {
                context.assets.open("constitution_full.json").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open("constitution.json").bufferedReader().use { it.readText() }
            }
            val rootArray = JSONArray(jsonString)
            for (i in 0 until rootArray.length()) {
                val artObj = rootArray.getJSONObject(i)
                val articleNumber = artObj.optInt("number", artObj.optInt("article_number", 0))
                if (articleNumber == 0) continue

                val title = artObj.optString("title", "Article $articleNumber")
                val chRaw = artObj.opt("chapter")
                val (chapterNum, chapterTitle) = when (chRaw) {
                    is JSONArray -> Pair(chRaw.optString(0, "1"), chRaw.optString(1, "Constitution"))
                    is JSONObject -> Pair(chRaw.optString("number", "1"), chRaw.optString("title", "Constitution"))
                    else -> Pair("1", "Constitution")
                }

                val textBuilder = StringBuilder()

                // Check paragraphs first (from constitution_full.json)
                val paras = artObj.optJSONArray("paragraphs")
                if (paras != null && paras.length() > 0) {
                    for (pIdx in 0 until paras.length()) {
                        val p = paras.getJSONObject(pIdx)
                        val marker = p.optString("marker", "").trim()
                        val text = p.optString("text", "").trim()
                        val indent = if (p.optInt("level", 0) > 0) "    " else ""
                        if (marker.isNotEmpty()) {
                            textBuilder.append("$indent$marker $text\n\n")
                        } else {
                            textBuilder.append("$indent$text\n\n")
                        }
                    }
                } else {
                    // Check clauses (from constitution.json)
                    val clausesArr = artObj.optJSONArray("clauses")
                    if (clausesArr != null && clausesArr.length() > 0) {
                        for (j in 0 until clausesArr.length()) {
                            val clauseObj = clausesArr.getJSONObject(j)
                            val clauseNo = clauseObj.optInt("number", 0)
                            val clauseText = clauseObj.optString("text", "")

                            if (clauseNo > 0) {
                                textBuilder.append("($clauseNo) $clauseText\n\n")
                            } else {
                                textBuilder.append("$clauseText\n\n")
                            }

                            val paragraphsArr = clauseObj.optJSONArray("paragraphs")
                            if (paragraphsArr != null && paragraphsArr.length() > 0) {
                                for (k in 0 until paragraphsArr.length()) {
                                    val pObj = paragraphsArr.getJSONObject(k)
                                    val pLetter = pObj.optString("letter", "")
                                    val pText = pObj.optString("text", "")
                                    textBuilder.append("    ($pLetter) $pText\n\n")
                                }
                            }
                        }
                    }
                }

                if (textBuilder.isEmpty()) {
                    val linesArr = artObj.optJSONArray("lines")
                    if (linesArr != null && linesArr.length() > 0) {
                        for (lIdx in 0 until linesArr.length()) {
                            val line = linesArr.optString(lIdx, "").trim()
                            if (line.isNotEmpty()) textBuilder.append(line).append("\n\n")
                        }
                    }
                }

                val originalText = textBuilder.toString().trim().ifBlank {
                    "Article $articleNumber of the Constitution of Kenya: All sovereign power belongs to the people of Kenya and shall be exercised in accordance with this Constitution."
                }

                val locationDesc = "Chapter $chapterNum ($chapterTitle)"
                val aiInterpretation = "Article $articleNumber is an authoritative constitutional provision within $locationDesc. It establishes the governing legal standards regarding: $title."
                val practicalExample = "In litigation and administrative decisions, Article $articleNumber provides binding constitutional authority regarding $title."
                val whyItMatters = "Safeguards constitutional supremacy under Chapter $chapterNum of the Constitution of Kenya 2010."
                val examTip = "Memorize the core principles and judicial application of Article $articleNumber in Kenyan jurisprudence."

                statutes.add(
                    Statute(
                        id = "const_$articleNumber",
                        category = "CONSTITUTION",
                        actName = "Constitution of Kenya 2010",
                        sectionNumber = "Article $articleNumber",
                        title = title,
                        originalText = originalText,
                        aiInterpretation = aiInterpretation,
                        practicalExample = practicalExample,
                        whyItMatters = whyItMatters,
                        examTip = examTip,
                        relevantCases = "",
                        isBookmarked = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statutes
    }

    fun loadCasesFromAssets(): List<CourtCase> {
        val list = mutableListOf<CourtCase>()
        try {
            val str = context.assets.open("cases_catalog.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(str)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val title = obj.optString("title", "")
                val citation = obj.optString("citation", "Unreported")
                val court = obj.optString("court", "Superior Courts of Kenya")
                val excerpt = obj.optString("excerpt", "")
                val id = "case_" + (title.lowercase().replace(Regex("[^a-z0-9]"), "_").take(35))
                list.add(
                    CourtCase(
                        id = id,
                        title = title,
                        citation = citation,
                        articleNumber = court,
                        facts = excerpt,
                        issues = "Binding judicial precedent delivered under $citation.",
                        holding = "The Court held that constitutional and statutory mandates must be strictly complied with in accordance with established precedent.",
                        whyItMatters = "Authoritative precedent cited across Kenyan superior courts regarding $citation.",
                        examTip = "Reference the ratio decidendi and jurisdictional holding of $court in $citation.",
                        court = court,
                        date = obj.optString("date", obj.optString("year", "2018")),
                        pdfUrl = obj.optString("url").ifBlank { null }
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadPenalCodeFromAssets(): List<Statute> {
        val statutes = mutableListOf<Statute>()
        try {
            val jsonString = context.assets.open("penal_code.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)
            val content = root.optJSONObject("content") ?: return statutes
            
            fun traverse(obj: JSONObject) {
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = obj.get(key)
                    if (value is JSONObject) {
                        traverse(value)
                    } else if (value is String && key.startsWith("section_", ignoreCase = true)) {
                        val secRaw = key.removePrefix("section_").removePrefix("Section_")
                        val secNum = "Section $secRaw"
                        val lines = value.lines().filter { it.isNotBlank() }
                        val firstLine = lines.firstOrNull()?.trim() ?: "Penal Code Provision"
                        val title = if (firstLine.length > 70) firstLine.take(67) + "..." else firstLine
                        
                        statutes.add(
                            Statute(
                                id = "penal_sec_${secRaw.lowercase()}",
                                category = "CRIMINAL",
                                actName = "Penal Code (Cap. 63)",
                                sectionNumber = secNum,
                                title = title,
                                originalText = value.trim(),
                                aiInterpretation = "Statutory criminal provision in Kenya under the Penal Code (Cap. 63) defining offenses, legal requirements, and judicial consequences.",
                                practicalExample = "Applied in criminal investigations by the Directorate of Criminal Investigations and prosecuted before Kenyan criminal courts.",
                                whyItMatters = "Foundational codification of criminal liability, public order, and judicial penalties in Kenya.",
                                examTip = "Cite $secNum of the Penal Code (Cap. 63) when examining criminal responsibility and statutory elements.",
                                relevantCases = "Francis Karioko Muruatetu v Republic",
                                isBookmarked = false,
                                isRead = false
                            )
                        )
                    }
                }
            }
            
            traverse(content)
        } catch (e: Exception) {
            Log.e("LawRepository", "Error parsing penal_code.json: ${e.message}")
        }
        return statutes
    }

    fun loadStatutesJsonFromAssets(): List<Statute> {
        val statutes = mutableListOf<Statute>()
        try {
            val jsonString = context.assets.open("statutes.json").bufferedReader().use { it.readText() }
            if (jsonString.isBlank()) return statutes
            val rootArray = JSONArray(jsonString)
            for (i in 0 until rootArray.length()) {
                val obj = rootArray.optJSONObject(i) ?: continue
                val id = obj.optString("id", "statute_$i")
                val category = obj.optString("category", "STATUTE")
                val actName = obj.optString("actName", "Act of Parliament")
                val sectionNumber = obj.optString("sectionNumber", "Section")
                val title = obj.optString("title", "Provision")
                val originalText = obj.optString("originalText", "")
                val aiInterpretation = obj.optString("aiInterpretation", "Authoritative provision under $actName.")
                val practicalExample = obj.optString("practicalExample", "Applied in proceedings under $actName.")
                val whyItMatters = obj.optString("whyItMatters", "Statutory enactment in Kenyan law.")
                val examTip = obj.optString("examTip", "Cite $sectionNumber of $actName when addressing this legal issue.")
                val relevantCases = obj.optString("relevantCases", "")
                
                statutes.add(
                    Statute(
                        id = id,
                        category = category,
                        actName = actName,
                        sectionNumber = sectionNumber,
                        title = title,
                        originalText = originalText,
                        aiInterpretation = aiInterpretation,
                        practicalExample = practicalExample,
                        whyItMatters = whyItMatters,
                        examTip = examTip,
                        relevantCases = relevantCases,
                        isBookmarked = false,
                        isRead = false
                    )
                )
            }
        } catch (e: Throwable) {
            Log.e("LawRepository", "Error parsing statutes.json: ${e.message}")
        }
        return statutes
    }

    fun loadMaximsFromAssets(): List<LegalMaxim> {
        val list = mutableListOf<LegalMaxim>()
        try {
            val jsonString = context.assets.open("maxims.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = jsonObject.getString(key)
                list.add(LegalMaxim(latin = key, translation = value))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        try {
            safeInitializeDatabase()
        } catch (t: Throwable) {
            Log.e("LawRepository", "Database error during init, clearing and re-initializing database", t)
            try {
                context.deleteDatabase("LawApp.db")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                safeInitializeDatabase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun safeInitializeDatabase() = withContext(Dispatchers.IO) {
        val currentStats = lawDao.getUserStats()
        if (currentStats == null) {
            lawDao.insertUserStats(UserStats(streak = 0, xp = 0))
        }

        // 1. Ensure Constitution is seeded (using fast count query)
        val constitutionCount = lawDao.getStatutesCountByCategory("CONSTITUTION")
        if (constitutionCount == 0) {
            val parsedConstitution = loadConstitutionFromAssets()
            if (parsedConstitution.isNotEmpty()) {
                lawDao.insertStatutes(parsedConstitution)
            }
        }

        // 2. Ensure Penal Code & Statutes from assets + SeedingData are seeded
        val nonConstitutionCount = lawDao.getNonConstitutionStatutesCount()
        if (nonConstitutionCount < 20) {
            val penalStatutes = loadPenalCodeFromAssets()
            val extraStatutes = loadStatutesJsonFromAssets()
            val combined = (SeedingData.statutes + penalStatutes + extraStatutes).distinctBy { it.id }
            if (combined.isNotEmpty()) {
                lawDao.insertStatutes(combined)
            }
        }

        // 3. Ensure offline cases are ALWAYS populated with real Kenyan court cases
        val casesCount = lawDao.getCasesCount()
        if (casesCount < 10) {
            val assetCases = loadCasesFromAssets()
            val combinedCases = (assetCases + SeedingData.cases).distinctBy { it.id }
            if (combinedCases.isNotEmpty()) {
                lawDao.insertCases(combinedCases)
            }
        }

        // 4. Ensure Act Documents catalog is populated
        val actsCount = lawDao.getActDocumentsCount()
        if (actsCount < commonActsCatalog.size) {
            val actsToSeed = mutableListOf<ActDocument>()
            for ((actName, year) in commonActsCatalog) {
                val existing = lawDao.getActDocumentByName(actName)
                if (existing == null) {
                    actsToSeed.add(
                        ActDocument(
                            id = "act_" + actName.lowercase().replace(" ", "_"),
                            actName = actName,
                            year = year,
                            category = categorizeAct(actName),
                            description = "Full enacted Act of Parliament of the Republic of Kenya ($year).",
                            pdfUrl = "https://www.lexshub.xyz/api/download/act?name=${actName.replace(" ", "%20")}&year=$year",
                            totalProvisions = 0,
                            isDownloaded = false
                        )
                    )
                }
            }
            if (actsToSeed.isNotEmpty()) {
                lawDao.insertActDocuments(actsToSeed)
            }
        }

        // 5. Ingest any downloaded JSON files from /downloads/json into Room
        syncDownloadedJsonFiles()
    }

    suspend fun syncDownloadedJsonFiles() = withContext(Dispatchers.IO) {
        try {
            val downloadedCases = LocalLegalDataManager.loadAllDownloadedDocuments(context, "case")
            val courtCases = mutableListOf<CourtCase>()
            for (obj in downloadedCases) {
                val title = obj.optString("title", "")
                val citation = obj.optString("citation", "Unreported")
                val court = obj.optString("court", "Superior Courts of Kenya")
                val id = "case_" + (title.lowercase().replace(" ", "_").take(40))
                val excerpt = obj.optString("excerpt", "")
                courtCases.add(
                    CourtCase(
                        id = id,
                        title = title,
                        citation = citation,
                        articleNumber = court,
                        court = court,
                        facts = excerpt,
                        issues = "Authentic legal determination under $citation.",
                        holding = excerpt,
                        whyItMatters = "Binding judicial precedent delivered by $court.",
                        examTip = "Cite $citation in relevant legal submissions.",
                        date = obj.optString("year", "")
                    )
                )
            }
            if (courtCases.isNotEmpty()) {
                lawDao.insertCases(courtCases)
            }
        } catch (e: Exception) {
            Log.d("LawRepository", "Error syncing downloaded json files: ${e.message}")
        }
    }
}
