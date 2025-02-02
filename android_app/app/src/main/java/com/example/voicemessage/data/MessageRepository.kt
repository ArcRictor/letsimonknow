package com.example.voicemessage.data

import android.util.Log

class MessageRepository(private val apiService: ApiService) {
    suspend fun login(passcode: String): Boolean {
        return try {
            Log.d("MessageRepository", "Actual passcode value: $passcode")
            Log.d("MessageRepository", "Attempting login with passcode length: ${passcode.length}")
            val response = apiService.login(mapOf("passcode" to passcode))
            Log.d("MessageRepository", "Login response: $response")
            response["success"] == true
        } catch (e: Exception) {
            Log.e("MessageRepository", "Login failed with error: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun getMessages(): List<Message> {
        try {
            Log.d("MessageRepository", "Fetching messages...")
            val messages = apiService.getMessages()
            Log.d("MessageRepository", "Fetched ${messages.size} messages")
            return messages
        } catch (e: Exception) {
            Log.e("MessageRepository", "Failed to fetch messages: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
} 