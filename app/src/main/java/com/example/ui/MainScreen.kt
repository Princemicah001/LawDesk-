package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.os.Build
import android.provider.Settings
import android.content.Intent
import android.net.Uri
import java.util.Calendar
import java.io.File
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import com.example.R
import com.example.data.*
import com.example.ui.theme.*
import kotlin.random.Random
import org.json.JSONArray
import org.json.JSONObject

fun loadRandomLegalFact(context: android.content.Context): Pair<String, String> {
    try {
        val jsonString = context.assets.open("legal_facts.json").bufferedReader().use { it.readText() }
        val rootArray = JSONArray(jsonString)
        if (rootArray.length() > 0) {
            val randomIndex = Random.nextInt(rootArray.length())
            val factObj = rootArray.getJSONObject(randomIndex)
            val fact = factObj.optString("fact", "")
            val source = factObj.optString("source", "")
            if (fact.isNotEmpty()) {
                return Pair(fact, source)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Pair(
        "The Constitution of Kenya was promulgated on 21st August 2010, ushering in a new era of governance, devolution, and robust human rights.",
        "Constitution of Kenya"
    )
}

@Composable
fun SplashScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val factPair = remember { loadRandomLegalFact(context) }
    
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "SplashFadeSpec"
    )

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
        delay(3800)
        visible = false
        delay(1000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F1E3)) // Organic warm cream background
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Organic background pastel blobs (Peach & Teal)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFEEB182).copy(alpha = 0.55f),
                radius = 180.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(-20.dp.toPx(), 40.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF8AB4A9).copy(alpha = 0.55f),
                radius = 200.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(size.width + 30.dp.toPx(), 20.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF8AB4A9).copy(alpha = 0.55f),
                radius = 190.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(-30.dp.toPx(), size.height - 40.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFEEB182).copy(alpha = 0.65f),
                radius = 220.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(size.width + 40.dp.toPx(), size.height - 30.dp.toPx())
            )
        }

        // Stylish Organic Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .graphicsLayer(alpha = alpha)
                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color(0x22000000)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, Color(0xFFF3F4F6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card Header Blob Shape
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 60.dp, bottomEnd = 40.dp, bottomStart = 20.dp))
                        .background(Color(0xFFE8EDEA))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // Lightbulb Icon in Top Right
                    Icon(
                        imageVector = IosIcons.Lightbulb,
                        contentDescription = "Lightbulb Icon",
                        tint = Color(0xFF4A4A4A),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(46.dp)
                            .padding(top = 2.dp, end = 2.dp)
                    )

                    // Header Text Column
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Did You",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF4A4A4A),
                            lineHeight = 40.sp
                        )
                        Text(
                            text = "KNOW?",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEEB182),
                            letterSpacing = 2.sp,
                            lineHeight = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fact Content Area
                Text(
                    text = factPair.first,
                    fontSize = 13.5.sp,
                    color = Color(0xFF374151),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (factPair.second.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        color = Color(0xFFF3F4F6),
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "~ ${factPair.second}",
                        fontSize = 10.5.sp,
                        color = Color(0xFF9CA3AF),
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

fun parseMarkdownToAnnotatedString(text: String, isDark: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            var workingLine = line.trim()
            if (workingLine.isEmpty()) {
                append("\n")
                return@forEachIndexed
            }
            
            // Skip markdown table delimiters completely (example: |---| or |:---|:---|)
            if (workingLine.contains("-") && workingLine.contains("|") && 
                workingLine.replace("|", "").replace("-", "").replace(":", "").replace(" ", "").isEmpty()) {
                return@forEachIndexed
            }
            
            // Strip table boundaries (such as leading and trailing |) and convert cells cleanly
            if (workingLine.startsWith("|") || workingLine.endsWith("|")) {
                workingLine = workingLine.removePrefix("|").removeSuffix("|").trim()
                // If it was parsed with pipes inside representing key-value labels, make it beautiful
                if (workingLine.contains("|")) {
                    val parts = workingLine.split("|")
                    if (parts.size == 2) {
                        workingLine = "**${parts[0].trim()}**: ${parts[1].trim()}"
                    } else {
                        workingLine = workingLine.replace("|", " • ").trim()
                    }
                }
            }
            
            // 1. Headers (e.g. ###, ##, #)
            val isHeader = if (workingLine.startsWith("### ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isDark) GoldPremium else GoldDark))
                workingLine = workingLine.substring(4)
                true
            } else if (workingLine.startsWith("## ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isDark) GoldPremium else GoldDark))
                workingLine = workingLine.substring(3)
                true
            } else if (workingLine.startsWith("# ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if (isDark) GoldPremium else GoldDark))
                workingLine = workingLine.substring(2)
                true
            } else {
                false
            }
            
            // 2. Lists (e.g. "- " or "* " or "+ ")
            if (!isHeader && (workingLine.startsWith("- ") || workingLine.startsWith("* ") || workingLine.startsWith("+ "))) {
                append(" •   ")
                workingLine = workingLine.substring(2)
            }
            
            // 3. Inline bold & italic scanning
            var i = 0
            while (i < workingLine.length) {
                if (workingLine.startsWith("**", i)) {
                    val endToken = workingLine.indexOf("**", i + 2)
                    if (endToken != -1) {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(workingLine.substring(i + 2, endToken))
                        pop()
                        i = endToken + 2
                    } else {
                        append("**")
                        i += 2
                    }
                } else if (workingLine.startsWith("*", i)) {
                    val endToken = workingLine.indexOf("*", i + 1)
                    if (endToken != -1) {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(workingLine.substring(i + 1, endToken))
                        pop()
                        i = endToken + 1
                    } else {
                        append("*")
                        i += 1
                    }
                } else if (workingLine.startsWith("_", i)) {
                    val endToken = workingLine.indexOf("_", i + 1)
                    if (endToken != -1) {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(workingLine.substring(i + 1, endToken))
                        pop()
                        i = endToken + 1
                    } else {
                        append("_")
                        i += 1
                    }
                } else {
                    append(workingLine[i].toString())
                    i++
                }
            }
            
            if (isHeader) {
                pop()
            }
            
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showBriefing by viewModel.showBriefing.collectAsStateWithLifecycle()
    val selectedStatute by viewModel.selectedStatute.collectAsStateWithLifecycle()
    val selectedCase by viewModel.selectedCase.collectAsStateWithLifecycle()
    val selectedActDocument by viewModel.selectedActDocument.collectAsStateWithLifecycle()
    val viewingPdfDocument by viewModel.viewingPdfDocument.collectAsStateWithLifecycle()
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val isAiQuizLoading by viewModel.isAiQuizLoading.collectAsStateWithLifecycle()
    
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val showBanner by viewModel.showNotificationBanner.collectAsStateWithLifecycle()
    val backgroundColor = if (isDark) PureBlack else OffWhite

    var showSplash by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root_surface"),
        color = backgroundColor
    ) {
        if (showSplash) {
            SplashScreen(onDismiss = { showSplash = false })
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (viewingPdfDocument != null) {
                    DocumentPdfViewer(
                        filePathOrUrl = viewingPdfDocument!!.first,
                        documentTitle = viewingPdfDocument!!.second,
                        onDismiss = { viewModel.closePdfViewer() }
                    )
                } else if (activeQuiz != null || isAiQuizLoading) {
                    QuizView(viewModel = viewModel, quizState = activeQuiz)
                } else if (selectedStatute != null) {
                    SmartReaderScreen(viewModel = viewModel, statute = selectedStatute!!)
                } else if (selectedCase != null) {
                    CaseDetailScreen(viewModel = viewModel, courtCase = selectedCase!!)
                } else if (selectedActDocument != null) {
                    ActDocumentDetailScreen(viewModel = viewModel, act = selectedActDocument!!)
                } else {
                    Scaffold(
                        bottomBar = {
                            LawDeskBottomNavigation(
                                currentTab = currentTab,
                                isDark = isDark,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                        },
                        containerColor = backgroundColor
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                AppTab.HOME -> HomeScreen(viewModel = viewModel)
                                AppTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                                AppTab.AI -> AITutorScreen(viewModel = viewModel)
                                AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // High-fidelity Floating Daily study banner notification
                AnimatedVisibility(
                    visible = showBanner,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    NotificationBannerCard(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                }
            }
        }
    }
}

// --- BRIEFING SCREEN ---
@Composable
fun BriefingScreen(viewModel: MainViewModel) {
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    
    val bg = if (isDark) PureBlack else PureWhite
    val textCol = if (isDark) PureWhite else PureBlack
    val grayCol = if (isDark) Color(0xFF8E8E93) else Color(0x99000000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .testTag("briefing_screen")
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Elegant brand icon box with dynamic iOS Blue gradient background
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF007AFF), Color(0xFF5AC8FA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_judgment),
                    contentDescription = "Scales",
                    modifier = Modifier.size(52.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dual-styled dynamic welcome text
            Text(
                text = androidx.compose.ui.text.buildAnnotatedString {
                    append("Welcome back,\n")
                    pushStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF007AFF), fontWeight = FontWeight.ExtraBold))
                    append(userStats.name.uppercase())
                    pop()
                    append(" 👋")
                },
                style = MaterialTheme.typography.headlineLarge.copy(
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = textCol,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Ready to help with your study sessions.",
                color = grayCol,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Primary pill action button
            Button(
                onClick = { viewModel.dismissBriefing() },
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
                    .testTag("begin_briefing_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Jump In",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BriefingMetricItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray900, RoundedCornerShape(12.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(GlassBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPremium,
                modifier = Modifier.size(20.dp)
            )
        }

        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Gray600
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --- DYNAMIC SLOGANS & HOLIDAY OVERRIDES ENGINE ---
data class BannerContent(
    val heading: String,
    val subheading: String
)

fun getDailyBannerContent(day: Int, month: Int, dayOfYear: Int): BannerContent {
    // Kenyan National Holidays Checks
    if (month == 6 && day == 1) {
        return BannerContent(
            heading = "HAPPY MADARAKA DAY",
            subheading = "Celebrating self-rule, sovereign authority, and the constitutional foundation of our republic."
        )
    }
    if (month == 10 && day == 20) {
        return BannerContent(
            heading = "HAPPY MASHUJAA DAY",
            subheading = "May this day be a celebration not only for the heroes who died for our freedom but also for our courage and the battles we win in silence."
        )
    }
    if (month == 12 && day == 12) {
        return BannerContent(
            heading = "HAPPY JAMHURI DAY",
            subheading = "Celebrating the birth of our republic, independence, and the rule of law safeguarding our collective progress."
        )
    }
    if (month == 5 && day == 1) {
        return BannerContent(
            heading = "HAPPY LABOUR DAY",
            subheading = "Honoring the hands that build the nation and the socio-economic rights guaranteed under Article 43."
        )
    }
    if (month == 10 && day == 10) {
        return BannerContent(
            heading = "HAPPY UTAMADUNI DAY",
            subheading = "Honoring our diverse heritage, community service, and rich national solidarity as enshrined in Article 10."
        )
    }
    if (month == 1 && day == 1) {
        return BannerContent(
            heading = "HAPPY NEW YEAR",
            subheading = "Embarking on a new milestone of learning, civic awareness, and legal excellence."
        )
    }
    if (month == 12 && day == 25) {
        return BannerContent(
            heading = "MERRY CHRISTMAS",
            subheading = "Wishing you a peaceful legal recess filled with warmth, family, and reflective wisdom."
        )
    }
    if (month == 12 && day == 26) {
        return BannerContent(
            heading = "HAPPY BOXING DAY",
            subheading = "Enjoying the holiday recess, celebrating community generosity, and restoring the scales of life."
        )
    }

    val rawHeadings = listOf(
        "MASTER THE CONSTITUTION",
        "STRENGTHEN CIVIC LITERACY",
        "DISCOVER LEADING CASE LAW",
        "UPHOLD THE RULE OF LAW",
        "CHAMPION CONSTITUTIONAL VALUES",
        "SAFEGUARD INDIVIDUAL LIBERTY",
        "EXPLORE PUBLIC JURISPRUDENCE",
        "MASTER DEVOLUTION DOCTRINES",
        "UNDERSTAND LANDMARK DECREES",
        "PROTECT FUNDAMENTAL FREEDOMS",
        "NAVIGATE STATUTORY MANDATES",
        "FOSTER PROFESSIONAL INTEGRITY",
        "ADVANCE ENVIRONMENTAL JUSTICE",
        "DECODE CONSUMER PROTECTION",
        "ANALYZE JUDICIAL DICTA",
        "SEEK CIVIL JUSTICE STANDARDS",
        "STUDY LABOUR LAW PROTOCOLS",
        "COMPREHEND SOVEREIGN TRUSTS",
        "UNVEIL HISTORIC PRECEDENTS",
        "TRACE LEGAL LANDSCAPES",
        "UNRAVEL THE BILL OF RIGHTS",
        "REFLECT ON NATIONAL SECURITY",
        "RESOLVE JURISDICTIONAL RIDDLES",
        "SCRUTINIZE EXECUTIVE VETOES",
        "INTERPRET LEGISLATIVE ENACTS",
        "HARNESS CONSTITUTIONAL REMEDIES",
        "EXPLORE DISPUTE RESOLUTION",
        "DEVISE LITIGATION INSIGHTS",
        "CULTIVATE PROCEDURAL FAIRNESS",
        "EMBRACE SYSTEMIC JUSTICE"
    )

    val rawSubheadings = listOf(
        "Kenyan jurisprudence, verified statutes, and historic precedents at your fingertips.",
        "Deep dive into state administrative structures, checks and balances of decentralized authority.",
        "A detailed roadmap to civic responsibilities and sovereign mandates of individual citizens.",
        "Demystifying judicial reasoning and legal philosophies that govern our Supreme Court.",
        "Unlocking the core dynamics of public finance management and institutional accountability.",
        "Exploring environmental stewardship and constitutional provisions on land usage rights.",
        "Empowering communities through direct access to local administrative statutory frameworks.",
        "A professional study space designed for legal scholars, advocates, and curious minds.",
        "Delving into the checks and balances between legislative, executive, and judicial organs.",
        "Tracing how Article 43 guarantees socio-economic rights from healthcare to housing."
    )

    // Mathematically combine variations to exceed 300+ entries uniquely (30 headings * 10 subheadings = 300)
    val index = dayOfYear % 300
    val headingIdx = (index / 10) % 30
    val subheadingIdx = index % 10

    return BannerContent(
        heading = rawHeadings[headingIdx],
        subheading = rawSubheadings[subheadingIdx]
    )
}

// --- HOME SCREEN ---
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val statutes by viewModel.statutes.collectAsStateWithLifecycle()
    val cases by viewModel.cases.collectAsStateWithLifecycle()
    val news by viewModel.news.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val backgroundColor = if (isDark) PureBlack else OffWhite
    val textColor = if (isDark) OffWhite else PureBlack
    val secondaryTextColor = if (isDark) Gray300 else Gray800
    val mutedTextColor = if (isDark) Gray600 else Color(0xFF707074)
    val cardBgc = if (isDark) Color(0xFF161513) else PureWhite
    val cardBdrColor = if (isDark) GlassBorder else Gray300

    val lawImages = remember {
        listOf(
            "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1505664194779-8bebcb95c539?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1450133064473-71024230f91b?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1447069387593-a5de0862481e?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1560179707-f14e90ef3623?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1427504494785-3a9ca7044f45?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1436450412740-6b988f486c6b?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1541872703-74c5e44368f9?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1575505586569-6d3330dbdf1f?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1528495612344-17feb4b8a284?auto=format&fit=crop&q=80&w=800"
        )
    }

    val calendar = remember { Calendar.getInstance() }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

    val selectedImage = remember(dayOfYear) {
        lawImages[dayOfYear % 16]
    }

    val bannerContent = remember(day, month, dayOfYear) {
        getDailyBannerContent(day = day, month = month, dayOfYear = dayOfYear)
    }

    val todayArticle = remember(statutes) {
        val conStatutes = statutes.filter { it.category == "CONSTITUTION" }.sortedBy { it.id }
        if (conStatutes.isNotEmpty()) {
            val cal = Calendar.getInstance()
            val doy = cal.get(Calendar.DAY_OF_YEAR)
            conStatutes[doy % conStatutes.size]
        } else {
            val sortedList = statutes.sortedBy { it.id }
            sortedList.firstOrNull { it.category == "CONSTITUTION" } ?: sortedList.firstOrNull()
        }
    }

    LaunchedEffect(todayArticle?.id) {
        todayArticle?.let {
            viewModel.fetchCaseAndNewsForToday(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_container")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
                .testTag("home_screen"),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
        // 1. LAWDESK HEADER ROW
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LawDesk",
                        style = MaterialTheme.typography.headlineLarge,
                        color = textColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Good afternoon, ${userStats.name.ifBlank { "Guest" }}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = mutedTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Flame Streak pill
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFEDD5), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = IosIcons.Flame,
                                contentDescription = "Streak",
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${userStats.streak}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFEA580C),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // XP Pill
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E7FF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = IosIcons.Zap,
                                contentDescription = "XP",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${userStats.xp}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF4F46E5),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. FEATURED STATUTE OF THE DAY CARD ("STATUTE OF THE DAY")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A))
                    .shadow(8.dp, RoundedCornerShape(24.dp))
            ) {
                // Background watermark scales icon
                Icon(
                    imageVector = IosIcons.Scale,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 20.dp, y = (-10).dp)
                )

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    val todayFormattedDate = remember {
                        try {
                            java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale.ENGLISH).format(java.util.Date()).uppercase()
                        } catch (e: Exception) {
                            "TODAY"
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = todayFormattedDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = todayArticle?.sectionNumber ?: "ARTICLE 1",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPremium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = todayArticle?.title ?: "Sovereignty of the People",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = todayArticle?.originalText ?: "All sovereign power belongs to the people of Kenya and shall be exercised only in accordance with this Constitution.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (todayArticle != null) {
                                viewModel.selectStatute(todayArticle!!)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = IosIcons.BookOpen,
                                contentDescription = "Read Article",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Read Article",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
        }

        // 3. MAXIM OF THE DAY SECTION (MOVED ABOVE CONSTITUTION)
        item {
            val maximOfTheDay by viewModel.maximOfTheDay.collectAsStateWithLifecycle()
            val maxims by viewModel.maxims.collectAsStateWithLifecycle()
            var showMaximsDialog by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0xFF4338CA))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4F46E5), // Indigo 600
                                Color(0xFF3730A3)  // Indigo 800
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .clickable { showMaximsDialog = true }
                        .padding(22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.Award,
                            contentDescription = "Maxim Icon",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "MAXIM OF THE DAY",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "\"${maximOfTheDay?.latin ?: "Actionable per se"}\"",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = maximOfTheDay?.translation ?: "The very act itself is punishable; no proof of damage is required to proceed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE0E7FF),
                        lineHeight = 22.sp
                    )
                }
            }

            if (showMaximsDialog) {
                MaximsBottomSheet(
                    maxims = maxims,
                    onDismiss = { showMaximsDialog = false }
                )
            }
        }

        // 4. DAILY OBJECTIVES SECTION (CONSTITUTION & CASE OF THE DAY)
        item {
            Column {
                Text(
                    text = "Daily Objectives",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Constitution Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBgc),
                        border = BorderStroke(1.dp, cardBdrColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (todayArticle != null) {
                                    viewModel.selectStatute(todayArticle)
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = IosIcons.BookOpen,
                                            contentDescription = "Constitution",
                                            tint = Color(0xFF4F46E5),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = "CONSTITUTION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = todayArticle?.sectionNumber ?: "Art. 255",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF4F46E5),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = todayArticle?.title ?: "Amendment of this Constitution",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = todayArticle?.originalText ?: "(1) A proposed amendment to this Constitution...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = mutedTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Case of the Day Card with AI-Driven Analysis
                    val caseDay = remember(cases, todayArticle) {
                        if (todayArticle != null) {
                            val matching = cases.filter {
                                it.articleNumber.contains(todayArticle.sectionNumber, ignoreCase = true) ||
                                todayArticle.sectionNumber.contains(it.articleNumber, ignoreCase = true)
                            }.sortedBy { it.id }
                            matching.firstOrNull() ?: cases.sortedBy { it.id }.firstOrNull()
                        } else {
                            cases.sortedBy { it.id }.firstOrNull()
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBgc),
                        border = BorderStroke(1.dp, GoldPremium.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("case_of_the_day_card")
                            .clickable {
                                if (caseDay != null) {
                                    viewModel.selectCase(caseDay)
                                }
                            }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(GoldPremium.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = IosIcons.Shield,
                                            contentDescription = "Case of the Day",
                                            tint = GoldPremium,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "CASE OF THE DAY",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldPremium,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp
                                        )
                                        Text(
                                            text = "AI-DRIVEN PRECEDENT ANALYSIS",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = mutedTextColor,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(GoldPremium.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .border(1.dp, GoldPremium.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = caseDay?.citation ?: "eKLR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldPremium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = caseDay?.title ?: "Francis Karioko Muruatetu v Republic",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mini preview chips of the 5 AI sections
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(GoldPremium, CircleShape)
                                    )
                                    Text(
                                        text = "Facts: ${caseDay?.facts ?: "Landmark case analysis..."}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = secondaryTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(GoldPremium, CircleShape)
                                    )
                                    Text(
                                        text = "Holding: ${caseDay?.holding ?: "Binding ruling..."}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = secondaryTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(GoldPremium, CircleShape)
                                    )
                                    Text(
                                        text = "Exam Tip: ${caseDay?.examTip ?: "Essential student takeaway..."}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GoldPremium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "OPEN FULL AI ANALYSIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldPremium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = IosIcons.ChevronRight,
                                    contentDescription = null,
                                    tint = GoldPremium,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. TODAY'S QUIZ GRAPHICAL CARD (AT THE VERY BOTTOM)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF0284C7))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0284C7), // Sky blue / Teal gradient
                                Color(0xFF0F172A)  // Deep slate
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Decorative background watermark icon
                    Icon(
                        imageVector = IosIcons.Zap,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(130.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 25.dp, y = 25.dp)
                    )

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = IosIcons.Award,
                                        contentDescription = "Quiz",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "TODAY'S QUIZ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(GoldPremium, CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+50 XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureBlack,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Test Your Daily Legal Knowledge",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Challenge yourself with interactive questions on Kenyan constitutional provisions, precedent cases & legal doctrines.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                viewModel.startOfflineMockQuiz("CONSTITUTION & RIGHTS")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPremium,
                                contentColor = PureBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = IosIcons.PlayCircle,
                                    contentDescription = "Start Quiz",
                                    tint = PureBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Take Today's Quiz",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PureBlack
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Floating Wiggy Coach in bottom right/center right!
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 32.dp)
            .width(170.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Speech Bubble (Clickable to trigger Practice Quiz)
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF161513) else PureWhite),
                border = BorderStroke(1.dp, if (isDark) GlassBorder else Gray300),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clickable { viewModel.startDailyJourneyQuiz() }
                    .padding(end = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Wanna test\nyour wits? 🦉",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 11.sp,
                        lineHeight = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "DAILY SPECIAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPremium,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Interactive pulsating Mascot
            WiggyTheJudge(
                expression = MascotExpr.IDLE,
                modifier = Modifier
                    .size(68.dp)
                    .clickable { viewModel.startDailyJourneyQuiz() }
            )
        }
    }
  }
}

// --- CONSTITUTION & STATUTORY LIBRARY SCREEN ---
@Composable
fun LibraryScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val statutes by viewModel.statutes.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    
    val backgroundColor = if (isDark) PureBlack else OffWhite
    val cardBgc = if (isDark) Gray900 else Color.White
    val cardBdrColor = if (isDark) GlassBorder else DividerColor
    val textColor = if (isDark) OffWhite else PureBlack
    val mutedTextColor = if (isDark) Gray600 else Color(0xFF64748B)

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Constitution") }
    var selectedChapter by remember { mutableStateOf("All Chapters") }
    var showOnlyBookmarks by remember { mutableStateOf(false) }

    val categories = listOf(
        "Constitution",
        "All Acts",
        "Criminal",
        "Evidence",
        "Civil & Commercial",
        "Judicature",
        "Rights & Protection"
    )

    val chaptersList = listOf(
        "All Chapters",
        "Chapter 1: Sovereignty & Supremacy",
        "Chapter 2: The Republic",
        "Chapter 3: Citizenship",
        "Chapter 4: The Bill of Rights",
        "Chapter 5: Land & Environment",
        "Chapter 6: Leadership & Integrity",
        "Chapter 7: Representation of the People",
        "Chapter 8: The Legislature",
        "Chapter 9: The Executive",
        "Chapter 10: Judiciary",
        "Chapter 11: Devolved Government",
        "Chapter 12: Public Finance",
        "Chapter 13: The Public Service",
        "Chapter 14: National Security",
        "Chapter 15: Commissions & Independent Offices",
        "Chapter 16: Amendment of this Constitution",
        "Chapter 17: General Provisions",
        "Chapter 18: Transitional & Consequential",
        "Schedules"
    )

    fun getArticleNum(secNum: String): Int {
        return try {
            secNum.replace(Regex("[^0-9]"), "").toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun getChapterForArticle(articleNum: Int): String {
        return when (articleNum) {
            in 1..3 -> "Chapter 1: Sovereignty & Supremacy"
            in 4..11 -> "Chapter 2: The Republic"
            in 12..18 -> "Chapter 3: Citizenship"
            in 19..59 -> "Chapter 4: The Bill of Rights"
            in 60..72 -> "Chapter 5: Land & Environment"
            in 73..80 -> "Chapter 6: Leadership & Integrity"
            in 81..92 -> "Chapter 7: Representation of the People"
            in 93..128 -> "Chapter 8: The Legislature"
            in 129..158 -> "Chapter 9: The Executive"
            in 159..173 -> "Chapter 10: Judiciary"
            in 174..200 -> "Chapter 11: Devolved Government"
            in 201..231 -> "Chapter 12: Public Finance"
            in 232..237 -> "Chapter 13: The Public Service"
            in 238..247 -> "Chapter 14: National Security"
            in 248..254 -> "Chapter 15: Commissions & Independent Offices"
            in 255..257 -> "Chapter 16: Amendment of this Constitution"
            in 258..260 -> "Chapter 17: General Provisions"
            in 261..264 -> "Chapter 18: Transitional & Consequential"
            else -> "Schedules"
        }
    }

    // Filter logic
    val filteredStatutes = remember(statutes, searchQuery, selectedCategory, selectedChapter, showOnlyBookmarks) {
        statutes.filter { statute ->
            // Bookmark filter
            if (showOnlyBookmarks && !statute.isBookmarked) return@filter false

            // Category filter
            val matchesCategory = when (selectedCategory) {
                "Constitution" -> statute.category == "CONSTITUTION"
                "All Acts" -> true
                "Criminal" -> statute.category == "CRIMINAL" || statute.actName.contains("Penal", ignoreCase = true) || statute.actName.contains("Prisons", ignoreCase = true) || statute.actName.contains("Torture", ignoreCase = true) || statute.actName.contains("Probation", ignoreCase = true)
                "Evidence" -> statute.category == "EVIDENCE" || statute.actName.contains("Evidence", ignoreCase = true)
                "Civil & Commercial" -> statute.category == "CIVIL" || statute.category == "COMMERCIAL" || statute.actName.contains("Contract", ignoreCase = true) || statute.actName.contains("Sale of Goods", ignoreCase = true) || statute.actName.contains("Insolvency", ignoreCase = true) || statute.actName.contains("Consumer", ignoreCase = true) || statute.actName.contains("Partnership", ignoreCase = true)
                "Judicature" -> statute.actName.contains("Judicature", ignoreCase = true) || statute.actName.contains("Judicial Service", ignoreCase = true) || statute.actName.contains("Legal Education", ignoreCase = true) || statute.actName.contains("Kadhis", ignoreCase = true)
                "Rights & Protection" -> statute.actName.contains("Victim", ignoreCase = true) || statute.actName.contains("Witness", ignoreCase = true) || statute.actName.contains("Restraint", ignoreCase = true) || statute.actName.contains("Goods", ignoreCase = true)
                else -> true
            }
            if (!matchesCategory) return@filter false

            // Constitution chapter filter
            if (selectedCategory == "Constitution" && selectedChapter != "All Chapters") {
                val artNum = getArticleNum(statute.sectionNumber)
                val chName = getChapterForArticle(artNum)
                if (chName != selectedChapter && !statute.whyItMatters.contains(selectedChapter.take(10), ignoreCase = true)) {
                    return@filter false
                }
            }

            // Search query filter
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                statute.sectionNumber.lowercase().contains(q) ||
                statute.title.lowercase().contains(q) ||
                statute.actName.lowercase().contains(q) ||
                statute.originalText.lowercase().contains(q) ||
                statute.aiInterpretation.lowercase().contains(q)
            } else {
                true
            }
        }.sortedWith(
            compareBy<Statute> { if (it.category == "CONSTITUTION") 0 else 1 }
                .thenBy { getArticleNum(it.sectionNumber) }
                .thenBy { it.sectionNumber }
        )
    }

    val constitutionArticlesCount = remember(statutes) {
        statutes.count { it.category == "CONSTITUTION" }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .testTag("library_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CONSTITUTION & STATUTES",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPremium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "LawDesk Library",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // LexsHub Web Portal Button
                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lexshub.xyz"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(cardBgc, CircleShape)
                                    .border(1.dp, cardBdrColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = IosIcons.Share,
                                    contentDescription = "LexsHub Web",
                                    tint = GoldPremium,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Bookmarks Only Filter Toggle
                            IconButton(
                                onClick = {
                                    SoundVibrationHelper.tick(context)
                                    showOnlyBookmarks = !showOnlyBookmarks
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (showOnlyBookmarks) GoldPremium.copy(alpha = 0.2f) else cardBgc,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (showOnlyBookmarks) GoldPremium else cardBdrColor,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (showOnlyBookmarks) IosIcons.BookmarkFilled else IosIcons.Bookmark,
                                    contentDescription = "Bookmarks",
                                    tint = if (showOnlyBookmarks) GoldPremium else textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${statutes.size} provisions indexed • $constitutionArticlesCount Constitution Articles • 27 Acts",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedTextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instant Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (selectedCategory == "Constitution") "Search Article 1-264, Bill of Rights, Land..." else "Search sections, keywords, definitions...",
                                color = mutedTextColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = IosIcons.Search,
                                contentDescription = "Search",
                                tint = GoldPremium,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = IosIcons.Close,
                                        contentDescription = "Clear",
                                        tint = mutedTextColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBgc,
                            unfocusedContainerColor = cardBgc,
                            focusedBorderColor = GoldPremium,
                            unfocusedBorderColor = cardBdrColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("library_search_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    SoundVibrationHelper.tick(context)
                                    selectedCategory = category
                                },
                                label = {
                                    Text(
                                        text = category,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    if (category == "Constitution") {
                                        Icon(
                                            imageVector = IosIcons.BookOpen,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isSelected) PureBlack else Color(0xFF4F46E5)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPremium,
                                    selectedLabelColor = PureBlack,
                                    containerColor = cardBgc,
                                    labelColor = textColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) GoldPremium else cardBdrColor,
                                    selectedBorderColor = GoldPremium
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Constitution Chapter Selector (visible when Constitution is selected)
                    if (selectedCategory == "Constitution") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "CONSTITUTION CHAPTERS",
                            style = MaterialTheme.typography.labelSmall,
                            color = mutedTextColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(chaptersList) { chapter ->
                                val isChapSelected = selectedChapter == chapter
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isChapSelected) Color(0xFF4F46E5) else cardBgc,
                                    border = BorderStroke(1.dp, if (isChapSelected) Color(0xFF4F46E5) else cardBdrColor),
                                    modifier = Modifier
                                        .clickable {
                                            SoundVibrationHelper.tick(context)
                                            selectedChapter = chapter
                                        }
                                ) {
                                    Text(
                                        text = chapter,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isChapSelected) Color.White else textColor,
                                        fontWeight = if (isChapSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results count banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredStatutes.size} provisions found",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedTextColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (selectedChapter != "All Chapters" && selectedCategory == "Constitution") {
                        Text(
                            text = selectedChapter,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4F46E5),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Empty State
            if (filteredStatutes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = IosIcons.Search,
                                contentDescription = null,
                                tint = mutedTextColor,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "No matching provisions found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = if (showOnlyBookmarks) "You have not bookmarked any articles in this filter." else "Try searching by article number (e.g. 40, 22) or different keywords.",
                                style = MaterialTheme.typography.bodySmall,
                                color = mutedTextColor,
                                textAlign = TextAlign.Center
                            )
                            if (searchQuery.isNotBlank() || showOnlyBookmarks || selectedChapter != "All Chapters") {
                                Button(
                                    onClick = {
                                        searchQuery = ""
                                        showOnlyBookmarks = false
                                        selectedChapter = "All Chapters"
                                        selectedCategory = "Constitution"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPremium,
                                        contentColor = PureBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(top = 10.dp)
                                ) {
                                    Text("Reset Filters", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // List of Statutes / Constitution Articles
                items(filteredStatutes, key = { it.id }) { statute ->
                    ConstitutionArticleCard(
                        statute = statute,
                        isDark = isDark,
                        textColor = textColor,
                        mutedTextColor = mutedTextColor,
                        cardBgc = cardBgc,
                        cardBdrColor = cardBdrColor,
                        onRead = {
                            SoundVibrationHelper.tick(context)
                            viewModel.selectStatute(statute)
                        },
                        onBookmark = {
                            SoundVibrationHelper.tick(context)
                            viewModel.toggleBookmark(statute)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConstitutionArticleCard(
    statute: Statute,
    isDark: Boolean,
    textColor: Color,
    mutedTextColor: Color,
    cardBgc: Color,
    cardBdrColor: Color,
    onRead: () -> Unit,
    onBookmark: () -> Unit
) {
    val isConstitution = statute.category == "CONSTITUTION"
    val categoryBadgeColor = when (statute.category) {
        "CONSTITUTION" -> Color(0xFF4F46E5)
        "CRIMINAL" -> Color(0xFFDC2626)
        "EVIDENCE" -> Color(0xFF059669)
        "CIVIL", "COMMERCIAL" -> Color(0xFFD97706)
        else -> Color(0xFF2563EB)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBgc),
        border = BorderStroke(1.dp, cardBdrColor),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onRead() }
            .testTag("statute_card_${statute.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row with Section pill, Act badge, and bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Section Number Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = categoryBadgeColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = statute.sectionNumber,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = categoryBadgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Act name / Chapter category tag
                    Text(
                        text = statute.actName,
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (statute.isBookmarked) IosIcons.BookmarkFilled else IosIcons.Bookmark,
                        contentDescription = "Bookmark",
                        tint = if (statute.isBookmarked) GoldPremium else mutedTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Article Title
            Text(
                text = statute.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Text snippet preview
            Text(
                text = statute.originalText,
                style = MaterialTheme.typography.bodyMedium,
                color = mutedTextColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (statute.isRead) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.CheckCircle,
                            contentDescription = "Read",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = if (isConstitution) "Constitution of Kenya 2010" else "Act of Parliament",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedTextColor.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "READ & INTERPRET",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isConstitution) Color(0xFF4F46E5) else GoldPremium,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = IosIcons.ArrowForward,
                        contentDescription = null,
                        tint = if (isConstitution) Color(0xFF4F46E5) else GoldPremium,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// --- SMART WRITER/READER WORKSPACE ---
@Composable
fun SmartReaderScreen(viewModel: MainViewModel, statute: Statute) {
    val context = androidx.compose.ui.platform.LocalContext.current
    BackHandler {
        viewModel.selectStatute(null)
    }

    val aiExplanation by viewModel.aiReaderExplanation.collectAsStateWithLifecycle()
    val isAIProgress by viewModel.isReadingExplanationLoading.collectAsStateWithLifecycle()
    val statutes by viewModel.statutes.collectAsStateWithLifecycle()
    val isDarkAppTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var isReaderLightMode by remember { mutableStateOf(!isDarkAppTheme) }

    // Dynamic reader theme color definitions
    val readerBgColor = if (isReaderLightMode) Color(0xFFFAF9F6) else PureBlack
    val readerCardBgColor = if (isReaderLightMode) Color(0xFFFFFFFF) else Gray900
    val readerCardBorderColor = if (isReaderLightMode) Color(0xFFE2E8F0) else GlassBorder
    val readerTextColor = if (isReaderLightMode) Color(0xFF1A202C) else OffWhite
    val readerMutedTextColor = if (isReaderLightMode) Color(0xFF718096) else Gray600
    val readerAccentColor = if (statute.category == "CONSTITUTION") Color(0xFF4F46E5) else GoldPremium
    val readerDividerColor = if (isReaderLightMode) Color(0xFFE2E8F0) else DividerColor

    val getArticleNumberInt = { secNum: String ->
        try {
            secNum.replace(Regex("[^0-9]"), "").toInt()
        } catch (e: Exception) {
            0
        }
    }

    val constitutionList = remember(statutes) {
        statutes.filter { it.category == "CONSTITUTION" }.sortedBy { getArticleNumberInt(it.sectionNumber) }
    }
    val currentIndex = constitutionList.indexOfFirst { it.id == statute.id }
    val prevStatute = if (currentIndex > 0) constitutionList[currentIndex - 1] else null
    val nextStatute = if (currentIndex != -1 && currentIndex < constitutionList.size - 1) constitutionList[currentIndex + 1] else null

    val scrollState = rememberLazyListState()

    val scrollProgress by remember {
        derivedStateOf {
            val totalItems = scrollState.layoutInfo.totalItemsCount
            if (totalItems <= 1) 0f
            else {
                val visible = scrollState.layoutInfo.visibleItemsInfo
                if (visible.isEmpty()) 0f
                else {
                    val lastVisible = visible.last().index
                    (lastVisible.toFloat() / (totalItems - 1).toFloat()).coerceIn(0f, 1f)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBgColor)
            .testTag("smart_reader_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Core micro-interaction progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(readerDividerColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(scrollProgress)
                        .background(readerAccentColor)
                )
            }

            // Header bar with Light Theme Toggle and Bookmark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.selectStatute(null) }) {
                    Icon(
                        imageVector = IosIcons.ChevronLeft,
                        contentDescription = "Back",
                        tint = readerAccentColor
                    )
                }

                Text(
                    text = if (statute.category == "CONSTITUTION") "CONSTITUTION READER" else "SMART READER",
                    style = MaterialTheme.typography.labelLarge,
                    color = readerAccentColor,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            SoundVibrationHelper.tick(context)
                            isReaderLightMode = !isReaderLightMode
                        },
                        modifier = Modifier.testTag("smart_reader_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isReaderLightMode) IosIcons.Moon else IosIcons.Sun,
                            contentDescription = if (isReaderLightMode) "Switch to Dark Theme" else "Switch to Light Theme",
                            tint = readerAccentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = {
                        SoundVibrationHelper.tick(context)
                        viewModel.toggleBookmark(statute)
                    }) {
                        Icon(
                            imageVector = if (statute.isBookmarked) IosIcons.BookmarkFilled else IosIcons.Bookmark,
                            contentDescription = "Bookmark",
                            tint = if (statute.isBookmarked) GoldPremium else readerAccentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Scrollable Content with comfortable margins
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "${statute.actName} — ${statute.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = readerMutedTextColor,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statute.sectionNumber,
                            style = MaterialTheme.typography.displaySmall,
                            color = readerAccentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statute.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = readerTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Core original text
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = readerCardBgColor),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, readerCardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "ORIGINAL LAW TEXT",
                                style = MaterialTheme.typography.labelSmall,
                                color = readerAccentColor,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = statute.originalText.ifBlank { "Text is available in the official gazette publication." },
                                style = MaterialTheme.typography.bodyLarge,
                                color = readerTextColor,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }

                // AI Discuss Button
                item {
                    Button(
                        onClick = { viewModel.discussStatuteWithAI(statute) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = readerAccentColor,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = IosIcons.ScalesOfJustice,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "DISCUSS & INTERPRET WITH AI",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Annotation Drawer: Plain English Interpretation
                if (statute.aiInterpretation.isNotBlank()) {
                    item {
                        AnnotationCard(
                            title = "PLAIN ENGLISH INTERPRETATION",
                            content = statute.aiInterpretation,
                            icon = IosIcons.List,
                            cardBg = readerCardBgColor,
                            textColor = readerTextColor,
                            accentColor = readerAccentColor,
                            borderColor = readerCardBorderColor
                        )
                    }
                }

                // Annotation Drawer: Why It Matters
                if (statute.whyItMatters.isNotBlank()) {
                    item {
                        AnnotationCard(
                            title = "WHY IT MATTERS",
                            content = statute.whyItMatters,
                            icon = IosIcons.Info,
                            cardBg = readerCardBgColor,
                            textColor = readerTextColor,
                            accentColor = readerAccentColor,
                            borderColor = readerCardBorderColor
                        )
                    }
                }

                // Annotation Drawer: Practical Example
                if (statute.practicalExample.isNotBlank()) {
                    item {
                        AnnotationCard(
                            title = "REAL-LIFE PRACTICAL EXAMPLE",
                            content = statute.practicalExample,
                            icon = IosIcons.StarFilled,
                            cardBg = readerCardBgColor,
                            textColor = readerTextColor,
                            accentColor = readerAccentColor,
                            borderColor = readerCardBorderColor
                        )
                    }
                }

                // Annotation Drawer: Practice Advice
                if (statute.examTip.isNotBlank()) {
                    item {
                        AnnotationCard(
                            title = "PRACTICE ADVICE",
                            content = statute.examTip,
                            icon = IosIcons.Star,
                            goldBorder = true,
                            cardBg = readerCardBgColor,
                            textColor = readerTextColor,
                            accentColor = GoldPremium,
                            borderColor = readerCardBorderColor
                        )
                    }
                }

                // Live Advanced AI Tutoring Analysis
                item {
                    if (aiExplanation == null && !isAIProgress) {
                        Button(
                            onClick = { viewModel.explainWithAI(statute) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, readerAccentColor, RoundedCornerShape(12.dp))
                                .testTag("generate_ai_analysis_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = readerBgColor,
                                contentColor = readerAccentColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(IosIcons.StarFilled, contentDescription = null, tint = readerAccentColor)
                                Text(
                                    text = "Query LawDesk Deep Analysis",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isAIProgress) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = readerCardBgColor),
                            border = BorderStroke(1.dp, readerAccentColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = readerAccentColor, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Retrieving transcripts and parsing annotations...",
                                    color = readerTextColor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isReaderLightMode) Color(0xFFFFFDF5) else Gray950Background()),
                            border = BorderStroke(1.dp, readerAccentColor)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(IosIcons.StarFilled, contentDescription = null, tint = readerAccentColor)
                                        Text(
                                            text = "LAWDESK CLINICAL DEEP REPORT",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = readerAccentColor,
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(onClick = { viewModel.explainWithAI(statute) }) {
                                        Icon(IosIcons.Refresh, contentDescription = "Regenerate", tint = readerMutedTextColor)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = aiExplanation ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = readerTextColor,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // Quick Assessment Quiz clicker
                item {
                    Button(
                        onClick = { viewModel.startArticleQuiz(statute) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_trigger_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = readerAccentColor,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(IosIcons.Edit, contentDescription = null, tint = PureWhite)
                            Text(
                                text = "Test My Understanding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Navigation Arrows for Constitution Articles
                if (statute.category == "CONSTITUTION") {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { prevStatute?.let { viewModel.selectStatute(it) } },
                                enabled = prevStatute != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = readerCardBgColor,
                                    contentColor = readerAccentColor,
                                    disabledContainerColor = readerBgColor,
                                    disabledContentColor = readerMutedTextColor
                                ),
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (prevStatute != null) readerAccentColor else readerDividerColor)
                            ) {
                                Icon(IosIcons.ChevronLeft, contentDescription = null, tint = if (prevStatute != null) readerAccentColor else readerMutedTextColor)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prev Article", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { nextStatute?.let { viewModel.selectStatute(it) } },
                                enabled = nextStatute != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = readerCardBgColor,
                                    contentColor = readerAccentColor,
                                    disabledContainerColor = readerBgColor,
                                    disabledContentColor = readerMutedTextColor
                                ),
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (nextStatute != null) readerAccentColor else readerDividerColor)
                            ) {
                                Text("Next Article", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(IosIcons.ChevronRight, contentDescription = null, tint = if (nextStatute != null) readerAccentColor else readerMutedTextColor)
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button properly positioned in outer Box
        FloatingActionButton(
            onClick = {
                SoundVibrationHelper.tick(context)
                viewModel.discussStatuteWithAI(statute)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .testTag("fab_discuss_ai"),
            containerColor = readerAccentColor,
            contentColor = PureWhite,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = IosIcons.ScalesOfJustice,
                    contentDescription = "Discuss with AI",
                    tint = PureWhite,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Discuss with AI",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}

// Support dark variant color helper
fun Gray950Background(): Color = Color(0xFF09090A)

@Composable
fun AnnotationCard(
    title: String,
    content: String,
    icon: ImageVector,
    goldBorder: Boolean = false,
    cardBg: Color = Gray900,
    textColor: Color = OffWhite,
    accentColor: Color = GoldPremium,
    borderColor: Color = GlassBorder
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (goldBorder) accentColor.copy(alpha = 0.5f) else borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                lineHeight = 22.sp
            )
        }
    }
}

// --- ACT DOCUMENT DEEP VIEW / PDF READER SCREEN ---
@Composable
fun ActDocumentDetailScreen(viewModel: MainViewModel, act: ActDocument) {
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isDownloadingPdf by viewModel.isDownloadingPdf.collectAsStateWithLifecycle()
    val statutes by viewModel.statutes.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val textColor = if (isDark) OffWhite else PureBlack
    val secondaryTextColor = if (isDark) Gray300 else Gray800
    val mutedTextColor = if (isDark) Gray600 else Color(0xFF707074)
    val cardBgc = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
    val cardBdrColor = if (isDark) GlassBorder else DividerColor

    var selectedTab by remember { mutableIntStateOf(0) } // 0: PDF Document Viewer, 1: Provisions Index

    // Filter statutes belonging to this Act
    val actProvisions = remember(statutes, act) {
        statutes.filter { it.actName.equals(act.actName, ignoreCase = true) || it.id.startsWith("act_" + act.actName.lowercase().replace(" ", "_")) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) PureBlack else OffWhite)
            .testTag("act_detail_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectActDocument(null) }) {
                Icon(
                    imageVector = IosIcons.ChevronLeft,
                    contentDescription = "Back",
                    tint = GoldPremium
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "LAWS OF KENYA",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPremium,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = GoldPremium.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = act.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPremium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = act.actName,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isDownloadingPdf) {
                CircularProgressIndicator(
                    color = GoldPremium,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Surface(
                    color = Color(0xFF34C759).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.Checkmark,
                            contentDescription = null,
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF34C759),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // View Mode Switcher: PDF Document vs Provisions Index
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFE5E5EA),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            SoundVibrationHelper.tick(context)
                            selectedTab = 0
                        },
                    color = if (selectedTab == 0) (if (isDark) Color(0xFF2C2C2E) else PureWhite) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = if (selectedTab == 0) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IosIcons.Library,
                            contentDescription = null,
                            tint = if (selectedTab == 0) GoldPremium else mutedTextColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL PDF VIEWER",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) textColor else mutedTextColor
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            SoundVibrationHelper.tick(context)
                            selectedTab = 1
                        },
                    color = if (selectedTab == 1) (if (isDark) Color(0xFF2C2C2E) else PureWhite) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = if (selectedTab == 1) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IosIcons.Search,
                            contentDescription = null,
                            tint = if (selectedTab == 1) GoldPremium else mutedTextColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INDEXED SECTIONS (${actProvisions.size})",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) textColor else mutedTextColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTab == 0) {
            // Direct Inline PDF Viewer with full rendering
            PdfInlineViewer(
                act = act,
                provisions = actProvisions,
                isDark = isDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            // Provisions Index List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (actProvisions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBgc),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBdrColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = IosIcons.Library,
                                    contentDescription = null,
                                    tint = GoldPremium,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "All provisions are compiled in the Official PDF Viewer tab above.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryTextColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(actProvisions, key = { it.id }) { prov ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectStatute(prov) },
                            colors = CardDefaults.cardColors(containerColor = cardBgc),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBdrColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prov.sectionNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = GoldPremium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = {
                                            SoundVibrationHelper.tick(context)
                                            viewModel.toggleBookmark(prov)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (prov.isBookmarked) IosIcons.BookmarkFilled else IosIcons.Bookmark,
                                            contentDescription = "Bookmark",
                                            tint = if (prov.isBookmarked) GoldPremium else mutedTextColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = prov.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = prov.originalText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryTextColor,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun CaseDetailScreen(viewModel: MainViewModel, courtCase: CourtCase) {
    val isCaseAnalysisLoading by viewModel.isCaseAnalysisLoading.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedCaseTab by remember { mutableIntStateOf(0) } // 0: AI Analysis, 1: Extracted PDF Document

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) PureBlack else OffWhite)
            .testTag("case_detail_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectCase(null) }) {
                Icon(
                    imageVector = IosIcons.ChevronLeft,
                    contentDescription = "Back",
                    tint = GoldPremium
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = IosIcons.Sparkles,
                    contentDescription = null,
                    tint = GoldPremium,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "AI-DRIVEN CASE ANALYSIS",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldPremium,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tab Selector for Analysis vs. PDF Document
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            color = if (isDark) Color(0xFF1C1C1E) else Color(0xFFE5E5EA),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            SoundVibrationHelper.tick(context)
                            selectedCaseTab = 0
                        },
                    color = if (selectedCaseTab == 0) (if (isDark) Color(0xFF2C2C2E) else PureWhite) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = if (selectedCaseTab == 0) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IosIcons.Sparkles,
                            contentDescription = null,
                            tint = if (selectedCaseTab == 0) GoldPremium else Gray600,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI BRIEF & ANALYSIS",
                            fontSize = 11.sp,
                            fontWeight = if (selectedCaseTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedCaseTab == 0) (if (isDark) OffWhite else PureBlack) else Gray600
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            SoundVibrationHelper.tick(context)
                            selectedCaseTab = 1
                        },
                    color = if (selectedCaseTab == 1) (if (isDark) Color(0xFF2C2C2E) else PureWhite) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = if (selectedCaseTab == 1) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = IosIcons.Book,
                            contentDescription = null,
                            tint = if (selectedCaseTab == 1) GoldPremium else Gray600,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL REPORT PDF",
                            fontSize = 11.sp,
                            fontWeight = if (selectedCaseTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedCaseTab == 1) (if (isDark) OffWhite else PureBlack) else Gray600
                        )
                    }
                }
            }
        }

        if (selectedCaseTab == 1) {
            val resolvedPdfPath by produceState(initialValue = courtCase.pdfUrl) {
                if (courtCase.pdfUrl.isNullOrBlank()) {
                    value = PdfDocumentHelper.getOrCreateCasePdfFile(context, courtCase).absolutePath
                } else {
                    value = courtCase.pdfUrl
                }
            }

            DocumentPdfViewer(
                documentTitle = courtCase.title,
                filePathOrUrl = resolvedPdfPath,
                isDark = isDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = courtCase.citation,
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPremium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = courtCase.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isDark) OffWhite else PureBlack,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Provision / Source: ${courtCase.articleNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) Gray300 else Gray600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Action Button
                    Button(
                        onClick = { viewModel.analyzeCaseWithAI(courtCase) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reanalyze_case_ai_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPremium,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isCaseAnalysisLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = IosIcons.Sparkles,
                                contentDescription = null,
                                tint = PureBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isCaseAnalysisLoading) "ANALYZING CASE WITH AI..." else "RE-ANALYZE CASE WITH AI",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (isCaseAnalysisLoading) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isDark) Gray900 else Color(0xFFF2F2F7)),
                            border = BorderStroke(1.dp, GoldPremium),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = GoldPremium, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "AI generating deep case analysis: Facts, Issues, Holding, Constitutional Interpretation & Student Exam Tips...",
                                    color = if (isDark) OffWhite else PureBlack,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                item {
                    CaseSegmentCard(
                        title = "FACTS",
                        subtitle = "Material Factual Background",
                        content = courtCase.facts,
                        icon = IosIcons.List
                    )
                }

                item {
                    CaseSegmentCard(
                        title = "ISSUES",
                        subtitle = "Contestable Legal & Constitutional Questions",
                        content = courtCase.issues,
                        icon = IosIcons.Info
                    )
                }

                item {
                    CaseSegmentCard(
                        title = "HOLDING",
                        subtitle = "Judicial Ruling & Binding Precedent",
                        content = courtCase.holding,
                        highlight = true,
                        icon = IosIcons.ScalesOfJustice
                    )
                }

                item {
                    CaseSegmentCard(
                        title = "WHY IT MATTERS",
                        subtitle = "Constitutional Interpretation & Jurisprudence",
                        content = courtCase.whyItMatters,
                        icon = IosIcons.StarFilled
                    )
                }

                item {
                    CaseSegmentCard(
                        title = "EXAM TIP",
                        subtitle = "Essential Takeaway for Law Students & Practitioners",
                        content = courtCase.examTip,
                        goldUnderline = true,
                        icon = IosIcons.Edit
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun CaseSegmentCard(
    title: String,
    subtitle: String = "",
    content: String,
    highlight: Boolean = false,
    goldUnderline: Boolean = false,
    icon: ImageVector? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (highlight) Gray950Background() else Gray900),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (highlight || goldUnderline) GoldPremium.copy(alpha = 0.5f) else GlassBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GoldPremium,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(GoldPremium, CircleShape)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldPremium,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray300
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite,
                lineHeight = 24.sp
            )
        }
    }
}

// --- AI TUTOR CHAT SCREEN ---
@Composable
fun AITutorScreen(viewModel: MainViewModel) {
    val messages by viewModel.currentSessionMessages.collectAsStateWithLifecycle()
    val chatInput by viewModel.chatInput.collectAsStateWithLifecycle()
    val isLoading by viewModel.aiTutorLoading.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var showHistoryDialog by remember { mutableStateOf(false) }
    var aiSubTab by remember { mutableStateOf("CHAT") }

    val keyboardController = LocalSoftwareKeyboardController.current

    val textColor = if (isDark) OffWhite else PureBlack
    val secondaryTextColor = if (isDark) Gray300 else Gray800
    val mutedTextColor = if (isDark) Gray600 else Color(0xFF707074)
    val cardBgc = if (isDark) Gray900 else Color(0xFFF2F2F7)
    val cardBdrColor = if (isDark) GlassBorder else DividerColor

    val recommendedPrompts = listOf(
        "Explain Article 43 like I am 15.",
        "Compare Article 24 and Article 25.",
        "Give me all cases related to Article 50.",
        "Test me on Article 40."
    )

    if (showHistoryDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Text(
                    text = "Discussion History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                val sessions by viewModel.chatSessions.collectAsStateWithLifecycle()
                val activeSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sessions) { session ->
                        val isActive = session.sessionId == activeSessionId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectChatSession(session.sessionId)
                                    showHistoryDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) GoldPremium.copy(alpha = 0.15f) else cardBgc
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isActive) GoldPremium else cardBdrColor
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.title,
                                        color = if (isActive) GoldPremium else textColor,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                }
                                if (isActive) {
                                    Icon(
                                        imageVector = IosIcons.Check,
                                        contentDescription = "Active",
                                        tint = GoldPremium,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close", color = GoldPremium)
                }
            },
            containerColor = if (isDark) Gray900 else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_tutor_screen")
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LawDesk Counsel",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kenyan Legal Companion & Case Researcher",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldPremium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // New Chat Button
                IconButton(
                    onClick = { 
                        viewModel.startNewChatSession() 
                        viewModel.setChatInput("")
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(cardBgc, CircleShape)
                ) {
                    Icon(IosIcons.Sparkles, contentDescription = "New Chat", tint = GoldPremium)
                }

                // Chat History List Button
                IconButton(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(cardBgc, CircleShape)
                ) {
                    Icon(IosIcons.List, contentDescription = "Chat History", tint = textColor)
                }

                // Clear Current Session Chat Button
                IconButton(
                    onClick = { viewModel.clearAIChat() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(cardBgc, CircleShape)
                ) {
                    Icon(IosIcons.Delete, contentDescription = "Clear Chat", tint = if (isDark) Gray600 else Gray800)
                }
            }
        }



        if (aiSubTab == "CHAT") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
            if (messages.isEmpty()) {
                // Welcome Layout with quick chips
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = IosIcons.StarFilled,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = GoldPremium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "I am your Constitutional Mentor.",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Query statutes or search precedents in real-time.",
                        color = mutedTextColor,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "TAP GUIDED TUTORIAL QUERIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPremium,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    recommendedPrompts.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.setChatInput(item)
                                    viewModel.sendChatMessage()
                                },
                            colors = CardDefaults.cardColors(containerColor = cardBgc),
                            border = BorderStroke(1.dp, cardBdrColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item,
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(IosIcons.ChevronRight, contentDescription = null, tint = GoldPremium, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Card(
                                modifier = Modifier
                                    .widthIn(max = 320.dp)
                                    .testTag("chat_msg_${if (isUser) "user" else "ai"}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) (if (isDark) Gray800 else Color(0xFFE5E5EA)) else cardBgc
                                ),
                                border = if (!isUser) BorderStroke(1.dp, GoldPremium.copy(alpha = 0.4f)) else BorderStroke(1.dp, cardBdrColor),
                                shape = if (isUser) {
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
                                } else {
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
                                }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = parseMarkdownToAnnotatedString(msg.content, isDark),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(GoldPremium, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("LD", fontSize = 11.sp, color = PureBlack, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Companion analyzing...", color = GoldPremium, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Float Glassmorphism Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(cardBgc, RoundedCornerShape(24.dp))
                .border(1.dp, cardBdrColor, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatInput,
                onValueChange = { viewModel.setChatInput(it) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_tutor_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                leadingIcon = {
                    Icon(
                        imageVector = IosIcons.MessageSquare,
                        contentDescription = "Chat",
                        tint = GoldPremium,
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = { Text("Query articles, rights, or case law...", color = mutedTextColor) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    viewModel.sendChatMessage()
                    keyboardController?.hide()
                })
            )

            IconButton(
                onClick = {
                    viewModel.sendChatMessage()
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(GoldPremium, CircleShape),
                enabled = chatInput.isNotEmpty()
            ) {
                Icon(
                    imageVector = IosIcons.Send,
                    contentDescription = "Send",
                    tint = PureBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    } else {
        val aiQuizError by viewModel.aiQuizError.collectAsStateWithLifecycle()
        var customTopic by remember { mutableStateOf("") }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
                if (aiQuizError != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1E1E)),
                            border = BorderStroke(1.dp, Color(0xFF5A2A2A))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(IosIcons.Close, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = aiQuizError!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OffWhite
                                )
                            }
                        }
                    }
                }

                // custom AI Gen Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgc),
                        border = BorderStroke(1.dp, cardBdrColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(IosIcons.Sparkles, contentDescription = null, tint = GoldPremium, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "DYNAMIC GEN-AI EXAM",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Enter any legal topic or act to generate a 5-question quiz.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = secondaryTextColor
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) PureBlack else PureWhite, RoundedCornerShape(12.dp))
                                    .border(1.dp, cardBdrColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                  TextField(
                                      value = customTopic,
                                      onValueChange = { customTopic = it },
                                      placeholder = { Text("e.g. Land Act, Matrimonial Property, Traffic Law", color = mutedTextColor, fontSize = 14.sp) },
                                      modifier = Modifier.weight(1f),
                                      colors = TextFieldDefaults.colors(
                                          focusedContainerColor = Color.Transparent,
                                          unfocusedContainerColor = Color.Transparent,
                                          focusedIndicatorColor = Color.Transparent,
                                          unfocusedIndicatorColor = Color.Transparent,
                                          focusedTextColor = textColor,
                                          unfocusedTextColor = textColor
                                      )
                                  )
                              }

                              Spacer(modifier = Modifier.height(14.dp))

                              Button(
                                  onClick = {
                                      if (customTopic.isNotBlank()) {
                                          viewModel.generateQuizWithAI(customTopic)
                                          keyboardController?.hide()
                                      }
                                  },
                                  colors = ButtonDefaults.buttonColors(containerColor = GoldPremium, contentColor = PureBlack),
                                  shape = RoundedCornerShape(12.dp),
                                  modifier = Modifier.fillMaxWidth(),
                                  enabled = customTopic.isNotBlank()
                              ) {
                                  Text(
                                      text = "COMPILE CUSTOM AI QUIZ",
                                      fontWeight = FontWeight.Bold,
                                      style = MaterialTheme.typography.labelMedium
                                  )
                              }
                        }
                    }
                }

                // Local Categories Practice
                item {
                    Text(
                        text = "PREPARED PRACTICE MODULES",
                        style = MaterialTheme.typography.labelMedium,
                        color = mutedTextColor,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val categoriesList = listOf(
                    "CONSTITUTIONAL LAW" to IosIcons.Book,
                    "CRIMINAL LAW & PROCEDURE" to IosIcons.ScalesOfJustice,
                    "PROPERTY & LAND LAW" to IosIcons.Home,
                    "EVIDENCE & COURT PROCEDURE" to IosIcons.Info,
                    "DEVOLUTION & PUBLIC FINANCE" to IosIcons.Star
                )

                items(categoriesList) { (catName, catIcon) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.startLocalCategoryQuiz(catName) },
                        colors = CardDefaults.cardColors(containerColor = cardBgc),
                        border = BorderStroke(1.dp, cardBdrColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(if (isDark) PureBlack else PureWhite, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(catIcon, contentDescription = null, tint = GoldPremium, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(
                                        text = catName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Assess statutory insights on this topic",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = secondaryTextColor
                                    )
                                }
                            }

                            Icon(
                                imageVector = IosIcons.ChevronRight,
                                contentDescription = "Start Quiz",
                                tint = GoldPremium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- PROFILE SCREEN WITH DETAILED SETTINGS, TONES, BOOKMARKS & PROGRESS ---
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val statutes by viewModel.statutes.collectAsStateWithLifecycle()

    val alarm1Enabled by viewModel.alarm1Enabled.collectAsStateWithLifecycle()
    val alarm1Hour by viewModel.alarm1Hour.collectAsStateWithLifecycle()
    val alarm1Minute by viewModel.alarm1Minute.collectAsStateWithLifecycle()

    val alarm2Enabled by viewModel.alarm2Enabled.collectAsStateWithLifecycle()
    val alarm2Hour by viewModel.alarm2Hour.collectAsStateWithLifecycle()
    val alarm2Minute by viewModel.alarm2Minute.collectAsStateWithLifecycle()

    val alarm3Enabled by viewModel.alarm3Enabled.collectAsStateWithLifecycle()
    val alarm3Hour by viewModel.alarm3Hour.collectAsStateWithLifecycle()
    val alarm3Minute by viewModel.alarm3Minute.collectAsStateWithLifecycle()

    val reminderSound by viewModel.reminderSoundType.collectAsStateWithLifecycle()
    val activeSoundFile by viewModel.selectedToneFile.collectAsStateWithLifecycle()

    val textColor = if (isDark) OffWhite else PureBlack
    val secondaryTextColor = if (isDark) Gray300 else Gray800
    val mutedTextColor = if (isDark) Gray600 else Color(0xFF707074)
    val cardBgc = if (isDark) Color(0xFF1C1C1E) else PureWhite
    val cardBdrColor = if (isDark) GlassBorder else Gray300
    val glassBgc = if (isDark) GlassBackground else Gray100

    // Profile Details Editing Details
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(userStats.name) }

    val context = LocalContext.current

    var hasOverlayPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.provider.Settings.canDrawOverlays(context)
            } else {
                true
            }
        )
    }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    android.provider.Settings.canDrawOverlays(context)
                } else {
                    true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. HEADER SECTION & EDIT PROFILE DETAILS CARD
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBgc),
                border = BorderStroke(1.dp, cardBdrColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarUrl = remember(userStats.name) {
                            "https://api.dicebear.com/7.x/lorelei/svg?seed=${android.net.Uri.encode(userStats.name)}"
                        }
                        coil.compose.AsyncImage(
                            model = avatarUrl,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, GoldPremium, CircleShape)
                                .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STUDENT PROFILE",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldPremium,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isEditingName) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        textStyle = MaterialTheme.typography.titleLarge.copy(color = textColor),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GoldPremium,
                                            unfocusedBorderColor = cardBdrColor,
                                            focusedTextColor = textColor
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            if (nameInput.trim().isNotEmpty()) {
                                                viewModel.updateUsername(nameInput.trim())
                                            }
                                            isEditingName = false
                                        },
                                        modifier = Modifier.background(GoldPremium, CircleShape).size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = IosIcons.Check,
                                            contentDescription = "Save username",
                                            tint = PureBlack,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.clickable {
                                        nameInput = userStats.name
                                        isEditingName = true
                                    }
                                ) {
                                    Text(
                                        text = userStats.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = IosIcons.Edit,
                                        contentDescription = "Edit name option",
                                        tint = GoldPremium,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Streak Display
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = mutedTextColor
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = IosIcons.Flame,
                                    contentDescription = "Flame Streak Icon",
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${userStats.streak} Days",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rank: ${userStats.rank} • XP: ${userStats.xp} Points",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = cardBdrColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. STUDY PATHWAY READING PROGRESS
                    val totalStatuted = statutes.size
                    val readStatuted = statutes.count { it.isRead }
                    val progressRatio = if (totalStatuted > 0) readStatuted.toFloat() / totalStatuted else 0f
                    val progressPercent = (progressRatio * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = IosIcons.TrendingUp,
                                contentDescription = "Progress",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "STATUTE STUDY PROGRESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = mutedTextColor
                            )
                        }
                        Text(
                            text = "$readStatuted / $totalStatuted Read ($progressPercent%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoldPremium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = GoldPremium,
                        trackColor = glassBgc
                    )
                }
            }
        }

        // 3. CORE PREFERENCES PANEL (THEME & OVERLAY)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBgc),
                border = BorderStroke(1.dp, cardBdrColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                imageVector = IosIcons.Moon,
                                contentDescription = "Dark Theme",
                                tint = GoldPremium,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "DARK THEME",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.toggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureBlack,
                                checkedTrackColor = GoldPremium,
                                uncheckedThumbColor = mutedTextColor,
                                uncheckedTrackColor = glassBgc
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = cardBdrColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    val context = LocalContext.current
                    var hasOverlayGranted by remember { 
                        mutableStateOf(
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                android.provider.Settings.canDrawOverlays(context)
                            } else {
                                true
                            }
                        )
                    }

                    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner) {
                        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                                hasOverlayGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    android.provider.Settings.canDrawOverlays(context)
                                } else {
                                    true
                                }
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    text = "DISPLAY OVER APPS",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                    text = "Show reminder banners on top of other apps.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = mutedTextColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (hasOverlayGranted) Color(0xFF34C759).copy(alpha = 0.15f) else GoldPremium.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (hasOverlayGranted) Color(0xFF34C759) else GoldPremium,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (!hasOverlayGranted && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        try {
                                            val intent = android.content.Intent(
                                                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                android.net.Uri.parse("package:" + context.packageName)
                                            )
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (hasOverlayGranted) "ACTIVE" else "CONFIGURE",
                                color = if (hasOverlayGranted) Color(0xFF34C759) else GoldPremium,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. CHRONO AUDIO STUDY ALARMS
        item {
            Text(
                text = "DAILY STUDY REMINDER",
                style = MaterialTheme.typography.labelSmall,
                color = mutedTextColor,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Primary alarm card
        item {
            AlarmSettingCard(
                alarmId = 1,
                title = "Article of the Day",
                subtitle = "Daily notifications on constitutional articles & rights",
                hour = alarm1Hour,
                minute = alarm1Minute,
                enabled = alarm1Enabled,
                onToggle = { viewModel.toggleAlarm(1, it) },
                viewModel = viewModel,
                textColor = textColor,
                mutedTextColor = mutedTextColor,
                glassBgc = glassBgc,
                cardBgc = cardBgc,
                cardBdrColor = cardBdrColor
            )
        }

        // Secondary alarm card
        item {
            AlarmSettingCard(
                alarmId = 2,
                title = "Case Law",
                subtitle = "Daily insights on leading judgments & judicial precedents",
                hour = alarm2Hour,
                minute = alarm2Minute,
                enabled = alarm2Enabled,
                onToggle = { viewModel.toggleAlarm(2, it) },
                viewModel = viewModel,
                textColor = textColor,
                mutedTextColor = mutedTextColor,
                glassBgc = glassBgc,
                cardBgc = cardBgc,
                cardBdrColor = cardBdrColor
            )
        }

        // Tertiary alarm card
        item {
            AlarmSettingCard(
                alarmId = 3,
                title = "Daily Quiz",
                subtitle = "Daily interactive mock question sets on the Constitution",
                hour = alarm3Hour,
                minute = alarm3Minute,
                enabled = alarm3Enabled,
                onToggle = { viewModel.toggleAlarm(3, it) },
                viewModel = viewModel,
                textColor = textColor,
                mutedTextColor = mutedTextColor,
                glassBgc = glassBgc,
                cardBgc = cardBgc,
                cardBdrColor = cardBdrColor
            )
        }

        // 5. NOTIFICATION SOUND AUDIO profile board
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBgc),
                border = BorderStroke(1.dp, cardBdrColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "NOTIFICATION TONE SELECTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPremium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a study chime. Tap to play sound.",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedTextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val audioOptions = listOf(
                        Pair("universfield-new-notification-03-323602.mp3", "Modern Chime"),
                        Pair("stu9-schl-bell-356847.mp3", "School Bell"),
                        Pair("mandakimdk-big-bellburmese-367944.mp3", "Burmese Big Bell"),
                        Pair("the_69-big-bell-330719.mp3", "Classic Bell v1"),
                        Pair("the_69-big-bell-330719_1.mp3", "Classic Bell v2"),
                        Pair("universfield-single-church-bell-2-352062.mp3", "Church Bell Accent")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        audioOptions.forEach { option ->
                            val toneFile = option.first
                            val toneName = option.second
                            val isSelected = activeSoundFile == toneFile

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) GoldPremium.copy(alpha = 0.12f) else glassBgc,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) GoldPremium else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.selectTone(toneFile, toneName)
                                        viewModel.playToneTest(toneFile)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) IosIcons.Bell else IosIcons.Close,
                                        contentDescription = "Speaker status icon",
                                        tint = if (isSelected) GoldPremium else mutedTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = toneName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) GoldPremium else cardBdrColor,
                                            CircleShape
                                        )
                                        .size(28.dp)
                                        .clickable {
                                            viewModel.playToneTest(toneFile)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = IosIcons.ArrowForward,
                                        contentDescription = "Play sound test preview",
                                        tint = if (isSelected) PureBlack else textColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.triggerTestNotification() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPremium),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = IosIcons.Bell,
                                contentDescription = "Test alarm broadcast",
                                tint = PureBlack,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FIRE TEST REMINDER BROADCAST",
                                color = PureBlack,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 6. BOOKMARKED ARTICLES PANEL
        val bookmarkedStatutes = statutes.filter { it.isBookmarked }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBgc),
                border = BorderStroke(1.dp, cardBdrColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "BOOKMARKED ARTICLES",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPremium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your bookmarked reference materials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedTextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (bookmarkedStatutes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(glassBgc, RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No bookmarks yet. Tap the bookmark icon while reading to save items.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = mutedTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            bookmarkedStatutes.forEach { statute ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(glassBgc, RoundedCornerShape(12.dp))
                                        .border(1.dp, cardBdrColor, RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.selectStatute(statute)
                                            viewModel.selectTab(AppTab.LIBRARY)
                                        }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = statute.sectionNumber,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GoldPremium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = statute.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = IosIcons.ChevronRight,
                                        contentDescription = "Read bookmarked article now",
                                        tint = GoldPremium,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmSettingCard(
    alarmId: Int,
    title: String,
    subtitle: String,
    hour: Int,
    minute: Int,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    viewModel: MainViewModel,
    textColor: Color,
    mutedTextColor: Color,
    glassBgc: Color,
    cardBgc: Color,
    cardBdrColor: Color
) {
    val context = LocalContext.current
    
    // Create the Native TimePickerDialog trigger for flexible input
    val timePickerDialog = remember(hour, minute) {
        android.app.TimePickerDialog(
            context,
            { _, pickedHour, pickedMinute ->
                viewModel.updateAlarmTime(alarmId, pickedHour, pickedMinute)
            },
            hour,
            minute,
            false // 12-hour selection
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBgc),
        border = BorderStroke(1.dp, cardBdrColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = mutedTextColor
                    )
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PureBlack,
                        checkedTrackColor = GoldPremium,
                        uncheckedThumbColor = mutedTextColor,
                        uncheckedTrackColor = glassBgc
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time displaying with click trigger for dial picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(glassBgc, RoundedCornerShape(12.dp))
                    .border(1.dp, cardBdrColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = enabled) {
                        timePickerDialog.show()
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = IosIcons.Bell,
                        contentDescription = "Clock schedule icon",
                        tint = if (enabled) GoldPremium else mutedTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Alarm Schedule Time",
                            style = MaterialTheme.typography.labelSmall,
                            color = mutedTextColor
                        )
                        Text(
                            text = String.format("%02d : %02d %s",
                                if (hour == 0 || hour == 12) 12 else hour % 12,
                                minute,
                                if (hour >= 12) "PM" else "AM"
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (enabled) GoldPremium else mutedTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (enabled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Tap to Change",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldPremium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = IosIcons.ChevronRight,
                            contentDescription = "Edit alarm time Dial",
                            tint = GoldPremium,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressTrackRow(label: String, ratio: Float, ratioText: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = OffWhite, style = MaterialTheme.typography.bodyMedium)
            Text(text = ratioText, color = GoldPremium, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = GoldPremium,
            trackColor = DividerColor
        )
    }
}

@Composable
fun MilestoneItem(title: String, completed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray900, RoundedCornerShape(10.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (completed) IosIcons.CheckCircle else IosIcons.FavoriteBorder,
            contentDescription = null,
            tint = if (completed) GoldPremium else Gray600,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (completed) OffWhite else Gray600
        )
    }
}

// --- QUIZ ASSESSMENT VIEWS ---
@Composable
fun QuizView(viewModel: MainViewModel, quizState: ActiveQuizState?) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val qBgc = if (isDark) PureBlack else OffWhite
    val qTextColor = if (isDark) OffWhite else PureBlack
    val qSecondaryTextColor = if (isDark) Gray300 else Gray800
    val qMutedTextColor = if (isDark) Gray600 else Color(0xFF707074)
    val qCardBgc = if (isDark) Color(0xFF161513) else PureWhite
    val qCardBdrColor = if (isDark) GlassBorder else Gray300

    if (quizState == null) {
        // Modern AI Quiz loading empty state animation as requested!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(qBgc)
                .testTag("quiz_loading_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                WiggyTheJudge(
                    expression = MascotExpr.THINKING,
                    modifier = Modifier.size(130.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                CircularProgressIndicator(
                    color = GoldPremium,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "DRAFTING TRIAL BRIEFS...",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPremium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Compiling customized scenario questions from live statutes. Please hold counsel...",
                    color = qSecondaryTextColor,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        return
    }

    val totalCount = quizState.questions.size
    val currentIdx = quizState.currentIndex
    val currentQuestion = quizState.questions.getOrNull(currentIdx) ?: return
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalCount > 0) (currentIdx + 1).toFloat() / totalCount.toFloat() else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "quiz_progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(qBgc)
            .testTag("quiz_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.closeQuiz() }) {
                    Icon(IosIcons.Close, contentDescription = "Close", tint = GoldPremium)
                }

                Text(
                    text = quizState.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldPremium,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "${currentIdx + 1} of $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                color = qMutedTextColor,
                fontWeight = FontWeight.Bold
            )
        }

        // Top progress bar
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = GoldPremium,
            trackColor = if (isDark) DividerColor else Color(0xFFE5E5EA)
        )

        if (quizState.isFinished) {
            val aiEvaluationText by viewModel.aiEvaluationText.collectAsStateWithLifecycle()
            val isAiEvaluationLoading by viewModel.isAiEvaluationLoading.collectAsStateWithLifecycle()

            // Trigger AI evaluation report automatically when finished
            LaunchedEffect(quizState.isFinished) {
                if (aiEvaluationText == null && !isAiEvaluationLoading) {
                    viewModel.evaluateUserPerformanceWithAI()
                }
            }

            // Game mechanics for filling completion / performance marks circle (Duolingo Vibe)
            val percentage = if (totalCount > 0) quizState.score.toFloat() / totalCount.toFloat() else 0f
            val animatedPercentage by androidx.compose.animation.core.animateFloatAsState(
                targetValue = percentage,
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                label = "PerformanceCircleProgress"
            )

            var lastTickedVal by remember { mutableStateOf(0) }
            LaunchedEffect(animatedPercentage) {
                val tickIndex = (animatedPercentage * 20).toInt()
                if (tickIndex > lastTickedVal && animatedPercentage < percentage) {
                    lastTickedVal = tickIndex
                    SoundVibrationHelper.tick(context)
                }
            }

            // Game mechanics for accumulating / compiling XP points (Duolingo Vibe)
            val targetXp = quizState.score * 20
            var animatedXp by remember { mutableStateOf(0) }
            LaunchedEffect(quizState.isFinished) {
                if (targetXp > 0) {
                    kotlinx.coroutines.delay(1600)
                    val stepDelay = (1100 / targetXp).coerceIn(15, 85).toLong()
                    for (xp in 1..targetXp) {
                        animatedXp = xp
                        SoundVibrationHelper.xpTick(context, xp, targetXp)
                        kotlinx.coroutines.delay(stepDelay)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Elegant Duolingo style mascot celebrating
                    WiggyTheJudge(
                        expression = MascotExpr.CELEBRATING,
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Module Completed!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = qTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Evaluation score circle with marks inside as requested!
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedPercentage },
                            modifier = Modifier.fillMaxSize(),
                            color = if (percentage >= 0.8f) Color(0xFF34C759) else if (percentage >= 0.5f) GoldPremium else Color(0xFFFF3B30),
                            strokeWidth = 10.dp,
                            trackColor = if (isDark) DividerColor else Color(0xFFD1D1D6)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${quizState.score}/$totalCount",
                                style = MaterialTheme.typography.headlineLarge,
                                color = qTextColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "MARKS",
                                style = MaterialTheme.typography.labelSmall,
                                color = qMutedTextColor,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (targetXp > 0) "You earned +$animatedXp XP!" else "No XP earned - Keep studying!",
                        color = GoldPremium,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // FAILED QUESTIONS & GUIDANCE ACTION CONTAINER
                    val failedQuestions = quizState.questions.filterIndexed { index, _ ->
                        val userSel = quizState.userSelections.getOrNull(index)
                        userSel != null && userSel != quizState.questions[index].correctIndex
                    }
                    if (failedQuestions.isNotEmpty()) {
                        Text(
                            text = "FAILED QUESTIONS & GUIDANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF3B30),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        failedQuestions.forEachIndexed { fIdx, failed ->
                            val origIdx = quizState.questions.indexOf(failed)
                            val userOption = quizState.userSelections.getOrNull(origIdx)
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF221114) else Color(0xFFFFF5F5)),
                                border = BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "${failed.question}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = qTextColor
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (userOption != null) {
                                        Text(
                                            text = "Your Answer: ${failed.options.getOrNull(userOption) ?: "None"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFF3B30),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "Correct Answer: ${failed.options.getOrNull(failed.correctIndex) ?: "None"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF34C759),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isDark) Color(0xFF161513) else Color.White, RoundedCornerShape(8.dp))
                                            .border(1.dp, qCardBdrColor, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Guidance: ${failed.explanation}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = qSecondaryTextColor,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = qCardBgc),
                        border = BorderStroke(1.dp, qCardBdrColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = IosIcons.Sparkles,
                                    contentDescription = null,
                                    tint = GoldPremium,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "PERFORMANCE COACH INSIGHTS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldPremium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isAiEvaluationLoading) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    LinearProgressIndicator(
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = GoldPremium,
                                        trackColor = if (isDark) DividerColor else Color(0xFFE5E5EA)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Formulating personalized review...",
                                        color = qMutedTextColor,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else if (aiEvaluationText != null) {
                                Text(
                                    text = parseMarkdownToAnnotatedString(aiEvaluationText!!, isDark),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = qTextColor
                                )
                            } else {
                                Text(
                                    text = "Analyzing your answer patterns and legal accuracy...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = qSecondaryTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.closeQuiz() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("quiz_finish_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPremium, contentColor = PureBlack)
                    ) {
                        Text("Complete Challenge", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. WIGGY COACH SPEECH SCAFFOLD
                    val mascotSpeechText = when {
                        quizState.showAnswerResult -> {
                            val isCorrect = quizState.selectedOptionIndex == currentQuestion.correctIndex
                            if (isCorrect) {
                                "Correct citation! Well done! 🏆"
                            } else {
                                "Let's review the explanation below."
                            }
                        }
                        quizState.selectedOptionIndex != null -> {
                            "Ready to submit your answer? 📜"
                        }
                        else -> {
                            listOf(
                                "Read options carefully before selecting! ⚖️",
                                "Pay close attention to constitutional details!",
                                "Analyze carefully, then proceed! 🦉"
                            )[currentIdx % 3]
                        }
                    }

                    val mascotExpression = when {
                        quizState.showAnswerResult -> {
                            val isCorrect = quizState.selectedOptionIndex == currentQuestion.correctIndex
                            if (isCorrect) MascotExpr.CORRECT else MascotExpr.INCORRECT
                        }
                        quizState.selectedOptionIndex != null -> MascotExpr.THINKING
                        else -> MascotExpr.IDLE
                    }

                    LawMascotSpeechBubble(
                        text = mascotSpeechText,
                        expression = mascotExpression,
                        isDark = isDark
                    )

                    // 2. SCENARIO / CONTEXT STATEMENTS
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleMedium,
                        color = qTextColor,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // 3. DUOLINGO OPTIONS CARDS
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 120.dp)
                    ) {
                        currentQuestion.options.forEachIndexed { optIdx, choiceText ->
                            val isSelected = quizState.selectedOptionIndex == optIdx
                            val isCorrectOption = optIdx == currentQuestion.correctIndex
                            val showResult = quizState.showAnswerResult

                            DuolingoOptionCard(
                                isSelected = isSelected,
                                showResult = showResult,
                                isCorrectOption = isCorrectOption,
                                onClick = {
                                    if (!showResult) {
                                        viewModel.selectQuizOption(optIdx)
                                    }
                                },
                                isDark = isDark,
                                modifier = Modifier.testTag("quiz_option_$optIdx")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = choiceText,
                                        color = if (showResult && isCorrectOption) Color(0xFF34C759) else qTextColor,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected || (showResult && isCorrectOption)) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (showResult && isCorrectOption) {
                                        Icon(IosIcons.Check, contentDescription = "Correct", tint = Color(0xFF34C759))
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. ACTION BAR / DUAL STATE BUTTON PANEL
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    if (!quizState.showAnswerResult) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, qBgc.copy(alpha = 0.95f), qBgc)
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Button(
                                onClick = {
                                    val q = quizState.questions.getOrNull(quizState.currentIndex)
                                    if (q != null) {
                                        val isCorrect = quizState.selectedOptionIndex == q.correctIndex
                                        if (isCorrect) {
                                            SoundVibrationHelper.playCorrect(context)
                                        } else {
                                            SoundVibrationHelper.playWrong(context)
                                        }
                                    }
                                    viewModel.submitQuizAnswer()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("quiz_submit_button"),
                                enabled = quizState.selectedOptionIndex != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPremium,
                                    contentColor = PureBlack,
                                    disabledContainerColor = if (isDark) Gray800 else Color(0xFFE5E5EA),
                                    disabledContentColor = qMutedTextColor
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Submit Answer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 5. CELEBRATORY SLIDEOUT SHEET
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = quizState.showAnswerResult,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        val isCorrect = quizState.selectedOptionIndex == currentQuestion.correctIndex
                        val bannerBgc = if (isCorrect) Color(0xFF1C3A27) else Color(0xFF3D1F21)
                        val accentCol = if (isCorrect) Color(0xFF34C759) else Color(0xFFFF3B30)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    bannerBgc,
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .border(
                                    1.dp,
                                    accentCol.copy(alpha = 0.4f),
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) IosIcons.Check else IosIcons.Close,
                                    contentDescription = null,
                                    tint = accentCol,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = if (isCorrect) "VERDICT CORRECT" else "OBJECTION SUSTAINED",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = accentCol,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 140.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "EXPLANATORY BRIEF",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accentCol,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    color = OffWhite,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.proceedQuizNext() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("quiz_next_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentCol,
                                    contentColor = PureWhite
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                val isFinalQuestion = currentIdx + 1 == totalCount
                                Text(
                                    text = if (isFinalQuestion) "Complete Module" else "Continue Trial",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- GLASSMORPHIC NAVIGATION BAR COMPONENTS ---
@Composable
fun LawDeskBottomNavigation(
    currentTab: AppTab,
    isDark: Boolean,
    onTabSelected: (AppTab) -> Unit
) {
    val navBgc = if (isDark) Color(0xCC1C1C1E) else Color(0xD9FFFFFF)
    val bdrColor = if (isDark) Color(0x22FFFFFF) else Color(0x1F000000)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .background(navBgc, RoundedCornerShape(24.dp))
                .border(1.dp, bdrColor, RoundedCornerShape(24.dp))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTabItem(
                tabName = "Home",
                icon = if (currentTab == AppTab.HOME) IosIcons.HomeFilled else IosIcons.Home,
                selected = currentTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) }
            )

            NavigationTabItem(
                tabName = "Library",
                icon = IosIcons.BookOpen,
                selected = currentTab == AppTab.LIBRARY,
                onClick = { onTabSelected(AppTab.LIBRARY) }
            )

            NavigationTabItem(
                tabName = "Companion",
                icon = IosIcons.Scale,
                selected = currentTab == AppTab.AI,
                onClick = { onTabSelected(AppTab.AI) }
            )

            NavigationTabItem(
                tabName = "Profile",
                icon = IosIcons.User,
                selected = currentTab == AppTab.PROFILE,
                onClick = { onTabSelected(AppTab.PROFILE) }
            )
        }
    }
}

@Composable
fun RowScope.NavigationTabItem(
    tabName: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("tab_${tabName.lowercase().replace(" ", "_")}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tabName,
            tint = if (selected) GoldPremium else Gray650Color(),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tabName,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) GoldPremium else Gray650Color(),
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.height(3.dp))
        // Subtle gold dot indicating the selected tab
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(if (selected) GoldPremium else Color.Transparent, CircleShape)
        )
    }
}

fun Gray650Color(): Color = Color(0xFF707074)

@Composable
fun NotificationBannerCard(
    viewModel: MainViewModel,
    isDark: Boolean
) {
    val title by viewModel.notificationBannerTitle.collectAsStateWithLifecycle()
    val body by viewModel.notificationBannerBody.collectAsStateWithLifecycle()
    
    val cardBgc = if (isDark) Color(0xFF0C0C0E) else PureWhite
    val textColor = if (isDark) OffWhite else PureBlack
    val accentColor = if (isDark) Color(0xFF007AFF) else Color(0xFF0056B3)
    val bdrColor = accentColor
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Safely position below status bar
            .padding(14.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(cardBgc)
            .border(2.dp, bdrColor, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .testTag("real_notification_banner")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = IosIcons.Bell,
                        contentDescription = "Notification icon",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "LAWDESK DAILY COMPANION",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
                
                IconButton(
                    onClick = { viewModel.dismissNotificationBanner() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = IosIcons.Close,
                        contentDescription = "Dismiss reminder",
                        tint = if (isDark) Gray600 else Color(0xFF707074),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Gray300 else Gray800,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DISMISS BUTTON
                TextButton(
                    onClick = { viewModel.dismissNotificationBanner() },
                    modifier = Modifier.testTag("banner_dismiss_button")
                ) {
                    Text(
                        text = "DISMISS",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isDark) Gray300 else Gray600,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // OPEN STUDY BUTTON
                Button(
                    onClick = {
                        viewModel.selectStatuteFromNotification(title, body)
                        viewModel.dismissNotificationBanner()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = if (isDark) PureBlack else PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("banner_open_study_button")
                ) {
                    Text(
                        text = "OPEN STUDY",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- DUOLINGO STYLE GAMIFICATION AND INTERACTIVE COMPONENTS ---

enum class MascotExpr {
    IDLE,
    THINKING,
    CORRECT,
    INCORRECT,
    CELEBRATING
}

@Composable
fun WiggyTheJudge(
    expression: MascotExpr,
    modifier: Modifier = Modifier
) {
    var blinkState by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((2500..6000).random().toLong())
            blinkState = true
            delay(120)
            blinkState = false
        }
    }

    val transition = rememberInfiniteTransition(label = "wiggy_bounce")
    val bounceY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Canvas(
        modifier = modifier
            .offset(y = bounceY.dp)
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // 1. JUDICIAL BLACK ROBE / COLLAR
        val robePath = Path().apply {
            moveTo(cx - 24.dp.toPx(), h)
            cubicTo(
                cx - 16.dp.toPx(), h - 28.dp.toPx(),
                cx + 16.dp.toPx(), h - 28.dp.toPx(),
                cx + 24.dp.toPx(), h
            )
            close()
        }
        drawPath(robePath, Color(0xFF1E1E1F))

        // White Jabot/Collar bands
        drawRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(cx - 6.dp.toPx(), h - 18.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(4.dp.toPx(), 10.dp.toPx())
        )
        drawRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(cx + 2.dp.toPx(), h - 18.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(4.dp.toPx(), 10.dp.toPx())
        )

        // 2. THE FACE (Slightly oval shaped owl face)
        val faceRadius = 26.dp.toPx()
        val faceY = cy - 2.dp.toPx()
        drawCircle(
            color = Color(0xFFB3E5FC), // Light blue owl skin
            radius = faceRadius,
            center = androidx.compose.ui.geometry.Offset(cx, faceY)
        )
        // Face shield
        drawCircle(
            color = Color.White,
            radius = faceRadius * 0.85f,
            center = androidx.compose.ui.geometry.Offset(cx, faceY)
        )

        // 3. THE WIG (Judicial White curly wig layers)
        // Left curls
        drawCircle(Color(0xFFE5E5EA), radius = 8.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx - 22.dp.toPx(), faceY - 12.dp.toPx()))
        drawCircle(Color(0xFFE5E5EA), radius = 10.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx - 25.dp.toPx(), faceY + 1.dp.toPx()))
        drawCircle(Color(0xFFD1D1D6), radius = 9.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx - 24.dp.toPx(), faceY + 13.dp.toPx()))

        // Right curls
        drawCircle(Color(0xFFE5E5EA), radius = 8.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx + 22.dp.toPx(), faceY - 12.dp.toPx()))
        drawCircle(Color(0xFFE5E5EA), radius = 10.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx + 25.dp.toPx(), faceY + 1.dp.toPx()))
        drawCircle(Color(0xFFD1D1D6), radius = 9.dp.toPx(), center = androidx.compose.ui.geometry.Offset(cx + 24.dp.toPx(), faceY + 13.dp.toPx()))

        // Top wig cap
        val wigPath = Path().apply {
            moveTo(cx - 20.dp.toPx(), faceY - 16.dp.toPx())
            cubicTo(
                cx - 12.dp.toPx(), faceY - 32.dp.toPx(),
                cx + 12.dp.toPx(), faceY - 32.dp.toPx(),
                cx + 20.dp.toPx(), faceY - 16.dp.toPx()
            )
            close()
        }
        drawPath(wigPath, Color(0xFFF2F2F7))

        // 4. EYES
        val eyeSpacing = 9.dp.toPx()
        val eyeSize = 6.dp.toPx()
        val eyeL_cx = cx - eyeSpacing
        val eyeR_cx = cx + eyeSpacing
        val eye_cy = faceY - 4.dp.toPx()

        when (expression) {
            MascotExpr.CORRECT, MascotExpr.CELEBRATING -> {
                // Cheerful closed eye arcs
                val happyPathL = Path().apply {
                    moveTo(eyeL_cx - 5.dp.toPx(), eye_cy)
                    quadraticTo(eyeL_cx, eye_cy - 4.dp.toPx(), eyeL_cx + 5.dp.toPx(), eye_cy)
                }
                val happyPathR = Path().apply {
                    moveTo(eyeR_cx - 5.dp.toPx(), eye_cy)
                    quadraticTo(eyeR_cx, eye_cy - 4.dp.toPx(), eyeR_cx + 5.dp.toPx(), eye_cy)
                }
                drawPath(happyPathL, Color(0xFF34C759), style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round))
                drawPath(happyPathR, Color(0xFF34C759), style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round))
                
                // Rosy cheeks
                drawCircle(Color(0xFFFF8A80).copy(alpha = 0.7f), radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeL_cx - 3.dp.toPx(), eye_cy + 6.dp.toPx()))
                drawCircle(Color(0xFFFF8A80).copy(alpha = 0.7f), radius = 4.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeR_cx + 3.dp.toPx(), eye_cy + 6.dp.toPx()))
            }
            MascotExpr.INCORRECT -> {
                // Slanted sad eyes
                drawCircle(Color.White, radius = 5.5f.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeL_cx, eye_cy))
                drawCircle(Color.White, radius = 5.5f.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeR_cx, eye_cy))
                drawCircle(Color(0xFF3A3A3C), radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeL_cx, eye_cy))
                drawCircle(Color(0xFF3A3A3C), radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeR_cx, eye_cy))

                // Sweat drop
                val dropPath = Path().apply {
                    moveTo(cx + 14.dp.toPx(), eye_cy - 10.dp.toPx())
                    quadraticTo(cx + 11.dp.toPx(), eye_cy, cx + 14.dp.toPx(), eye_cy + 3.dp.toPx())
                    quadraticTo(cx + 17.dp.toPx(), eye_cy, cx + 14.dp.toPx(), eye_cy - 10.dp.toPx())
                }
                drawPath(dropPath, Color(0xFF007AFF))
            }
            MascotExpr.THINKING -> {
                // Wide thinking eyes
                drawCircle(Color(0xFF1C1C1E), radius = eyeSize, center = androidx.compose.ui.geometry.Offset(eyeL_cx, eye_cy))
                drawCircle(Color(0xFF1C1C1E), radius = eyeSize, center = androidx.compose.ui.geometry.Offset(eyeR_cx, eye_cy))
                drawCircle(Color.White, radius = 1.8f.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeL_cx - 1.5f.dp.toPx(), eye_cy - 1.5f.dp.toPx()))
                drawCircle(Color.White, radius = 1.8f.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeR_cx - 1.5f.dp.toPx(), eye_cy - 1.5f.dp.toPx()))

                // Slanted thinking eyebrows
                drawLine(Color(0xFF3E2723), start = androidx.compose.ui.geometry.Offset(eyeL_cx - 5.dp.toPx(), eye_cy - 7.dp.toPx()), end = androidx.compose.ui.geometry.Offset(eyeL_cx + 3.dp.toPx(), eye_cy - 5.dp.toPx()), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(Color(0xFF3E2723), start = androidx.compose.ui.geometry.Offset(eyeR_cx - 3.dp.toPx(), eye_cy - 5.dp.toPx()), end = androidx.compose.ui.geometry.Offset(eyeR_cx + 5.dp.toPx(), eye_cy - 8.dp.toPx()), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
            }
            else -> {
                // IDLE
                if (blinkState) {
                    drawLine(Color(0xFF1C1C1E), start = androidx.compose.ui.geometry.Offset(eyeL_cx - 4.dp.toPx(), eye_cy), end = androidx.compose.ui.geometry.Offset(eyeL_cx + 4.dp.toPx(), eye_cy), strokeWidth = 2.5f.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(Color(0xFF1C1C1E), start = androidx.compose.ui.geometry.Offset(eyeR_cx - 4.dp.toPx(), eye_cy), end = androidx.compose.ui.geometry.Offset(eyeR_cx + 4.dp.toPx(), eye_cy), strokeWidth = 2.5f.dp.toPx(), cap = StrokeCap.Round)
                } else {
                    drawCircle(Color(0xFF1C1C1E), radius = eyeSize, center = androidx.compose.ui.geometry.Offset(eyeL_cx, eye_cy))
                    drawCircle(Color(0xFF1C1C1E), radius = eyeSize, center = androidx.compose.ui.geometry.Offset(eyeR_cx, eye_cy))
                    drawCircle(Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeL_cx - 1.5f.dp.toPx(), eye_cy - 1.5f.dp.toPx()))
                    drawCircle(Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(eyeR_cx - 1.5f.dp.toPx(), eye_cy - 1.5f.dp.toPx()))
                }
            }
        }

        // 5. BEAK / MOUTH
        val beakPath = Path().apply {
            moveTo(cx - 4.dp.toPx(), faceY + 1.dp.toPx())
            lineTo(cx + 4.dp.toPx(), faceY + 1.dp.toPx())
            when (expression) {
                MascotExpr.CORRECT, MascotExpr.CELEBRATING -> {
                    lineTo(cx, faceY + 9.dp.toPx())
                }
                MascotExpr.INCORRECT -> {
                    lineTo(cx, faceY + 7.dp.toPx())
                }
                else -> {
                    lineTo(cx, faceY + 6.dp.toPx())
                }
            }
            close()
        }
        drawPath(beakPath, Color(0xFFFF9500)) // Deep orange

        // Glasses rim
        val rimRadius = 10.dp.toPx()
        drawCircle(
            color = Color(0xFFFFCC00),
            radius = rimRadius,
            center = androidx.compose.ui.geometry.Offset(eyeL_cx, eye_cy),
            style = Stroke(width = 1.5f.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFFFCC00),
            radius = rimRadius,
            center = androidx.compose.ui.geometry.Offset(eyeR_cx, eye_cy),
            style = Stroke(width = 1.5f.dp.toPx())
        )
        drawLine(
            color = Color(0xFFFFCC00),
            start = androidx.compose.ui.geometry.Offset(eyeL_cx + rimRadius, eye_cy),
            end = androidx.compose.ui.geometry.Offset(eyeR_cx - rimRadius, eye_cy),
            strokeWidth = 1.5f.dp.toPx()
        )
    }
}

@Composable
fun LawMascotSpeechBubble(
    text: String,
    expression: MascotExpr,
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WiggyTheJudge(
            expression = expression,
            modifier = Modifier.size(60.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                    RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                )
                .border(
                    1.dp,
                    if (isDark) Color(0x28FFFFFF) else Color(0x1F000000),
                    RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color(0xFFF2F2F7) else Color(0xFF1C1C1E),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DuolingoOptionCard(
    isSelected: Boolean,
    showResult: Boolean,
    isCorrectOption: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val translationY by animateFloatAsState(
        targetValue = if (isSelected) 3f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "depress"
    )

    val baseBorderColor = when {
        showResult && isCorrectOption -> Color(0xFF34C759)
        showResult && isSelected && !isCorrectOption -> Color(0xFFFF3B30)
        isSelected -> GoldPremium
        else -> if (isDark) Color(0x14FFFFFF) else Color(0xFFD1D1D6)
    }

    val bottomBorderColor = when {
        showResult && isCorrectOption -> Color(0xFF248A3D)
        showResult && isSelected && !isCorrectOption -> Color(0xFFB5231B)
        isSelected -> GoldDark
        else -> if (isDark) Color(0x0AFFFFFF) else Color(0xFFB5B5B9)
    }

    val containerColor = when {
        showResult && isCorrectOption -> Color(0xFF34C759).copy(alpha = 0.12f)
        showResult && isSelected && !isCorrectOption -> Color(0xFFFF3B30).copy(alpha = 0.12f)
        isSelected -> GoldPremium.copy(alpha = 0.1f)
        else -> if (isDark) Color(0xFF1C1C1E) else Color.White
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.translationY = translationY.dp.toPx()
            }
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .background(bottomBorderColor, RoundedCornerShape(14.dp))
            .padding(bottom = if (isSelected) 1.dp else 4.dp)
            .background(containerColor, RoundedCornerShape(14.dp))
            .border(1.dp, baseBorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
fun FallingConfetti(isActive: Boolean) {
    if (!isActive) return
    val list = remember {
        List(70) {
            ConfettiParticle(
                x = (0..1000).random().toFloat() / 1000f,
                y = -(0..1000).random().toFloat() / 400f,
                speed = 0.008f + (0..100).random().toFloat() / 4000f,
                color = listOf(
                    GoldPremium,
                    Color(0xFF34C759),
                    Color(0xFF5856D6),
                    Color(0xFFFF2D55),
                    Color(0xFFFF9500),
                    Color(0xFF30B0C7)
                ).random(),
                rotation = (0..360).random().toFloat(),
                rotationSpeed = (-4..4).random().toFloat(),
                shape = (0..2).random(),
                swayAmplitude = 0.03f + (0..100).random().toFloat() / 1200f,
                swaySpeed = 0.02f + (0..100).random().toFloat() / 1500f
            )
        }
    }

    var time by remember { mutableStateOf(0L) }
    LaunchedEffect(isActive) {
        while (isActive) {
            withFrameMillis { frameTime ->
                time = frameTime
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        list.forEach { particle ->
            particle.y += particle.speed
            if (particle.y > 1.1f) {
                particle.y = -0.1f
                particle.x = (0..1000).random().toFloat() / 1000f
            }
            particle.rotation += particle.rotationSpeed
            val sway = particle.swayAmplitude * kotlin.math.sin(particle.y * 10f + particle.swaySpeed * time.toFloat() / 80f)
            val px = (particle.x + sway).coerceIn(0f, 1f) * w
            val py = particle.y * h

            drawContext.canvas.save()
            drawContext.canvas.translate(px, py)
            drawContext.canvas.rotate(particle.rotation)

            val pSize = 10.dp.toPx()
            when (particle.shape) {
                0 -> {
                    drawRect(
                        color = particle.color,
                        size = androidx.compose.ui.geometry.Size(pSize, pSize / 2f)
                    )
                }
                1 -> {
                    drawCircle(
                        color = particle.color,
                        radius = pSize / 2f
                    )
                }
                else -> {
                    val path = Path().apply {
                        moveTo(0f, -pSize / 2f)
                        lineTo(pSize / 2f, pSize / 2f)
                        lineTo(-pSize / 2f, pSize / 2f)
                        close()
                    }
                    drawPath(path = path, color = particle.color)
                }
            }
            drawContext.canvas.restore()
        }
    }
}

class ConfettiParticle(
    var x: Float,
    var y: Float,
    val speed: Float,
    val color: Color,
    var rotation: Float,
    val rotationSpeed: Float,
    val shape: Int,
    val swayAmplitude: Float,
    val swaySpeed: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaximsBottomSheet(
    maxims: List<LegalMaxim>,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filteredMaxims = remember(maxims, query) {
        if (query.isBlank()) maxims else maxims.filter {
            it.latin.contains(query, ignoreCase = true) ||
            it.translation.contains(query, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF0F2B5B),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LEGAL MAXIMS DICTIONARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF93C5FD),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Principles of Jurisprudence (${filteredMaxims.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = IosIcons.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                placeholder = { Text("Search Latin maxims or English terms...", color = Color(0xFF93C5FD).copy(alpha = 0.7f)) },
                leadingIcon = { Icon(IosIcons.Search, contentDescription = null, tint = Color(0xFF93C5FD)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E3A8A).copy(alpha = 0.5f),
                    unfocusedContainerColor = Color(0xFF1E3A8A).copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredMaxims, key = { it.latin }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A).copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.latin,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.translation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE0F2FE),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
