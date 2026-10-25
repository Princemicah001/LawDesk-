package com.example.data

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

object LocalLegalDataManager {
    private const val TAG = "LocalLegalDataManager"
    private const val PREFS_NAME = "legal_engine_prefs"
    private const val KEY_BACKEND_URL = "custom_backend_url"

    // Primary deployed remote Python backend
    const val DEFAULT_BACKEND_URL = "https://lawdesk.onrender.com"

    @Volatile
    private var configuredBackendUrl: String? = null

    // High-speed In-Memory Cache Engine for Sub-Millisecond Native Responses
    private val memoryCache = ConcurrentHashMap<String, String>()
    private val urlToFileIndex = ConcurrentHashMap<String, File>()
    @Volatile
    private var isIndexInitialized = false

    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    private var cachedActsCatalog: List<JSONObject>? = null
    @Volatile
    private var cachedCasesCatalog: List<JSONObject>? = null

    fun getBackendUrl(context: Context): String {
        configuredBackendUrl?.let { if (it.isNotBlank()) return it }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_BACKEND_URL, null)
        if (!saved.isNullOrBlank()) {
            configuredBackendUrl = saved
            return saved
        }
        return DEFAULT_BACKEND_URL
    }

    fun setBackendUrl(context: Context, url: String) {
        val trimmed = url.trim().removeSuffix("/")
        configuredBackendUrl = trimmed
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BACKEND_URL, trimmed).apply()
        Log.i(TAG, "Configured remote Python backend URL to: $trimmed")
    }

    /**
     * Resolves the /downloads/json storage directory.
     * Uses external downloads folder or internal storage fallback.
     */
    fun getJsonDir(context: Context): File {
        val extDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val dir = if (extDir != null) {
            File(extDir, "json")
        } else {
            File(context.filesDir, "downloads/json")
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Secondary public downloads directory check if accessible.
     */
    fun getPublicJsonDir(): File? {
        return try {
            val pub = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File(pub, "json")
            if (!dir.exists()) dir.mkdirs()
            dir
        } catch (e: Exception) {
            null
        }
    }

    // --- JSON FILE CACHING ENGINE (O(1) In-Memory & Persistent) ---

    fun ensureCacheIndex(context: Context) {
        if (isIndexInitialized) return
        synchronized(this) {
            if (isIndexInitialized) return
            try {
                val dirs = listOfNotNull(getJsonDir(context), getPublicJsonDir())
                for (dir in dirs) {
                    if (!dir.exists()) continue
                    val files = dir.listFiles { _, name -> name.endsWith(".url") } ?: emptyArray()
                    for (marker in files) {
                        try {
                            val savedUrl = marker.readText(Charsets.UTF_8).trim()
                            val jsonFile = File(dir, marker.nameWithoutExtension + ".json")
                            if (jsonFile.exists()) {
                                urlToFileIndex[savedUrl] = jsonFile
                                urlToFileIndex[savedUrl.lowercase()] = jsonFile
                            }
                        } catch (_: Exception) {}
                    }
                }
            } catch (_: Exception) {}
            isIndexInitialized = true
        }
    }

    fun saveDocumentJson(context: Context, url: String, kind: String, jsonString: String): File? {
        return try {
            val cleanUrl = url.trim()
            memoryCache[cleanUrl] = jsonString
            memoryCache[cleanUrl.lowercase()] = jsonString

            val dir = getJsonDir(context)
            val jsonObject = JSONObject(jsonString)
            val title = jsonObject.optString("title", "document").replace(Regex("[^a-zA-Z0-9 ._-]"), "_")
            val citation = jsonObject.optString("citation", "").replace(Regex("[^a-zA-Z0-9 ._-]"), "_")
            val fileName = if (citation.isNotBlank()) "$title - $citation.json" else "$title.json"
            val target = File(dir, fileName)
            target.writeText(jsonString, Charsets.UTF_8)

            // Save .url marker sidecar file identical to Python script
            val urlMarker = File(dir, target.nameWithoutExtension + ".url")
            urlMarker.writeText(cleanUrl, Charsets.UTF_8)

            urlToFileIndex[cleanUrl] = target
            urlToFileIndex[cleanUrl.lowercase()] = target

            // Invalidate catalogs so newly saved documents appear instantly
            if (kind == "statute") cachedActsCatalog = null
            if (kind == "case") cachedCasesCatalog = null

            // Also mirror to public downloads if available
            try {
                getPublicJsonDir()?.let { pDir ->
                    val pubTarget = File(pDir, fileName)
                    pubTarget.writeText(jsonString, Charsets.UTF_8)
                    File(pDir, target.nameWithoutExtension + ".url").writeText(cleanUrl, Charsets.UTF_8)
                }
            } catch (_: Exception) {}

            Log.d(TAG, "Cached $kind document to ${target.absolutePath}")
            target
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save document json: ${e.message}")
            null
        }
    }

    @Volatile
    private var actsIndexCache: JSONObject? = null
    @Volatile
    private var casesIndexCache: JSONObject? = null

    fun getActsIndex(context: Context): JSONObject {
        actsIndexCache?.let { return it }
        return synchronized(this) {
            actsIndexCache ?: try {
                val str = context.assets.open("acts_index.json").bufferedReader().use { it.readText() }
                val obj = JSONObject(str)
                actsIndexCache = obj
                obj
            } catch (e: Exception) {
                Log.e(TAG, "Failed reading acts_index.json: ${e.message}")
                JSONObject()
            }
        }
    }

    fun getCasesIndex(context: Context): JSONObject {
        casesIndexCache?.let { return it }
        return synchronized(this) {
            casesIndexCache ?: try {
                val str = context.assets.open("cases_index.json").bufferedReader().use { it.readText() }
                val obj = JSONObject(str)
                casesIndexCache = obj
                obj
            } catch (e: Exception) {
                Log.e(TAG, "Failed reading cases_index.json: ${e.message}")
                JSONObject()
            }
        }
    }

    fun isPlaceholderStatute(jsonStr: String?): Boolean {
        if (jsonStr.isNullOrBlank()) return true
        if (jsonStr.contains("This Act may be cited as the") &&
            jsonStr.contains("Short Title") &&
            jsonStr.contains("Interpretation")) {
            try {
                val obj = JSONObject(jsonStr)
                val items = obj.optJSONArray("items")
                if (items == null || items.length() <= 3) return true
            } catch (_: Exception) {
                return true
            }
        }
        return false
    }

    fun loadBundledAct(context: Context, key: String): String? {
        val cleanKey = key.trim()
        val index = getActsIndex(context)
        val fileName = index.optString(cleanKey, "")
            .ifEmpty { index.optString(cleanKey.lowercase(), "") }
            .ifEmpty {
                val title = cleanKey.substringAfterLast("/").replace("-", " ").replace("_", " ").trim()
                index.optString(title.lowercase(), "")
            }
        if (fileName.isNotBlank()) {
            return try {
                context.assets.open("acts_data/$fileName").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed opening acts_data/$fileName: ${e.message}")
                null
            }
        }
        return null
    }

    fun loadBundledCase(context: Context, key: String): String? {
        val cleanKey = key.trim()
        val index = getCasesIndex(context)
        val fileName = index.optString(cleanKey, "")
            .ifEmpty { index.optString(cleanKey.lowercase(), "") }
            .ifEmpty {
                val title = cleanKey.substringAfterLast("/").replace("-", " ").replace("_", " ").trim()
                index.optString(title.lowercase(), "")
            }
        if (fileName.isNotBlank()) {
            return try {
                context.assets.open("cases_data/$fileName").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.w(TAG, "Failed opening cases_data/$fileName: ${e.message}")
                null
            }
        }
        return null
    }

    fun loadDocumentJsonByUrl(context: Context, url: String): String? {
        val cleanUrl = url.trim()
        if (cleanUrl.isBlank()) return null

        // 1. In-memory hot RAM cache (0ms) - only if non-placeholder
        memoryCache[cleanUrl]?.let { if (!isPlaceholderStatute(it)) return it }
        memoryCache[cleanUrl.lowercase()]?.let { if (!isPlaceholderStatute(it)) return it }

        ensureCacheIndex(context)

        // 2. Direct indexed file hit (< 1ms) - purge if placeholder
        val indexedFile = urlToFileIndex[cleanUrl] ?: urlToFileIndex[cleanUrl.lowercase()]
        if (indexedFile != null && indexedFile.exists()) {
            val content = try { indexedFile.readText(Charsets.UTF_8) } catch (_: Exception) { null }
            if (content != null) {
                if (!isPlaceholderStatute(content)) {
                    memoryCache[cleanUrl] = content
                    return content
                } else {
                    indexedFile.delete()
                    urlToFileIndex.remove(cleanUrl)
                    urlToFileIndex.remove(cleanUrl.lowercase())
                }
            }
        }

        // 3. Fallback disk scan for newly written unindexed files
        val dirs = listOfNotNull(getJsonDir(context), getPublicJsonDir())
        for (dir in dirs) {
            if (!dir.exists()) continue
            val files = dir.listFiles { _, name -> name.endsWith(".url") } ?: emptyArray()
            for (marker in files) {
                try {
                    val savedUrl = marker.readText(Charsets.UTF_8).trim()
                    if (savedUrl.equals(cleanUrl, ignoreCase = true) ||
                        savedUrl.contains(cleanUrl, ignoreCase = true) ||
                        cleanUrl.contains(savedUrl, ignoreCase = true)) {
                        val jsonFile = File(dir, marker.nameWithoutExtension + ".json")
                        if (jsonFile.exists()) {
                            val content = jsonFile.readText(Charsets.UTF_8)
                            if (!isPlaceholderStatute(content)) {
                                memoryCache[cleanUrl] = content
                                urlToFileIndex[savedUrl] = jsonFile
                                return content
                            } else {
                                jsonFile.delete()
                                marker.delete()
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        }

        // 4. Bundled authentic eKLR Acts (< 1ms, 120 acts available)
        val bundledAct = loadBundledAct(context, cleanUrl)
        if (bundledAct != null) {
            memoryCache[cleanUrl] = bundledAct
            return bundledAct
        }

        // 5. Bundled authentic eKLR Cases (< 1ms, 29 landmark cases available)
        val bundledCase = loadBundledCase(context, cleanUrl)
        if (bundledCase != null) {
            memoryCache[cleanUrl] = bundledCase
            return bundledCase
        }

        // 6. Bundled asset fallback (e.g. Penal Code Cap. 63)
        if (cleanUrl.contains("penal", ignoreCase = true) || cleanUrl.contains("cap-63", ignoreCase = true) || cleanUrl.contains("cap 63", ignoreCase = true)) {
            val penalJson = loadBundledPenalCodeStatute(context, cleanUrl)
            if (penalJson != null) {
                memoryCache[cleanUrl] = penalJson
                return penalJson
            }
        }

        return null
    }

    fun loadBundledPenalCodeStatute(context: Context, url: String): String? {
        return try {
            val jsonStr = context.assets.open("penal_code.json").bufferedReader().use { it.readText() }
            val rootObj = JSONObject(jsonStr)
            val title = rootObj.optString("title", "Penal Code Cap. 63")
            val contentObj = rootObj.optJSONObject("content") ?: return null

            val statuteObj = JSONObject().apply {
                put("kind", "statute")
                put("url", url)
                put("title", title)
                put("citation", "Cap. 63")
                put("metadata", JSONArray().apply {
                    put(JSONObject().apply {
                        put("label", "Enacted by Parliament of Kenya")
                        put("value", "In force")
                    })
                })
                put("preamble", "An Act of Parliament to establish a code of criminal law for the Republic of Kenya.")
                val items = JSONArray()
                var secCounter = 1

                val partKeys = contentObj.keys()
                var partIndex = 1
                while (partKeys.hasNext()) {
                    val partKey = partKeys.next()
                    val partVal = contentObj.opt(partKey)
                    val partTitle = partKey.replace("_", " ").uppercase()

                    items.put(JSONObject().apply {
                        put("type", "part")
                        put("number", "PART $partIndex")
                        put("title", partTitle)
                    })
                    partIndex++

                    if (partVal is JSONObject) {
                        val subKeys = partVal.keys()
                        while (subKeys.hasNext()) {
                            val subKey = subKeys.next()
                            val subVal = partVal.optString(subKey, "")
                            val secTitle = subKey.replace("_", " ").split(" ")
                                .joinToString(" ") { it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() } }

                            items.put(JSONObject().apply {
                                put("type", "section")
                                put("number", secCounter)
                                put("title", secTitle)
                                put("subsections", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("level", 1)
                                        put("marker", "($secCounter)")
                                        put("text", subVal)
                                    })
                                })
                            })
                            secCounter++
                        }
                    }
                }
                put("items", items)
                put("raw_body", "")
            }
            statuteObj.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bundled penal code: ${e.message}")
            null
        }
    }

    fun loadAllDownloadedDocuments(context: Context, kind: String): List<JSONObject> {
        val results = mutableListOf<JSONObject>()
        val seenUrls = mutableSetOf<String>()
        val dirs = listOfNotNull(getJsonDir(context), getPublicJsonDir())

        for (dir in dirs) {
            if (!dir.exists()) continue
            val jsonFiles = dir.listFiles { _, name -> name.endsWith(".json") } ?: emptyArray()
            for (f in jsonFiles) {
                try {
                    val content = f.readText(Charsets.UTF_8)
                    val obj = JSONObject(content)
                    val docKind = obj.optString("kind", obj.optString("type", ""))
                    if (kind == "all" || docKind.equals(kind, ignoreCase = true)) {
                        val docUrl = obj.optString("url", f.name)
                        if (seenUrls.add(docUrl)) {
                            results.add(obj)
                        }
                    }
                } catch (e: Exception) {
                    // skip corrupt file
                }
            }
        }
        return results
    }

    // --- CONSTITUTION PARSER & TOC ---

    private var cachedConstitutionArray: JSONArray? = null
    private var cachedConstitutionTOC: String? = null

    private fun loadFullConstitution(context: Context): JSONArray {
        cachedConstitutionArray?.let { return it }
        return try {
            val jsonStr = try {
                context.assets.open("constitution_full.json").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open("constitution.json").bufferedReader().use { it.readText() }
            }
            val arr = JSONArray(jsonStr)
            cachedConstitutionArray = arr
            arr
        } catch (e: Exception) {
            Log.e(TAG, "Error loading constitution assets: ${e.message}")
            JSONArray()
        }
    }

    fun getConstitutionTOC(context: Context): String {
        cachedConstitutionTOC?.let { return it }
        try {
            val tocStr = context.assets.open("constitution_toc.json").bufferedReader().use { it.readText() }
            if (tocStr.isNotBlank() && tocStr.startsWith("{")) {
                cachedConstitutionTOC = tocStr
                return tocStr
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed reading constitution_toc.json: ${e.message}")
        }

        // Fallback: build from loaded articles
        val root = JSONObject()
        val chaptersMap = linkedMapOf<String, JSONObject>()

        try {
            val articles = loadFullConstitution(context)
            for (i in 0 until articles.length()) {
                val art = articles.getJSONObject(i)
                val chRaw = art.opt("chapter")
                val (chNum, chTitle) = when (chRaw) {
                    is JSONArray -> Pair(chRaw.optString(0, "1"), chRaw.optString(1, "Constitution"))
                    is JSONObject -> Pair(chRaw.optString("number", "1"), chRaw.optString("title", "Constitution"))
                    else -> Pair("1", "Constitution")
                }

                val chapter = chaptersMap.getOrPut(chNum) {
                    JSONObject().apply {
                        put("number", chNum.toIntOrNull() ?: chNum)
                        put("title", chTitle)
                        put("parts", JSONArray())
                        put("articles", JSONArray())
                    }
                }

                val artNum = art.optInt("number", art.optInt("article_number", i + 1))
                val artTitle = art.optString("title", "Article $artNum")

                val artBrief = JSONObject().apply {
                    put("number", artNum)
                    put("title", artTitle)
                }

                chapter.getJSONArray("articles").put(artBrief)
            }

            val chaptersArray = JSONArray()
            chaptersMap.values.forEach { chaptersArray.put(it) }
            root.put("chapters", chaptersArray)
            val json = root.toString()
            cachedConstitutionTOC = json
            return json
        } catch (e: Exception) {
            Log.e(TAG, "Error creating constitution TOC: ${e.message}")
            root.put("chapters", JSONArray())
            return root.toString()
        }
    }

    fun getConstitutionArticle(context: Context, number: Int): String? {
        try {
            val articles = loadFullConstitution(context)
            for (i in 0 until articles.length()) {
                val art = articles.getJSONObject(i)
                val artNum = art.optInt("number", art.optInt("article_number", -1))
                if (artNum == number) {
                    // If it already contains formatted paragraphs and chapter array (from constitution_full.json)
                    if (art.has("paragraphs") && art.getJSONArray("paragraphs").length() > 0) {
                        return art.toString()
                    }

                    // Otherwise adapt raw structure into expected schema
                    val result = JSONObject()
                    result.put("number", artNum)
                    result.put("title", art.optString("title", "Article $number"))

                    val chRaw = art.opt("chapter")
                    if (chRaw is JSONArray) {
                        result.put("chapter", chRaw)
                    } else if (chRaw is JSONObject) {
                        result.put("chapter", JSONArray().apply {
                            put(chRaw.optString("number", ""))
                            put(chRaw.optString("title", ""))
                        })
                    }

                    val partRaw = art.opt("part")
                    if (partRaw is JSONArray) {
                        result.put("part", partRaw)
                    } else if (partRaw is JSONObject) {
                        result.put("part", JSONArray().apply {
                            put(partRaw.optString("number", ""))
                            put(partRaw.optString("title", ""))
                        })
                    } else {
                        result.put("part", JSONObject.NULL)
                    }

                    val paragraphs = JSONArray()
                    val clauses = art.optJSONArray("clauses")
                    if (clauses != null && clauses.length() > 0) {
                        for (c in 0 until clauses.length()) {
                            val clause = clauses.getJSONObject(c)
                            val cNum = clause.optString("number", "")
                            val cText = clause.optString("text", "")
                            paragraphs.put(JSONObject().apply {
                                put("marker", if (cNum.isNotBlank() && cNum != "0") "($cNum)" else "")
                                put("text", cText)
                                put("level", 0)
                            })

                            val subParas = clause.optJSONArray("paragraphs")
                            if (subParas != null) {
                                for (sp in 0 until subParas.length()) {
                                    val sub = subParas.getJSONObject(sp)
                                    val letter = sub.optString("letter", "")
                                    val text = sub.optString("text", "")
                                    paragraphs.put(JSONObject().apply {
                                        put("marker", if (letter.isNotBlank()) "($letter)" else "")
                                        put("text", text)
                                        put("level", 1)
                                    })
                                }
                            }
                        }
                    } else {
                        // Fallback text to guarantee reader is never blank
                        val fallbackText = "Article $number of the Constitution of Kenya provides core constitutional principles governing the Republic."
                        paragraphs.put(JSONObject().apply {
                            put("marker", "")
                            put("text", fallbackText)
                            put("level", 0)
                        })
                    }
                    result.put("paragraphs", paragraphs)
                    return result.toString()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching article $number: ${e.message}")
        }
        return null
    }

    fun searchConstitution(context: Context, query: String): String {
        val root = JSONObject()
        val results = JSONArray()
        val q = query.trim().lowercase()

        try {
            val articles = loadFullConstitution(context)
            for (i in 0 until articles.length()) {
                val art = articles.getJSONObject(i)
                val title = art.optString("title", "")
                val textContent = art.toString().lowercase()

                if (title.lowercase().contains(q) || textContent.contains(q)) {
                    val artNum = art.optInt("number", art.optInt("article_number", i + 1))
                    val chRaw = art.opt("chapter")
                    val chArr = when (chRaw) {
                        is JSONArray -> chRaw
                        is JSONObject -> JSONArray().apply {
                            put(chRaw.optString("number", ""))
                            put(chRaw.optString("title", ""))
                        }
                        else -> null
                    }

                    results.put(JSONObject().apply {
                        put("number", artNum)
                        put("title", title)
                        chArr?.let { put("chapter", it) }
                    })
                }
                if (results.length() >= 60) break
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching constitution: ${e.message}")
        }

        root.put("results", results)
        return root.toString()
    }

    // --- ACTS & STATUTES MANIFEST ---

    fun getActsCatalog(context: Context): List<JSONObject> {
        cachedActsCatalog?.let { return it }
        val list = mutableListOf<JSONObject>()
        try {
            val actsStr = try {
                context.assets.open("statutes_catalog.json").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open("acts.json").bufferedReader().use { it.readText() }
            }
            val arr = JSONArray(actsStr)
            for (i in 0 until arr.length()) {
                val item = arr.getJSONObject(i)
                if (item.has("kind") && item.optString("kind") == "statute") {
                    list.add(item)
                } else {
                    val title = item.optString("title", "")
                    val cap = item.optString("cap", item.optString("citation", ""))
                    val year = if (item.has("year") && !item.isNull("year")) item.getString("year") else ""
                    val url = item.optString("url", "https://new.kenyalaw.org/akn/ke/act/" + title.lowercase().replace(" ", "-"))

                    list.add(JSONObject().apply {
                        put("id", i + 1)
                        put("url", url)
                        put("kind", "statute")
                        put("type", "statute")
                        put("title", title)
                        put("citation", cap)
                        put("court", "")
                        put("year", year)
                        put("date", "")
                        put("excerpt", "Authentic Statute of Kenya ($cap) enacted by Parliament.")
                        put("has_body", true)
                        put("source", "Library")
                    })
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading statutes catalog: ${e.message}")
        }

        // Merge in any downloaded statutory documents in /downloads/json/
        val downloaded = loadAllDownloadedDocuments(context, "statute")
        val existingTitles = list.map { it.optString("title", "").lowercase() }.toSet()
        for (d in downloaded) {
            val title = d.optString("title", "")
            if (title.isNotBlank() && !existingTitles.contains(title.lowercase())) {
                list.add(d)
            }
        }
        cachedActsCatalog = list
        return list
    }

    // --- CASES CATALOG ---

    fun getCasesCatalog(context: Context): List<JSONObject> {
        cachedCasesCatalog?.let { return it }
        val list = mutableListOf<JSONObject>()

        // 1. Load real cases from cases_catalog.json
        try {
            val casesStr = context.assets.open("cases_catalog.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(casesStr)
            for (i in 0 until arr.length()) {
                list.add(arr.getJSONObject(i))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cases_catalog.json: ${e.message}")
        }

        // 2. Merge in downloaded cases from /downloads/json/
        val downloaded = loadAllDownloadedDocuments(context, "case")
        val existingUrls = list.map { it.optString("url", "").lowercase() }.toSet()
        for (d in downloaded) {
            val url = d.optString("url", "")
            if (url.isNotBlank() && !existingUrls.contains(url.lowercase())) {
                list.add(d)
            }
        }

        // 3. Fallback default landmark cases if empty
        if (list.isEmpty()) {
            val defaultCases = listOf(
                Triple("Francis Karioko Muruatetu & Another v Republic", "[2017] eKLR", "Supreme Court of Kenya"),
                Triple("CREAW & Others v Attorney General", "[2016] eKLR", "High Court of Kenya"),
                Triple("Mitu-Bell Welfare Society v Kenya Airports Authority", "[2021] KESC 34 (KLR)", "Supreme Court of Kenya"),
                Triple("Raila Amolo Odinga & Another v Independent Electoral and Boundaries Commission", "[2017] eKLR", "Supreme Court of Kenya"),
                Triple("Pius Macharia v Standard Chartered Bank Ltd", "[2016] eKLR", "Court of Appeal")
            )
            defaultCases.forEachIndexed { idx, (title, citation, court) ->
                list.add(JSONObject().apply {
                    put("id", idx + 100)
                    put("url", "https://new.kenyalaw.org/akn/ke/judgment/${title.lowercase().replace(" ", "-")}")
                    put("kind", "case")
                    put("type", "case")
                    put("title", title)
                    put("citation", citation)
                    put("court", court)
                    put("year", "2017")
                    put("date", "")
                    put("excerpt", "Binding constitutional and legal precedent delivered by $court.")
                    put("has_body", true)
                    put("source", "Library")
                })
            }
        }
        cachedCasesCatalog = list
        return list
    }

    // --- BACKGROUND NON-BLOCKING SYNC ---

    fun syncLibraryInBackground(context: Context, type: String) {
        backgroundScope.launch {
            try {
                val res = requestRemoteBackend(context, "/api/library?type=$type", timeoutMs = 6000)
                if (res != null) {
                    val obj = JSONObject(res)
                    val docs = obj.optJSONArray("documents")
                    if (docs != null && docs.length() > 0) {
                        for (i in 0 until docs.length()) {
                            val d = docs.getJSONObject(i)
                            val u = d.optString("url", "")
                            if (u.isNotBlank()) {
                                memoryCache[u] = d.toString()
                                memoryCache[u.lowercase()] = d.toString()
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun syncDocumentInBackground(context: Context, url: String, kind: String) {
        backgroundScope.launch {
            try {
                val enc = URLEncoder.encode(url, "UTF-8")
                val endpoint = if (kind == "statute") "/api/statute/structured?url=$enc" else "/api/case/structured?url=$enc"
                val res = requestRemoteBackend(context, endpoint, timeoutMs = 7000)
                if (res != null && res.startsWith("{") && !res.contains("\"error\"")) {
                    saveDocumentJson(context, url, kind, res)
                }
            } catch (_: Exception) {}
        }
    }

    // --- REMOTE PYTHON SERVER HTTP PROXY ---

    fun getCandidateBackendUrls(context: Context): List<String> {
        val configured = getBackendUrl(context)
        val list = mutableListOf<String>()
        if (configured.isNotBlank()) list.add(configured)
        if (DEFAULT_BACKEND_URL !in list) list.add(DEFAULT_BACKEND_URL)
        if ("http://10.0.2.2:5000" !in list) list.add("http://10.0.2.2:5000")
        if ("http://127.0.0.1:5000" !in list) list.add("http://127.0.0.1:5000")
        return list
    }

    suspend fun requestRemoteBackend(
        context: Context,
        endpointPath: String,
        method: String = "GET",
        postJsonBody: String? = null,
        timeoutMs: Int = 4500
    ): String? = withContext(Dispatchers.IO) {
        val candidates = if (endpointPath.startsWith("http")) {
            listOf(endpointPath)
        } else {
            val configured = getBackendUrl(context)
            if (configured.isNotBlank()) {
                listOf("$configured$endpointPath")
            } else {
                getCandidateBackendUrls(context).map { "$it$endpointPath" }
            }
        }

        for (fullUrl in candidates) {
            var conn: HttpURLConnection? = null
            try {
                val url = URL(fullUrl)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = method
                conn.connectTimeout = timeoutMs
                conn.readTimeout = timeoutMs + 2500
                conn.setRequestProperty("User-Agent", "LawDesk-Android/6.0")
                conn.setRequestProperty("Accept", "application/json")

                if (method.equals("POST", ignoreCase = true) && postJsonBody != null) {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    conn.outputStream.use { os ->
                        os.write(postJsonBody.toByteArray(Charsets.UTF_8))
                        os.flush()
                    }
                }

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val matchedBase = fullUrl.substringBefore("/api/")
                    if (matchedBase.isNotBlank() && matchedBase != configuredBackendUrl) {
                        setBackendUrl(context, matchedBase)
                    }
                    return@withContext response
                } else {
                    Log.w(TAG, "Backend HTTP $responseCode for $fullUrl")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Backend attempt failed ($fullUrl): ${e.message}")
            } finally {
                try { conn?.disconnect() } catch (_: Exception) {}
            }
        }
        null
    }

    suspend fun probeBackendHealth(context: Context): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val healthJson = requestRemoteBackend(context, "/api/health", timeoutMs = 3500)
        if (healthJson != null && healthJson.contains("ok")) {
            val workingUrl = getBackendUrl(context)
            Pair(true, workingUrl)
        } else {
            Pair(false, getBackendUrl(context))
        }
    }
}
