package com.example.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object SoundVibrationHelper {
    private const val TAG = "SoundVibrationHelper"
    private var toneGenerator: ToneGenerator? = null

    private fun getOrInitToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize ToneGenerator", e)
            }
        }
        return toneGenerator
    }

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to obtain Vibrator service", e)
            null
        }
    }

    /**
     * Light vibration and a correct answer chime (Duolingo Style)
     */
    fun playCorrect(context: Context) {
        // Correct Answer Vibration (Light pulse)
        val vibrator = getVibrator(context)
        if (vibrator != null && vibrator.hasVibrator()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(45, 160))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(45)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Vibrate correct failed", e)
            }
        }

        // Correct Answer Tone (Uplifting quick chords/beeps)
        try {
            getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_PROP_ACK, 220)
        } catch (e: Exception) {
            Log.e(TAG, "Play correct tone failed", e)
        }
    }

    /**
     * Long wrong answer buzz & double flat/alert beeps (Duolingo Style)
     */
    fun playWrong(context: Context) {
        // Wrong Answer Vibration (Long/strong double-pulse buzz)
        val vibrator = getVibrator(context)
        if (vibrator != null && vibrator.hasVibrator()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 220, 100, 220)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(550)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Vibrate wrong failed", e)
            }
        }

        // Wrong Answer Tone (Low buzz double flat pitch)
        try {
            getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_PROP_NACK, 350)
        } catch (e: Exception) {
            Log.e(TAG, "Play wrong tone failed", e)
        }
    }

    /**
     * Quick light tick haptic + soft click tone for continuous actions like loading/compiling
     */
    fun tick(context: Context) {
        val vibrator = getVibrator(context)
        if (vibrator != null && vibrator.hasVibrator()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(12, 100))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(12)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Vibrate tick failed", e)
            }
        }

        try {
            getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (e: Exception) {
            Log.e(TAG, "Play tick tone failed", e)
        }
    }

    /**
     * Satisfying ascending pitch and light pulses while XP counts up
     */
    fun xpTick(context: Context, currentVal: Int, maxVal: Int) {
        val vibrator = getVibrator(context)
        if (vibrator != null && vibrator.hasVibrator()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(20, 120))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(20)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Vibrate xpTick failed", e)
            }
        }

        try {
            // Generate delightful ascending musical tone based on progress
            val ratio = if (maxVal > 0) currentVal.toFloat() / maxVal.toFloat() else 0.5f
            val toneType = when {
                ratio < 0.15f -> ToneGenerator.TONE_DTMF_1
                ratio < 0.30f -> ToneGenerator.TONE_DTMF_2
                ratio < 0.45f -> ToneGenerator.TONE_DTMF_3
                ratio < 0.60f -> ToneGenerator.TONE_DTMF_4
                ratio < 0.75f -> ToneGenerator.TONE_DTMF_5
                ratio < 0.90f -> ToneGenerator.TONE_DTMF_6
                else -> ToneGenerator.TONE_DTMF_9
            }
            getOrInitToneGenerator()?.startTone(toneType, 40)
        } catch (e: Exception) {
            Log.e(TAG, "Play xpTick tone failed", e)
        }
    }
}
