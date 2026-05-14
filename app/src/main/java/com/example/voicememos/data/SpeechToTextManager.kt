package com.example.voicememos.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class SpeechToTextManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onStatusChange: (RecordingState) -> Unit = {}
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening() {
        if (isListening) return

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Распознавание речи недоступно на этом устройстве")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
            it.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                    onStatusChange(RecordingState.Listening)
                }

                override fun onBeginningOfSpeech() {
                    onStatusChange(RecordingState.Processing)
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                    onStatusChange(RecordingState.Idle)
                }

                override fun onError(error: Int) {
                    isListening = false
                    onStatusChange(RecordingState.Idle)

                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Ошибка записи звука"
                        SpeechRecognizer.ERROR_CLIENT -> "Ошибка клиента"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения на микрофон"
                        SpeechRecognizer.ERROR_NETWORK -> "Ошибка сети"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Таймаут сети"
                        SpeechRecognizer.ERROR_NO_MATCH -> "Речь не распознана"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Распознавание уже запущено"
                        SpeechRecognizer.ERROR_SERVER -> "Ошибка сервера"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Речь не обнаружена"
                        else -> "Неизвестная ошибка: $error"
                    }
                    onError(message)
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    onStatusChange(RecordingState.Idle)

                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull()

                    if (!text.isNullOrBlank()) {
                        onResult(text)
                    } else {
                        onError("Текст не распознан")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите...")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // опционально: частичные результаты
        }

        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
        onStatusChange(RecordingState.Idle)
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    fun isListening(): Boolean = isListening
}

// Состояния записи для UI
sealed class RecordingState {
    object Idle : RecordingState()
    object Listening : RecordingState()   // ждём речь
    object Processing : RecordingState()  // обрабатываем
}