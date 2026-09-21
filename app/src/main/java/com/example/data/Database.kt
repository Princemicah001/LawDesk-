package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "statutes")
data class Statute(
    @PrimaryKey val id: String, // e.g. "const_27", "penal_36"
    val category: String, // e.g. "CONSTITUTION", "CRIMINAL", "CIVIL", "LAND", "COMMERCIAL", "EMPLOYMENT", "FAMILY", "ADMINISTRATIVE"
    val actName: String, // e.g. "Constitution of Kenya 2010"
    val sectionNumber: String, // e.g. "Article 27", "Section 36"
    val title: String, // e.g. "Equality and freedom from discrimination"
    val originalText: String,
    val aiInterpretation: String,
    val practicalExample: String,
    val whyItMatters: String,
    val examTip: String,
    val relevantCases: String, // JSON or comma-separated case names
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "act_documents")
data class ActDocument(
    @PrimaryKey val id: String, // e.g. "act_penal_code", "act_employment_act_2007"
    val actName: String, // e.g. "Penal Code (Cap. 63)"
    val year: Int? = null,
    val category: String = "STATUTE", // e.g. "CRIMINAL", "CIVIL", "COMMERCIAL", "LAND", "EMPLOYMENT"
    val description: String = "",
    val totalProvisions: Int = 0,
    val pdfUrl: String? = null,
    val localPdfPath: String? = null,
    val isDownloaded: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "court_cases")
data class CourtCase(
    @PrimaryKey val id: String,
    val title: String, // e.g. "Republic v Karisa Chengo & 2 others"
    val citation: String, // e.g. "[2017] eKLR"
    val articleNumber: String, // e.g. "Criminal Precedent"
    val facts: String,
    val issues: String,
    val holding: String,
    val whyItMatters: String,
    val examTip: String,
    val court: String = "High Court / Supreme Court",
    val date: String = "",
    val pdfUrl: String? = null,
    val isRead: Boolean = false
)

@Entity(tableName = "legal_news")
data class LegalNews(
    @PrimaryKey val id: String,
    val title: String,
    val sourceName: String, // e.g. "Judiciary", "LSK", "Kenya Gazette"
    val sourceUrl: String,
    val aiSummary: String,
    val whyLawyersCare: String,
    val datePublished: String
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val userId: String = "local_user",
    val name: String = "Guest",
    val streak: Int = 0,
    val xp: Int = 0,
    val lastActiveDate: String = "", // "YYYY-MM-DD"
    val rank: String = "Beginner" // "Beginner", "Paralegal", "Advocate", "Senior Counsel", "Constitutional Scholar"
)

@Entity(tableName = "ai_messages")
data class AIMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user", "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String = "default"
)

@Dao
interface LawDao {
    @Query("SELECT * FROM statutes ORDER BY id ASC")
    fun getAllStatutes(): Flow<List<Statute>>

    @Query("SELECT * FROM statutes WHERE category = :category ORDER BY id ASC")
    fun getStatutesByCategory(category: String): Flow<List<Statute>>

    @Query("""
        SELECT * FROM statutes 
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR actName LIKE '%' || :query || '%' OR sectionNumber LIKE '%' || :query || '%' OR originalText LIKE '%' || :query || '%' OR aiInterpretation LIKE '%' || :query || '%' OR whyItMatters LIKE '%' || :query || '%' OR examTip LIKE '%' || :query || '%')
        ORDER BY id ASC
    """)
    fun searchStatutes(query: String): Flow<List<Statute>>

    @Query("""
        SELECT * FROM statutes 
        WHERE (:category = 'ALL' OR category = :category)
        AND (:query = '' OR title LIKE '%' || :query || '%' OR actName LIKE '%' || :query || '%' OR sectionNumber LIKE '%' || :query || '%' OR originalText LIKE '%' || :query || '%' OR aiInterpretation LIKE '%' || :query || '%' OR whyItMatters LIKE '%' || :query || '%' OR examTip LIKE '%' || :query || '%')
        ORDER BY id ASC
    """)
    fun searchStatutesFiltered(query: String, category: String): Flow<List<Statute>>

    @Query("SELECT DISTINCT actName FROM statutes ORDER BY actName ASC")
    fun getAllActNames(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM statutes")
    suspend fun getStatutesCount(): Int

    @Query("SELECT * FROM statutes WHERE id = :id")
    suspend fun getStatuteById(id: String): Statute?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatutes(statutes: List<Statute>)

    @Update
    suspend fun updateStatute(statute: Statute)

    @Query("SELECT * FROM court_cases ORDER BY id ASC")
    fun getAllCases(): Flow<List<CourtCase>>

    @Query("""
        SELECT * FROM court_cases 
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR citation LIKE '%' || :query || '%' OR holding LIKE '%' || :query || '%' OR facts LIKE '%' || :query || '%' OR issues LIKE '%' || :query || '%' OR whyItMatters LIKE '%' || :query || '%' OR examTip LIKE '%' || :query || '%' OR articleNumber LIKE '%' || :query || '%' OR court LIKE '%' || :query || '%')
        ORDER BY id ASC
    """)
    fun searchCases(query: String): Flow<List<CourtCase>>

    @Query("SELECT * FROM court_cases WHERE articleNumber = :article")
    suspend fun getCasesForArticle(article: String): List<CourtCase>

    @Query("SELECT * FROM court_cases WHERE id = :id")
    suspend fun getCaseById(id: String): CourtCase?

    @Query("SELECT COUNT(*) FROM court_cases")
    suspend fun getCasesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCases(cases: List<CourtCase>)

    @Update
    suspend fun updateCase(courtCase: CourtCase)

    @Query("DELETE FROM court_cases")
    suspend fun clearCases()

    // --- Act Documents (Full Acts & Offline PDFs) ---
    @Query("SELECT * FROM act_documents ORDER BY actName ASC")
    fun getAllActDocuments(): Flow<List<ActDocument>>

    @Query("SELECT * FROM act_documents WHERE (:query = '' OR actName LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY actName ASC")
    fun searchActDocuments(query: String): Flow<List<ActDocument>>

    @Query("SELECT * FROM act_documents WHERE id = :id")
    suspend fun getActDocumentById(id: String): ActDocument?

    @Query("SELECT * FROM act_documents WHERE actName = :actName LIMIT 1")
    suspend fun getActDocumentByName(actName: String): ActDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActDocuments(acts: List<ActDocument>)

    @Update
    suspend fun updateActDocument(act: ActDocument)

    @Query("SELECT COUNT(*) FROM act_documents")
    suspend fun getActDocumentsCount(): Int

    @Query("DELETE FROM act_documents")
    suspend fun clearActDocuments()

    @Query("SELECT * FROM legal_news ORDER BY datePublished DESC")
    fun getAllNews(): Flow<List<LegalNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<LegalNews>)

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getUserStats(userId: String = "local_user"): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(userStats: UserStats)

    @Query("SELECT * FROM ai_messages ORDER BY timestamp ASC")
    fun getAIMessages(): Flow<List<AIMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIMessage(message: AIMessage)

    @Query("DELETE FROM ai_messages")
    suspend fun clearAIChat()

    @Query("DELETE FROM ai_messages WHERE sessionId = :sessionId")
    suspend fun clearAIChatForSession(sessionId: String)

    @Query("DELETE FROM statutes")
    suspend fun clearStatutes()
}

@Database(
    entities = [Statute::class, ActDocument::class, CourtCase::class, LegalNews::class, UserStats::class, AIMessage::class],
    version = 3,
    exportSchema = false
)
abstract class LawDatabase : RoomDatabase() {
    abstract fun lawDao(): LawDao
}
