package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LawDesk", appName)
  }

  @Test
  fun `verify constitution assets load correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.LawRepository(context)
    val statutes = repo.loadConstitutionFromAssets()
    org.junit.Assert.assertTrue("Constitution should have articles", statutes.isNotEmpty())
    org.junit.Assert.assertTrue("Constitution should have over 200 articles", statutes.size >= 260)
  }

  @Test
  fun `verify cases and maxims load correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.LawRepository(context)
    val cases = repo.loadCasesFromAssets()
    org.junit.Assert.assertTrue("Cases should be loaded from assets", cases.isNotEmpty())

    val maxims = repo.loadMaximsFromAssets()
    org.junit.Assert.assertTrue("Maxims should be loaded from assets", maxims.isNotEmpty())
  }

  @Test
  fun `verify executeApiRequestSync handles health, constitution, and library`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // Health
    val healthJson = com.example.ui.executeApiRequestSync(context, "/api/health")
    org.junit.Assert.assertTrue("Health response should have ok: true", healthJson.contains("\"ok\":true"))

    // Constitution TOC
    val tocJson = com.example.ui.executeApiRequestSync(context, "/api/constitution/toc")
    org.junit.Assert.assertTrue("TOC should contain chapters", tocJson.contains("chapters"))

    // Constitution Article
    val artJson = com.example.ui.executeApiRequestSync(context, "/api/constitution/article/1")
    org.junit.Assert.assertTrue("Article 1 should have sovereignty", artJson.contains("Sovereignty") || artJson.contains("people"))

    // Library Cases
    val casesJson = com.example.ui.executeApiRequestSync(context, "/api/library?type=case")
    org.junit.Assert.assertTrue("Cases library should contain documents", casesJson.contains("documents"))

    // Library Statutes
    val statutesJson = com.example.ui.executeApiRequestSync(context, "/api/library?type=statute")
    org.junit.Assert.assertTrue("Statutes library should contain documents", statutesJson.contains("documents"))

    // Search
    val searchJson = com.example.ui.executeApiRequestSync(context, "/api/search?q=land")
    org.junit.Assert.assertTrue("Search should return results", searchJson.contains("results"))

    // Structured Case
    val structCaseJson = com.example.ui.executeApiRequestSync(context, "/api/case/structured?url=https%3A%2F%2Fnew.kenyalaw.org%2Fakn%2Fke%2Fjudgment%2Fsample")
    org.junit.Assert.assertTrue("Structured case should return kind:case", structCaseJson.contains("\"kind\":\"case\"") || structCaseJson.contains("\"kind\": \"case\""))

    // Offline Bundled Penal Code Statute
    val penalStatuteJson = com.example.ui.executeApiRequestSync(context, "/api/statute/structured?url=https%3A%2F%2Fnew.kenyalaw.org%2Fakn%2Fke%2Fact%2Fpenal-code")
    org.junit.Assert.assertTrue("Penal code should return kind:statute", penalStatuteJson.contains("\"kind\":\"statute\"") || penalStatuteJson.contains("\"kind\": \"statute\""))
    org.junit.Assert.assertTrue("Penal code should contain title", penalStatuteJson.contains("Penal Code"))

    // Test Save and Load Cache Engine
    val testDocUrl = "https://kenyalaw.org/test/statute/1"
    val testContent = """{"kind":"statute","title":"Test Act","citation":"Cap. 999","items":[]}"""
    com.example.data.LocalLegalDataManager.saveDocumentJson(context, testDocUrl, "statute", testContent)
    val loadedCache = com.example.data.LocalLegalDataManager.loadDocumentJsonByUrl(context, testDocUrl)
    org.junit.Assert.assertNotNull("Cache engine should return saved document", loadedCache)
    org.junit.Assert.assertTrue("Loaded document should match content", loadedCache!!.contains("Test Act"))
  }
}

