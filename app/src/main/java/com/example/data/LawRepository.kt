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
                // 1. Fetch latest real court cases via search query
                val caseQueries = listOf("Kenya", "Supreme Court", "Constitutional", "Criminal Appeal", "Employment", "Land")
                val allFetchedCases = mutableListOf<CourtCase>()

                for (q in caseQueries) {
                    val apiCases = ELegalApiClient.searchDocuments(query = q, source = "all", limit = 15)
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
                }

                if (allFetchedCases.isNotEmpty()) {
                    lawDao.insertCases(allFetchedCases)
                    Log.d("LawRepository", "Persisted ${allFetchedCases.size} authentic cases from eLegal API into Room DB.")
                }

                // 2. Fetch and seed common Statutory Acts into local Room DB
                val actsToSeed = mutableListOf<ActDocument>()
                for ((actName, year) in commonActsCatalog) {
                    val existing = lawDao.getActDocumentByName(actName)
                    if (existing == null) {
                        val actDoc = ActDocument(
                            id = "act_" + actName.lowercase().replace(" ", "_"),
                            actName = actName,
                            year = year,
                            category = categorizeAct(actName),
                            description = "Full enacted Act of Parliament of the Republic of Kenya ($year).",
                            pdfUrl = "https://www.lexshub.xyz/api/download/act?name=${actName.replace(" ", "%20")}&year=$year",
                            totalProvisions = 0,
                            isDownloaded = false
                        )
                        actsToSeed.add(actDoc)
                    }
                }

                if (actsToSeed.isNotEmpty()) {
                    lawDao.insertActDocuments(actsToSeed)
                    Log.d("LawRepository", "Seeded ${actsToSeed.size} Act documents in Room DB.")
                }

                // 3. Proactively sync provisions and PDFs for primary common acts from eLegal API
                for (act in actsToSeed.take(4)) {
                    fetchAndCacheFullAct(act.actName, act.year)
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
            val jsonString = context.assets.open("constitution.json").bufferedReader().use { it.readText() }
            val rootArray = JSONArray(jsonString)
            for (i in 0 until rootArray.length()) {
                val artObj = rootArray.getJSONObject(i)
                val articleNumber = artObj.optInt("article_number", 0)
                if (articleNumber == 0) continue
                
                val title = artObj.optString("title", "")
                val chapterObj = artObj.optJSONObject("chapter")
                val chapterNum = chapterObj?.optString("number", "") ?: ""
                val chapterTitle = chapterObj?.optString("title", "") ?: ""
                val partObj = artObj.optJSONObject("part")
                val partTitle = partObj?.optString("title", "") ?: ""
                
                if (chapterNum.equals("EIGHTEEN", ignoreCase = true) ||
                    chapterTitle.contains("TRANSITIONAL", ignoreCase = true) ||
                    chapterTitle.contains("SCHEDULE", ignoreCase = true)) {
                    continue
                }
                
                val clausesArr = artObj.optJSONArray("clauses")
                val textBuilder = java.lang.StringBuilder()
                
                if (clausesArr != null && clausesArr.length() > 0) {
                    for (j in 0 until clausesArr.length()) {
                        val clauseObj = clausesArr.getJSONObject(j)
                        val clauseNo = clauseObj.optInt("number", 0)
                        val clauseText = clauseObj.optString("text", "")
                        
                        if (clauseNo > 0) {
                            textBuilder.append("($clauseNo) $clauseText\n")
                        } else {
                            textBuilder.append("$clauseText\n")
                        }
                        
                        val paragraphsArr = clauseObj.optJSONArray("paragraphs")
                        if (paragraphsArr != null && paragraphsArr.length() > 0) {
                            for (k in 0 until paragraphsArr.length()) {
                                val pObj = paragraphsArr.getJSONObject(k)
                                val pLetter = pObj.optString("letter", "")
                                val pText = pObj.optString("text", "")
                                textBuilder.append("    ($pLetter) $pText\n")
                            }
                        }
                    }
                } else {
                    textBuilder.append("Refer to the full text of Article $articleNumber of the Constitution of Kenya.")
                }
                
                val originalText = textBuilder.toString().trim()
                val locationDesc = if (partTitle.isNotEmpty()) {
                    "Chapter $chapterNum ($chapterTitle, Part $partTitle)"
                } else {
                    "Chapter $chapterNum ($chapterTitle)"
                }
                
                val aiInterpretation = "Article $articleNumber is a foundational constitutional provision within $locationDesc. It details and delineates the official legal parameters regarding: $title."
                val practicalExample = "In litigation involving the state or individuals, Article $articleNumber provides the official constitutional authority governing $title."
                val whyItMatters = "Maintains constitutional supremacy by safeguarding the processes and public values defined under Chapter $chapterNum of the Constitution of Kenya 2010."
                val examTip = "Memorize the core elements of Article $articleNumber. Understand how this relates to democratic principles and limitation tests in Article 24."
                
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

    suspend fun initializeDatabase() {
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

    private suspend fun safeInitializeDatabase() {
        val currentStats = lawDao.getUserStats()
        if (currentStats == null) {
            lawDao.insertUserStats(UserStats(streak = 0, xp = 0))
        }

        // 1. Ensure Constitution is seeded
        val existingStatutes = lawDao.getAllStatutes().firstOrNull() ?: emptyList()
        val hasConstitution = existingStatutes.any { it.category == "CONSTITUTION" }
        if (!hasConstitution) {
            val parsedConstitution = loadConstitutionFromAssets()
            if (parsedConstitution.isNotEmpty()) {
                lawDao.insertStatutes(parsedConstitution)
            }
        }

        // 2. Ensure Penal Code & Statutes from assets + SeedingData are seeded
        val nonConstitutionCount = existingStatutes.count { it.category != "CONSTITUTION" }
        if (nonConstitutionCount < 20) {
            val penalStatutes = loadPenalCodeFromAssets()
            val extraStatutes = loadStatutesJsonFromAssets()
            val combined = (SeedingData.statutes + penalStatutes + extraStatutes).distinctBy { it.id }
            if (combined.isNotEmpty()) {
                lawDao.insertStatutes(combined)
            }
        }

        // 3. Ensure offline cases are ALWAYS populated
        val casesCount = lawDao.getCasesCount()
        if (casesCount < 10) {
            lawDao.insertCases(SeedingData.cases)
        }

        // 4. Ensure Act Documents catalog is populated
        val currentAllStatutes = lawDao.getAllStatutes().firstOrNull() ?: emptyList()
        val actsToSeed = mutableListOf<ActDocument>()
        for ((actName, year) in commonActsCatalog) {
            val existing = lawDao.getActDocumentByName(actName)
            val matchingProvisionsCount = currentAllStatutes.count { it.actName.contains(actName, ignoreCase = true) }
            if (existing == null) {
                actsToSeed.add(
                    ActDocument(
                        id = "act_" + actName.lowercase().replace(" ", "_"),
                        actName = actName,
                        year = year,
                        category = categorizeAct(actName),
                        description = "Full enacted Act of Parliament of the Republic of Kenya ($year).",
                        pdfUrl = "https://www.lexshub.xyz/api/download/act?name=${actName.replace(" ", "%20")}&year=$year",
                        totalProvisions = matchingProvisionsCount,
                        isDownloaded = false
                    )
                )
            } else if (existing.totalProvisions == 0 && matchingProvisionsCount > 0) {
                lawDao.insertActDocuments(listOf(existing.copy(totalProvisions = matchingProvisionsCount)))
            }
        }
        if (actsToSeed.isNotEmpty()) {
            lawDao.insertActDocuments(actsToSeed)
        }

        // 5. In background, attempt to enrich with eLegal API if online
        try {
            fetchInitialELegalData()
        } catch (e: Exception) {
            Log.e("LawRepository", "Background eLegal data fetch skipped: ${e.message}")
        }
    }
}
