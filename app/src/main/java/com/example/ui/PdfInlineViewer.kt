package com.example.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActDocument
import com.example.data.Statute
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfInlineViewer(
    act: ActDocument,
    provisions: List<Statute>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    var renderedPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var currentPageIndex by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    // Track visible page index from scroll state
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    LaunchedEffect(firstVisibleIndex) {
        currentPageIndex = firstVisibleIndex
    }

    // Clean up bitmaps when leaving screen or changing document
    DisposableEffect(act.id) {
        onDispose {
            renderedPages.forEach { bitmap ->
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
            renderedPages = emptyList()
        }
    }

    // Load and render PDF pages safely in background
    LaunchedEffect(act, provisions) {
        isLoading = true
        errorMessage = null
        withContext(Dispatchers.IO) {
            var pfd: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null
            try {
                // 1. Get or create the local PDF file
                val pdfFile: File = if (act.localPdfPath != null && File(act.localPdfPath).exists() && File(act.localPdfPath).length() > 500) {
                    File(act.localPdfPath)
                } else {
                    PdfDocumentHelper.getOrCreateActPdfFile(context, act, provisions)
                }

                if (!pdfFile.exists() || pdfFile.length() < 100) {
                    throw IllegalStateException("PDF document file could not be prepared")
                }

                // 2. Open with PdfRenderer
                pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                val totalPages = renderer.pageCount
                val bitmaps = mutableListOf<Bitmap>()

                // Crisp rendering scale
                val renderScale = 1.75f

                for (i in 0 until totalPages) {
                    val page = renderer.openPage(i)
                    val width = (page.width * renderScale).toInt().coerceAtLeast(1)
                    val height = (page.height * renderScale).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bitmaps.add(bitmap)
                }

                withContext(Dispatchers.Main) {
                    // Recycle old bitmaps before swapping
                    renderedPages.forEach { old ->
                        if (!old.isRecycled) old.recycle()
                    }
                    pageCount = totalPages
                    renderedPages = bitmaps
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e("PdfInlineViewer", "Error rendering PDF: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    errorMessage = e.message ?: "Failed to render PDF document"
                    isLoading = false
                }
            } finally {
                try {
                    renderer?.close()
                } catch (ignored: Exception) {}
                try {
                    pfd?.close()
                } catch (ignored: Exception) {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) PureBlack else Color(0xFFE5E5EA))
            .testTag("inline_pdf_viewer")
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = GoldPremium,
                    modifier = Modifier.size(44.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Rendering Official Act PDF...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) OffWhite else PureBlack,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "High-definition vector rasterization in progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDark) Gray300 else Gray600
                )
            }
        } else if (errorMessage != null || renderedPages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = IosIcons.Warning,
                    contentDescription = null,
                    tint = GoldPremium,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Document Formatting in Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDark) OffWhite else PureBlack,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "Preparing statutory sections for display...",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Gray300 else Gray600
                )
            }
        } else {
            // Rendered PDF Document List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.85f, 3.0f)
                        }
                    },
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(renderedPages) { index, bitmap ->
                    if (!bitmap.isRecycled) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Page Sheet Container with realistic Paper Drop Shadow
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(if (zoomScale > 1f) 1f else 0.98f)
                                    .shadow(8.dp, RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp)),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFD1D1D6))
                            ) {
                                Column {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Page ${index + 1} of $pageCount",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "— Page ${index + 1} of $pageCount —",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) Gray300 else Gray600,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Floating Navigation & Zoom Controls Toolbar
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.95f) else Color(0xFFFFFFFF).copy(alpha = 0.95f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, if (isDark) GlassBorder else DividerColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Previous Page Button
                    IconButton(
                        onClick = {
                            if (currentPageIndex > 0) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(currentPageIndex - 1)
                                }
                            }
                        },
                        enabled = currentPageIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.ChevronLeft,
                            contentDescription = "Previous Page",
                            tint = if (currentPageIndex > 0) GoldPremium else (if (isDark) Gray600 else Gray300),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Page Indicator Chip
                    Surface(
                        color = GoldPremium.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${currentPageIndex + 1} / $pageCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPremium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }

                    // Next Page Button
                    IconButton(
                        onClick = {
                            if (currentPageIndex < pageCount - 1) {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(currentPageIndex + 1)
                                }
                            }
                        },
                        enabled = currentPageIndex < pageCount - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.ChevronRight,
                            contentDescription = "Next Page",
                            tint = if (currentPageIndex < pageCount - 1) GoldPremium else (if (isDark) Gray600 else Gray300),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(1.dp)
                            .background(if (isDark) Gray600 else Gray300)
                    )

                    // Zoom In / Reset Button
                    IconButton(
                        onClick = {
                            zoomScale = if (zoomScale > 1.2f) 1.0f else 1.5f
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (zoomScale > 1.2f) IosIcons.Checkmark else IosIcons.Search,
                            contentDescription = "Zoom",
                            tint = GoldPremium,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = if (zoomScale > 1.2f) "150%" else "100%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) OffWhite else PureBlack,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
