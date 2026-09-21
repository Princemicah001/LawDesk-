package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApiLogEntry
import com.example.ui.theme.*

private val TerminalBg = Color(0xFF0D1117)
private val TerminalCardBg = Color(0xFF161B22)
private val TerminalBorder = Color(0xFF30363D)
private val TerminalGreen = Color(0xFF3FB950)
private val TerminalBlue = Color(0xFF58A6FF)
private val TerminalGold = Color(0xFFD29922)
private val TerminalRed = Color(0xFFF85149)
private val TerminalMuted = Color(0xFF8B949E)
private val TerminalText = Color(0xFFC9D1D9)

/**
 * Developer API Logs Terminal embedded in Search.
 * Displays real-time HTTP requests, cURL commands, latency, and raw JSON responses.
 */
@Composable
fun ApiLogsTerminalBanner(
    logs: List<ApiLogEntry>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingCount = logs.count { it.isPending }
    val latest = logs.firstOrNull()
    val isSuccess = latest?.statusCode in 200..299

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("api_logs_terminal_banner"),
        color = TerminalBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (pendingCount > 0) GoldPremium else (if (isExpanded) TerminalBlue else TerminalBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Indicator Dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = when {
                                pendingCount > 0 -> TerminalGold
                                isSuccess -> TerminalGreen
                                latest?.isError == true -> TerminalRed
                                else -> TerminalBlue
                            },
                            shape = CircleShape
                        )
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "API LOGS TERMINAL",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoldPremium,
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            color = TerminalCardBg,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, TerminalBorder)
                        ) {
                            Text(
                                text = "${logs.size} calls",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TerminalMuted,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (latest != null) {
                        Text(
                            text = "${latest.method} ${latest.url.substringBefore('?')} • ${latest.statusCode ?: "..."} (${latest.latencyMs ?: 0}ms)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (latest.isError) TerminalRed else TerminalMuted,
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = "Awaiting queries... type in search to see live requests",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TerminalMuted
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (logs.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = IosIcons.Delete,
                            contentDescription = "Clear Logs",
                            tint = TerminalMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Surface(
                    color = if (isExpanded) TerminalBlue.copy(alpha = 0.15f) else TerminalCardBg,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (isExpanded) TerminalBlue else TerminalBorder)
                ) {
                    Text(
                        text = if (isExpanded) "COLLAPSE" else "EXPAND TERMINAL",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpanded) TerminalBlue else TerminalText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ApiLogsTerminalConsole(
    logs: List<ApiLogEntry>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalBg, RoundedCornerShape(12.dp))
            .border(1.dp, TerminalBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("api_logs_terminal_console")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFF5F56), CircleShape))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFFBD2E), CircleShape))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF27C93F), CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "eLegal Live HTTP Request / Response Inspector",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TerminalMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$ Ready for live API requests.\nEnter keywords in the search bar above to trigger live queries.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TerminalMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    ApiLogItemCard(log = log, context = context)
                }
            }
        }
    }
}

@Composable
private fun ApiLogItemCard(log: ApiLogEntry, context: Context) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { isExpanded = !isExpanded },
        color = TerminalCardBg,
        border = BorderStroke(1.dp, if (log.isError) TerminalRed.copy(alpha = 0.5f) else TerminalBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Method badge
                    Surface(
                        color = when (log.method.uppercase()) {
                            "GET" -> TerminalBlue.copy(alpha = 0.2f)
                            "POST" -> TerminalGold.copy(alpha = 0.2f)
                            else -> TerminalGreen.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = log.method,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = when (log.method.uppercase()) {
                                "GET" -> TerminalBlue
                                "POST" -> TerminalGold
                                else -> TerminalGreen
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Status Code
                    if (log.isPending) {
                        Text(
                            text = "PENDING...",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = TerminalGold
                        )
                    } else {
                        val statusColor = when (log.statusCode) {
                            in 200..299 -> TerminalGreen
                            in 300..399 -> TerminalBlue
                            in 400..499 -> TerminalGold
                            else -> TerminalRed
                        }
                        Text(
                            text = "${log.statusCode ?: 0} ${log.statusMessage ?: ""}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = statusColor
                        )
                    }

                    if (log.latencyMs != null) {
                        Text(
                            text = "${log.latencyMs}ms",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TerminalMuted
                        )
                    }
                }

                Text(
                    text = log.timestamp,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TerminalMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // URL
            Text(
                text = log.url,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = TerminalText,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2
            )

            // Expanded view for cURL and full Response Body
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Copy cURL Command Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "cURL Command:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = GoldPremium,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("eLegal cURL", log.curlCommand))
                                Toast.makeText(context, "cURL command copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(
                                text = "COPY CURL",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = TerminalBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // cURL Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        color = TerminalBg,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, TerminalBorder)
                    ) {
                        Text(
                            text = log.curlCommand,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TerminalGreen,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Response JSON Box
                    Text(
                        text = "Response Payload (JSON):",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = GoldPremium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        color = TerminalBg,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, TerminalBorder)
                    ) {
                        Text(
                            text = log.responseBody ?: "[No response body]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (log.isError) TerminalRed else TerminalText,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
