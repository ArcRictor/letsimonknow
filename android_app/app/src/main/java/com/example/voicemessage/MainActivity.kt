package com.example.voicemessage

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voicemessage.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastProcessedTimestamp: String? = null
    private val mediaPlayer = MediaPlayer()
    private val serverUrl = "http://10.0.2.2:5000" // Android emulator localhost
    private val elevenlabsApiKey = "sk_51ebbf9c10504370bc52b7a0e3ef6fd8f47c16f03810ee28"

    private val retrofit = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startMessageMonitoring()
    }

    private fun startMessageMonitoring() {
        lifecycleScope.launch {
            while (true) {
                try {
                    checkForNewMessages()
                    delay(5000) // Check every 5 seconds
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error checking messages", e)
                    binding.statusText.text = "Error: ${e.message}"
                }
            }
        }
    }

    private suspend fun checkForNewMessages() {
        val messages = api.getMessages()
        val newMessages = messages.filter { 
            it.status == "new" && (lastProcessedTimestamp == null || it.timestamp > lastProcessedTimestamp)
        }

        if (newMessages.isNotEmpty()) {
            val latestMessage = newMessages.maxByOrNull { it.timestamp }!!
            processNewMessage(latestMessage)
        }
    }

    private suspend fun processNewMessage(message: Message) = withContext(Dispatchers.IO) {
        binding.statusText.text = "Processing new message..."
        binding.lastMessageText.text = message.message

        // Convert text to speech using ElevenLabs API
        val client = OkHttpClient()
        val requestBody = """
            {
                "text": "${message.message}",
                "model_id": "eleven_monolingual_v1",
                "voice_settings": {
                    "stability": 0.5,
                    "similarity_boost": 0.5
                }
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.elevenlabs.io/v1/text-to-speech/21m00Tcm4TlvDq8ikWAM") // Default voice
            .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
            .addHeader("xi-api-key", elevenlabsApiKey)
            .build()

        val response = client.newCall(request).execute()
        
        if (response.isSuccessful) {
            val audioFile = File(cacheDir, "message_audio.mp3")
            FileOutputStream(audioFile).use { output ->
                response.body()?.byteStream()?.copyTo(output)
            }

            withContext(Dispatchers.Main) {
                playAudio(audioFile.path)
                binding.statusText.text = "Playing message..."
            }

            // Mark message as processed
            api.markProcessed(MarkProcessedRequest(message.timestamp))
            lastProcessedTimestamp = message.timestamp
        } else {
            withContext(Dispatchers.Main) {
                binding.statusText.text = "Error: Failed to convert text to speech"
            }
        }
    }

    private fun playAudio(audioPath: String) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(audioPath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error playing audio", e)
            binding.statusText.text = "Error: Failed to play audio"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

interface ApiService {
    @GET("/get_messages")
    suspend fun getMessages(): List<Message>

    @POST("/mark_processed")
    suspend fun markProcessed(@Body request: MarkProcessedRequest)
}

data class Message(
    val message: String,
    val timestamp: String,
    val status: String
)

data class MarkProcessedRequest(
    val timestamp: String
) 