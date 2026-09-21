package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

data class ApiLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val curlCommand: String,
    val statusCode: Int? = null,
    val statusMessage: String? = null,
    val latencyMs: Long? = null,
    val requestBody: String? = null,
    val responseBody: String? = null,
    val isPending: Boolean = false,
    val isError: Boolean = false,
    val summary: String? = null
)

object ApiLogManager {
    private val _logs = MutableStateFlow<List<ApiLogEntry>>(emptyList())
    val logs: StateFlow<List<ApiLogEntry>> = _logs.asStateFlow()

    private val _isTerminalExpanded = MutableStateFlow(false)
    val isTerminalExpanded: StateFlow<Boolean> = _isTerminalExpanded.asStateFlow()

    fun toggleTerminal() {
        _isTerminalExpanded.value = !_isTerminalExpanded.value
    }

    fun setTerminalExpanded(expanded: Boolean) {
        _isTerminalExpanded.value = expanded
    }

    fun logRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        requestBody: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        
        val headerCurl = headers.entries.joinToString(" ") { "-H \"${it.key}: ${it.value}\"" }
        val curl = if (method.equals("GET", ignoreCase = true)) {
            "curl -X GET \"$url\" $headerCurl"
        } else {
            "curl -X $method \"$url\" $headerCurl ${if (requestBody != null) "-d '$requestBody'" else ""}"
        }

        val entry = ApiLogEntry(
            id = id,
            timestamp = timeStr,
            method = method,
            url = url,
            headers = headers,
            curlCommand = curl,
            requestBody = requestBody,
            isPending = true,
            summary = "Dispatching $method to ${url.substringBefore('?')}"
        )

        val current = _logs.value.toMutableList()
        current.add(0, entry)
        if (current.size > 50) {
            _logs.value = current.take(50)
        } else {
            _logs.value = current
        }
        return id
    }

    fun logResponse(
        id: String,
        statusCode: Int,
        statusMessage: String,
        latencyMs: Long,
        responseBody: String?,
        isError: Boolean = false,
        summary: String? = null
    ) {
        val current = _logs.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = current[index]
            current[index] = old.copy(
                statusCode = statusCode,
                statusMessage = statusMessage,
                latencyMs = latencyMs,
                responseBody = responseBody,
                isPending = false,
                isError = isError || statusCode >= 400,
                summary = summary ?: "HTTP $statusCode $statusMessage (${latencyMs}ms)"
            )
            _logs.value = current
        }
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
