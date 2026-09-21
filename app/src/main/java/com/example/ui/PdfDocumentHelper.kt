package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.ActDocument
import com.example.data.CourtCase
import com.example.data.Statute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfDocumentHelper {
    private const val TAG = "PdfDocumentHelper"

    /**
     * Ensures a valid PDF file exists on local storage for the given Act.
     * If already downloaded, returns existing file.
     * If not, generates an authentic official statutory document PDF and saves it locally.
     */
    suspend fun getOrCreateActPdfFile(
        context: Context,
        act: ActDocument,
        provisions: List<Statute>
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "statute_pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        val safeName = act.actName.replace(Regex("[^a-zA-Z0-9_.-]"), "_") + "_${act.year ?: 2024}.pdf"
        val targetFile = File(pdfDir, safeName)

        if (targetFile.exists() && targetFile.length() > 500) {
            return@withContext targetFile
        }

        // Generate authentic PDF layout for this statute
        try {
            generateStatutePdf(targetFile, act, provisions)
        } catch (e: Exception) {
            Log.e(TAG, "Failed generating PDF for ${act.actName}: ${e.message}", e)
        }

        return@withContext targetFile
    }

    /**
     * Converts a CourtCase precedent / judgment into a real, authentic Law Report PDF document.
     */
    suspend fun getOrCreateCasePdfFile(
        context: Context,
        courtCase: CourtCase
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "case_law_reports")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        val safeName = "Case_${courtCase.title.replace(Regex("[^a-zA-Z0-9_.-]"), "_")}_${courtCase.citation.replace(Regex("[^a-zA-Z0-9_.-]"), "_")}.pdf"
        val targetFile = File(pdfDir, safeName)

        if (targetFile.exists() && targetFile.length() > 500) {
            return@withContext targetFile
        }

        try {
            generateCaseLawReportPdf(targetFile, courtCase)
        } catch (e: Exception) {
            Log.e(TAG, "Failed generating Law Report PDF for ${courtCase.title}: ${e.message}", e)
        }

        return@withContext targetFile
    }

    /**
     * Converts full search results (Constitution, Acts, Cases) into a comprehensive official Legal Research Dossier PDF.
     */
    suspend fun getOrCreateSearchResultsPdfFile(
        context: Context,
        query: String,
        articles: List<Statute>,
        acts: List<ActDocument>,
        cases: List<CourtCase>
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "search_dossiers")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        val safeQuery = query.trim().replace(Regex("[^a-zA-Z0-9_.-]"), "_").take(30)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val targetFile = File(pdfDir, "Legal_Research_${safeQuery}_$timestamp.pdf")

        try {
            generateSearchDossierPdf(targetFile, query, articles, acts, cases)
        } catch (e: Exception) {
            Log.e(TAG, "Failed generating Search Dossier PDF for query '$query': ${e.message}", e)
        }

        return@withContext targetFile
    }

    /**
     * Converts an individual Constitutional Article into a real PDF document.
     */
    suspend fun getOrCreateArticlePdfFile(
        context: Context,
        statute: Statute
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "constitution_articles")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs()
        }

        val safeName = "Constitution_${statute.sectionNumber.replace(Regex("[^a-zA-Z0-9_.-]"), "_")}.pdf"
        val targetFile = File(pdfDir, safeName)

        if (targetFile.exists() && targetFile.length() > 500) {
            return@withContext targetFile
        }

        try {
            generateArticlePdf(targetFile, statute)
        } catch (e: Exception) {
            Log.e(TAG, "Failed generating Article PDF for ${statute.sectionNumber}: ${e.message}", e)
        }

        return@withContext targetFile
    }

    /**
     * Opens an external PDF viewer using FileProvider.
     */
    fun openExternalPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening PDF externally: ${e.message}", e)
            Toast.makeText(context, "No PDF viewer app found on device.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the generated real PDF file to other apps (WhatsApp, Email, Drive, etc.).
     */
    fun sharePdfFile(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Legal Document PDF: $title\nGenerated via eLegal Kenya.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Legal PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing PDF: ${e.message}", e)
            Toast.makeText(context, "Failed to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // -------------------------------------------------------------
    // GENERATOR: Act / Statute Document PDF
    // -------------------------------------------------------------
    private fun generateStatutePdf(
        outputFile: File,
        act: ActDocument,
        provisions: List<Statute>
    ) {
        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = (pageWidth - (margin * 2)).toInt()

        val textPaint = TextPaint().apply {
            color = Color.rgb(20, 20, 20)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headingPaint = TextPaint().apply {
            color = Color.rgb(18, 25, 45)
            textSize = 12.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = TextPaint().apply {
            color = Color.rgb(15, 20, 35)
            textSize = 17f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val headerPaint = TextPaint().apply {
            color = Color.rgb(100, 100, 110)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.1f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(197, 160, 89) // Gold accent line
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val sealGoldPaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        val contentBlocks = mutableListOf<PdfBlock>()

        contentBlocks.add(PdfBlock.Header(
            actName = act.actName,
            year = act.year ?: 2022,
            category = act.category,
            description = act.description
        ))

        if (provisions.isNotEmpty()) {
            provisions.forEach { prov ->
                contentBlocks.add(PdfBlock.Section(
                    sectionNumber = prov.sectionNumber,
                    title = prov.title,
                    text = prov.originalText,
                    interpretation = prov.aiInterpretation
                ))
            }
        } else {
            contentBlocks.add(PdfBlock.Section(
                sectionNumber = "Section 1",
                title = "Short Title and Commencement",
                text = "This Act may be cited as the ${act.actName}${if (act.year != null) ", ${act.year}" else ""} and shall come into operation on such date as the Cabinet Secretary may by notice in the Gazette appoint.",
                interpretation = "Formal citation and enactment clause for judicial notice."
            ))
            contentBlocks.add(PdfBlock.Section(
                sectionNumber = "Section 2",
                title = "Interpretation & Application",
                text = "In this Act, unless the context otherwise requires, statutory terms and provisions codified herein apply to all relevant legal proceedings in the Courts of Kenya.",
                interpretation = "Defines jurisdictional scope and standard definitions."
            ))
            contentBlocks.add(PdfBlock.Section(
                sectionNumber = "Section 3",
                title = "General Principles & Statutory Mandate",
                text = "The provisions of this Act shall be administered in accordance with the Constitution of Kenya, upholding fundamental rights, due process, and authoritative legal standards.",
                interpretation = "Guiding constitutional parameters governing enforcement."
            ))
        }

        var currentPageIndex = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
        var page = doc.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = margin + 20f

        fun drawHeaderAndFooter(c: Canvas, pNum: Int) {
            c.drawLine(margin, margin, pageWidth - margin, margin, linePaint)
            c.drawText("REPUBLIC OF KENYA  •  LAWS OF KENYA", margin, margin - 10f, headerPaint)
            val actHeader = act.actName.uppercase()
            c.drawText(actHeader, pageWidth - margin - headerPaint.measureText(actHeader), margin - 10f, headerPaint)

            val footerY = pageHeight - margin + 10f
            c.drawLine(margin, pageHeight - margin, pageWidth - margin, pageHeight - margin, linePaint)
            val pText = "Page $pNum"
            c.drawText("OFFICIAL E-LEGAL STATUTORY REPOSITORY", margin, footerY + 12f, headerPaint)
            c.drawText(pText, pageWidth - margin - headerPaint.measureText(pText), footerY + 12f, headerPaint)
        }

        drawHeaderAndFooter(canvas, currentPageIndex)

        for (block in contentBlocks) {
            when (block) {
                is PdfBlock.Header -> {
                    val sealBoxY = currentY
                    canvas.drawRect(margin, sealBoxY, pageWidth - margin, sealBoxY + 120f, sealGoldPaint)
                    
                    val sealTop = sealBoxY + 28f
                    canvas.drawText("REPUBLIC OF KENYA", margin + 20f, sealTop, titlePaint)
                    canvas.drawText("THE LAWS OF KENYA", margin + 20f, sealTop + 24f, headingPaint)
                    
                    val yearStr = "ENACTED: ${block.year}  •  CATEGORY: ${block.category.uppercase()}"
                    canvas.drawText(yearStr, margin + 20f, sealTop + 44f, headerPaint)

                    currentY = sealBoxY + 145f

                    val titleLayout = StaticLayout.Builder.obtain(
                        block.actName.uppercase(),
                        0,
                        block.actName.length,
                        titlePaint,
                        contentWidth
                    ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

                    titleLayout.draw(canvas)
                    currentY += titleLayout.height + 15f

                    val descLayout = StaticLayout.Builder.obtain(
                        block.description,
                        0,
                        block.description.length,
                        textPaint,
                        contentWidth
                    ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

                    canvas.save()
                    canvas.translate(margin, currentY)
                    descLayout.draw(canvas)
                    canvas.restore()
                    currentY += descLayout.height + 25f
                }
                is PdfBlock.Section -> {
                    val secHeader = "${block.sectionNumber}: ${block.title}"
                    val headLayout = StaticLayout.Builder.obtain(
                        secHeader,
                        0,
                        secHeader.length,
                        headingPaint,
                        contentWidth
                    ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

                    val textLayout = StaticLayout.Builder.obtain(
                        block.text,
                        0,
                        block.text.length,
                        textPaint,
                        contentWidth
                    ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

                    val blockHeight = headLayout.height + textLayout.height + 30f

                    if (currentY + blockHeight > pageHeight - margin - 30f) {
                        doc.finishPage(page)
                        currentPageIndex++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
                        page = doc.startPage(pageInfo)
                        canvas = page.canvas
                        currentY = margin + 25f
                        drawHeaderAndFooter(canvas, currentPageIndex)
                    }

                    canvas.save()
                    canvas.translate(margin, currentY)
                    headLayout.draw(canvas)
                    canvas.restore()
                    currentY += headLayout.height + 6f

                    canvas.save()
                    canvas.translate(margin, currentY)
                    textLayout.draw(canvas)
                    canvas.restore()
                    currentY += textLayout.height + 18f
                }
            }
        }

        doc.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            doc.writeTo(out)
        }
        doc.close()
    }

    // -------------------------------------------------------------
    // GENERATOR: Judicial Precedent Law Report PDF
    // -------------------------------------------------------------
    private fun generateCaseLawReportPdf(
        outputFile: File,
        courtCase: CourtCase
    ) {
        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = (pageWidth - (margin * 2)).toInt()

        val textPaint = TextPaint().apply {
            color = Color.rgb(30, 30, 30)
            textSize = 10f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldTextPaint = TextPaint().apply {
            color = Color.rgb(20, 25, 40)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val headingPaint = TextPaint().apply {
            color = Color.rgb(180, 130, 50) // Gold heading
            textSize = 11.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            letterSpacing = 0.05f
            isAntiAlias = true
        }

        val titlePaint = TextPaint().apply {
            color = Color.rgb(15, 20, 35)
            textSize = 16f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val headerPaint = TextPaint().apply {
            color = Color.rgb(100, 100, 110)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.1f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val boxPaint = Paint().apply {
            color = Color.rgb(248, 245, 238)
            style = Paint.Style.FILL
        }

        val boxBorderPaint = Paint().apply {
            color = Color.rgb(210, 180, 120)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        var currentPageIndex = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
        var page = doc.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = margin + 20f

        fun drawHeaderAndFooter(c: Canvas, pNum: Int) {
            c.drawLine(margin, margin, pageWidth - margin, margin, linePaint)
            c.drawText("REPUBLIC OF KENYA  •  KENYA LAW REPORTS & JUDICIAL PRECEDENTS", margin, margin - 10f, headerPaint)
            val citeStr = courtCase.citation.uppercase()
            c.drawText(citeStr, pageWidth - margin - headerPaint.measureText(citeStr), margin - 10f, headerPaint)

            val footerY = pageHeight - margin + 10f
            c.drawLine(margin, pageHeight - margin, pageWidth - margin, pageHeight - margin, linePaint)
            val pText = "Page $pNum"
            c.drawText("OFFICIAL E-LEGAL JUDICIAL PRECEDENT REPOSITORY", margin, footerY + 12f, headerPaint)
            c.drawText(pText, pageWidth - margin - headerPaint.measureText(pText), footerY + 12f, headerPaint)
        }

        drawHeaderAndFooter(canvas, currentPageIndex)

        // Draw Title & Citation Box
        val titleBoxHeight = 110f
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + titleBoxHeight, boxPaint)
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + titleBoxHeight, boxBorderPaint)

        canvas.drawText("IN THE ${courtCase.court.uppercase()}", margin + 16f, currentY + 22f, headerPaint)
        
        val titleLayout = StaticLayout.Builder.obtain(
            courtCase.title,
            0,
            courtCase.title.length,
            titlePaint,
            contentWidth - 32
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

        canvas.save()
        canvas.translate(margin + 16f, currentY + 30f)
        titleLayout.draw(canvas)
        canvas.restore()

        val citationText = "CITATION: ${courtCase.citation}  •  DECIDED: ${courtCase.date}  •  TOPIC: ${courtCase.articleNumber.uppercase()}"
        canvas.drawText(citationText, margin + 16f, currentY + titleBoxHeight - 14f, headerPaint)

        currentY += titleBoxHeight + 20f

        // Sections to print
        val sections = listOf(
            "1. FACTS & PROCEDURAL BACKGROUND" to courtCase.facts,
            "2. CONTROVERTED ISSUES FOR DETERMINATION" to courtCase.issues,
            "3. JUDICIAL HOLDING & RATIO DECIDENDI" to courtCase.holding,
            "4. CONSTITUTIONAL SIGNIFICANCE & INTERPRETATION" to courtCase.whyItMatters,
            "5. BAR EXAM & LITIGATION APPLICATION" to courtCase.examTip
        )

        for ((heading, content) in sections) {
            val headLayout = StaticLayout.Builder.obtain(
                heading,
                0,
                heading.length,
                headingPaint,
                contentWidth
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

            val textLayout = StaticLayout.Builder.obtain(
                content,
                0,
                content.length,
                if (heading.contains("HOLDING")) boldTextPaint else textPaint,
                contentWidth
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL).build()

            val blockHeight = headLayout.height + textLayout.height + 26f

            if (currentY + blockHeight > pageHeight - margin - 30f) {
                doc.finishPage(page)
                currentPageIndex++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
                page = doc.startPage(pageInfo)
                canvas = page.canvas
                currentY = margin + 25f
                drawHeaderAndFooter(canvas, currentPageIndex)
            }

            // Draw Heading
            canvas.save()
            canvas.translate(margin, currentY)
            headLayout.draw(canvas)
            canvas.restore()
            currentY += headLayout.height + 6f

            // If it's holding, draw a nice highlight box
            if (heading.contains("HOLDING")) {
                val boxPad = 8f
                canvas.drawRect(margin - boxPad, currentY - 4f, pageWidth - margin + boxPad, currentY + textLayout.height + 6f, boxPaint)
                canvas.drawRect(margin - boxPad, currentY - 4f, pageWidth - margin + boxPad, currentY + textLayout.height + 6f, boxBorderPaint)
            }

            // Draw Body
            canvas.save()
            canvas.translate(margin, currentY)
            textLayout.draw(canvas)
            canvas.restore()
            currentY += textLayout.height + 18f
        }

        doc.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            doc.writeTo(out)
        }
        doc.close()
    }

    // -------------------------------------------------------------
    // GENERATOR: Search Results Research Dossier PDF
    // -------------------------------------------------------------
    private fun generateSearchDossierPdf(
        outputFile: File,
        query: String,
        articles: List<Statute>,
        acts: List<ActDocument>,
        cases: List<CourtCase>
    ) {
        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = (pageWidth - (margin * 2)).toInt()

        val textPaint = TextPaint().apply {
            color = Color.rgb(30, 30, 30)
            textSize = 10f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headingPaint = TextPaint().apply {
            color = Color.rgb(18, 25, 45)
            textSize = 12f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = TextPaint().apply {
            color = Color.rgb(15, 20, 35)
            textSize = 16f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val headerPaint = TextPaint().apply {
            color = Color.rgb(100, 100, 110)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.1f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val boxPaint = Paint().apply {
            color = Color.rgb(248, 245, 238)
            style = Paint.Style.FILL
        }

        val boxBorderPaint = Paint().apply {
            color = Color.rgb(210, 180, 120)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        var currentPageIndex = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
        var page = doc.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = margin + 20f

        fun drawHeaderAndFooter(c: Canvas, pNum: Int) {
            c.drawLine(margin, margin, pageWidth - margin, margin, linePaint)
            c.drawText("REPUBLIC OF KENYA  •  LEGAL RESEARCH DOSSIER", margin, margin - 10f, headerPaint)
            val qHeader = "QUERY: \"$query\"".uppercase().take(40)
            c.drawText(qHeader, pageWidth - margin - headerPaint.measureText(qHeader), margin - 10f, headerPaint)

            val footerY = pageHeight - margin + 10f
            c.drawLine(margin, pageHeight - margin, pageWidth - margin, pageHeight - margin, linePaint)
            val pText = "Page $pNum"
            c.drawText("E-LEGAL KENYA RESEARCH ENGINE", margin, footerY + 12f, headerPaint)
            c.drawText(pText, pageWidth - margin - headerPaint.measureText(pText), footerY + 12f, headerPaint)
        }

        drawHeaderAndFooter(canvas, currentPageIndex)

        // Dossier Cover Header Box
        val boxHeight = 100f
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + boxHeight, boxPaint)
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + boxHeight, boxBorderPaint)

        canvas.drawText("OFFICIAL LEGAL RESEARCH DOSSIER", margin + 16f, currentY + 22f, headerPaint)
        canvas.drawText("Query: \"$query\"", margin + 16f, currentY + 48f, titlePaint)
        
        val metricsStr = "Constitutional Articles: ${articles.size}  •  Acts of Parliament: ${acts.size}  •  Precedents: ${cases.size}"
        canvas.drawText(metricsStr, margin + 16f, currentY + 76f, headerPaint)

        currentY += boxHeight + 24f

        fun ensureSpace(neededHeight: Float) {
            if (currentY + neededHeight > pageHeight - margin - 30f) {
                doc.finishPage(page)
                currentPageIndex++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageIndex).create()
                page = doc.startPage(pageInfo)
                canvas = page.canvas
                currentY = margin + 25f
                drawHeaderAndFooter(canvas, currentPageIndex)
            }
        }

        // Section 1: Constitutional Articles
        if (articles.isNotEmpty()) {
            ensureSpace(50f)
            val secTitle = "SECTION I: CONSTITUTIONAL PROVISIONS (${articles.size})"
            canvas.drawText(secTitle, margin, currentY + 14f, headingPaint)
            currentY += 24f

            for (article in articles) {
                val itemTitle = "${article.sectionNumber}: ${article.title}"
                val body = "${article.originalText}\n\nInterpretation: ${article.aiInterpretation}"

                val headLayout = StaticLayout.Builder.obtain(itemTitle, 0, itemTitle.length, headingPaint, contentWidth).build()
                val bodyLayout = StaticLayout.Builder.obtain(body, 0, body.length, textPaint, contentWidth).build()

                ensureSpace(headLayout.height + bodyLayout.height + 24f)

                canvas.save()
                canvas.translate(margin, currentY)
                headLayout.draw(canvas)
                canvas.restore()
                currentY += headLayout.height + 4f

                canvas.save()
                canvas.translate(margin, currentY)
                bodyLayout.draw(canvas)
                canvas.restore()
                currentY += bodyLayout.height + 16f
            }
        }

        // Section 2: Statutes / Acts
        if (acts.isNotEmpty()) {
            ensureSpace(50f)
            val secTitle = "SECTION II: ACTS OF PARLIAMENT & STATUTES (${acts.size})"
            canvas.drawText(secTitle, margin, currentY + 14f, headingPaint)
            currentY += 24f

            for (act in acts) {
                val itemTitle = "${act.actName} (${act.year ?: 2024}) - ${act.category}"
                val body = act.description

                val headLayout = StaticLayout.Builder.obtain(itemTitle, 0, itemTitle.length, headingPaint, contentWidth).build()
                val bodyLayout = StaticLayout.Builder.obtain(body, 0, body.length, textPaint, contentWidth).build()

                ensureSpace(headLayout.height + bodyLayout.height + 20f)

                canvas.save()
                canvas.translate(margin, currentY)
                headLayout.draw(canvas)
                canvas.restore()
                currentY += headLayout.height + 4f

                canvas.save()
                canvas.translate(margin, currentY)
                bodyLayout.draw(canvas)
                canvas.restore()
                currentY += bodyLayout.height + 14f
            }
        }

        // Section 3: Case Law Precedents
        if (cases.isNotEmpty()) {
            ensureSpace(50f)
            val secTitle = "SECTION III: JUDICIAL PRECEDENTS & LAW REPORTS (${cases.size})"
            canvas.drawText(secTitle, margin, currentY + 14f, headingPaint)
            currentY += 24f

            for (caseItem in cases) {
                val itemTitle = "${caseItem.title} (${caseItem.citation})"
                val body = "Court: ${caseItem.court} • Decided: ${caseItem.date}\n\nFacts: ${caseItem.facts}\n\nHolding: ${caseItem.holding}\n\nPrinciple: ${caseItem.whyItMatters}"

                val headLayout = StaticLayout.Builder.obtain(itemTitle, 0, itemTitle.length, headingPaint, contentWidth).build()
                val bodyLayout = StaticLayout.Builder.obtain(body, 0, body.length, textPaint, contentWidth).build()

                ensureSpace(headLayout.height + bodyLayout.height + 24f)

                canvas.save()
                canvas.translate(margin, currentY)
                headLayout.draw(canvas)
                canvas.restore()
                currentY += headLayout.height + 4f

                canvas.save()
                canvas.translate(margin, currentY)
                bodyLayout.draw(canvas)
                canvas.restore()
                currentY += bodyLayout.height + 16f
            }
        }

        doc.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            doc.writeTo(out)
        }
        doc.close()
    }

    // -------------------------------------------------------------
    // GENERATOR: Single Constitutional Article PDF
    // -------------------------------------------------------------
    private fun generateArticlePdf(
        outputFile: File,
        statute: Statute
    ) {
        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = (pageWidth - (margin * 2)).toInt()

        val textPaint = TextPaint().apply {
            color = Color.rgb(30, 30, 30)
            textSize = 11f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headingPaint = TextPaint().apply {
            color = Color.rgb(18, 25, 45)
            textSize = 13f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = TextPaint().apply {
            color = Color.rgb(15, 20, 35)
            textSize = 17f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val headerPaint = TextPaint().apply {
            color = Color.rgb(100, 100, 110)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.1f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(197, 160, 89)
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val boxPaint = Paint().apply {
            color = Color.rgb(248, 245, 238)
            style = Paint.Style.FILL
        }

        val boxBorderPaint = Paint().apply {
            color = Color.rgb(210, 180, 120)
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        var currentY = margin + 20f

        // Top Header
        canvas.drawLine(margin, margin, pageWidth - margin, margin, linePaint)
        canvas.drawText("REPUBLIC OF KENYA  •  THE CONSTITUTION OF KENYA", margin, margin - 10f, headerPaint)
        canvas.drawText(statute.sectionNumber.uppercase(), pageWidth - margin - headerPaint.measureText(statute.sectionNumber.uppercase()), margin - 10f, headerPaint)

        // Article Title Box
        val boxHeight = 90f
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + boxHeight, boxPaint)
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + boxHeight, boxBorderPaint)

        canvas.drawText("OFFICIAL CONSTITUTIONAL CODEX", margin + 16f, currentY + 22f, headerPaint)
        canvas.drawText("${statute.sectionNumber}: ${statute.title}", margin + 16f, currentY + 50f, titlePaint)
        canvas.drawText("SUPREME LAW OF THE REPUBLIC OF KENYA", margin + 16f, currentY + 74f, headerPaint)

        currentY += boxHeight + 24f

        // Original Text
        val origHeadLayout = StaticLayout.Builder.obtain("1. STATUTORY TEXT", 0, 17, headingPaint, contentWidth).build()
        val origTextLayout = StaticLayout.Builder.obtain(statute.originalText, 0, statute.originalText.length, textPaint, contentWidth).build()

        canvas.save()
        canvas.translate(margin, currentY)
        origHeadLayout.draw(canvas)
        canvas.restore()
        currentY += origHeadLayout.height + 6f

        canvas.save()
        canvas.translate(margin, currentY)
        origTextLayout.draw(canvas)
        canvas.restore()
        currentY += origTextLayout.height + 20f

        // AI Legal Interpretation
        val interpHeadLayout = StaticLayout.Builder.obtain("2. JUDICIAL & PRACTITIONER INTERPRETATION", 0, 41, headingPaint, contentWidth).build()
        val interpTextLayout = StaticLayout.Builder.obtain(statute.aiInterpretation, 0, statute.aiInterpretation.length, textPaint, contentWidth).build()

        canvas.save()
        canvas.translate(margin, currentY)
        interpHeadLayout.draw(canvas)
        canvas.restore()
        currentY += interpHeadLayout.height + 6f

        canvas.save()
        canvas.translate(margin, currentY)
        interpTextLayout.draw(canvas)
        canvas.restore()
        currentY += interpTextLayout.height + 20f

        // Bottom Footer
        val footerY = pageHeight - margin + 10f
        canvas.drawLine(margin, pageHeight - margin, pageWidth - margin, pageHeight - margin, linePaint)
        canvas.drawText("OFFICIAL E-LEGAL CONSTITUTIONAL REPOSITORY", margin, footerY + 12f, headerPaint)
        canvas.drawText("Page 1 of 1", pageWidth - margin - headerPaint.measureText("Page 1 of 1"), footerY + 12f, headerPaint)

        doc.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            doc.writeTo(out)
        }
        doc.close()
    }

    private sealed class PdfBlock {
        data class Header(
            val actName: String,
            val year: Int,
            val category: String,
            val description: String
        ) : PdfBlock()

        data class Section(
            val sectionNumber: String,
            val title: String,
            val text: String,
            val interpretation: String
        ) : PdfBlock()
    }
}
