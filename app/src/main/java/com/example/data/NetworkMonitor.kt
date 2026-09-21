package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import retrofit2.HttpException
import java.io.IOException

class NoConnectivityException(message: String = "No dynamic internet connection detected. Please check your network connection and retry.") : IOException(message)

class GeminiApiException(val code: Int, val rawMessage: String) : IOException(
    when (code) {
        404 -> "[HTTP 404 Content Not Found Error]\nThe selected Gemini AI model or resource identifier is not recognized or has been deprecated. Please verify your requested endpoint."
        503 -> "[HTTP 503 Service Temporarily Unavailable Alert]\nThe Gemini AI cloud servers are currently overloaded, undergoing maintenance, or experiencing transient downtime. Please try again in a few moments."
        else -> "[HTTP $code API Error]\nThe web services returned an unexpected server status. Details: $rawMessage"
    }
)

object NetworkMonitor {
    
    private var appContext: Context? = null

    /**
     * Initializes the application context reference for the Network Monitor.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Synchronously checks if the device is currently connected to the internet.
     */
    fun isConnected(): Boolean {
        val context = appContext ?: return true // Fallback to avoid false negatives if not initialized
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    /**
     * Verifies connectivity before executing a network request, other wise throws NoConnectivityException.
     */
    fun verifyConnectivity() {
        if (!isConnected()) {
            throw NoConnectivityException()
        }
    }

    /**
     * Helper to map network exceptions to user-friendly messages/alerts.
     */
    fun parseException(e: Throwable): String {
        return when (e) {
            is NoConnectivityException -> e.message ?: "Network offline."
            is GeminiApiException -> e.message ?: "API error."
            is HttpException -> {
                val code = e.code()
                val rawMsg = e.message() ?: ""
                GeminiApiException(code, rawMsg).message ?: "HTTP Status Code: $code"
            }
            is IOException -> "A connection failure occurred while communicating with LawDesk online services. Please check your connection stability."
            else -> e.localizedMessage ?: "An unexpected error occurred during your request."
        }
    }
}
