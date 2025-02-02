package com.example.voicemessage.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicemessage.data.MessageRepository
import com.example.voicemessage.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.voicemessage.BuildConfig

sealed class UiState {
    object Loading : UiState()
    data class Success(val messages: List<Message>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(
    private val repository: MessageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    init {
        login()
    }
    
    private fun login() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                Log.d("MainViewModel", "Attempting to login...")
                val success = repository.login(BuildConfig.PASSCODE)
                _isAuthenticated.value = success
                if (success) {
                    loadMessages()
                } else {
                    _uiState.value = UiState.Error("Authentication failed")
                    Log.e("MainViewModel", "Authentication failed")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Login error: ${e.message}")
                Log.e("MainViewModel", "Login error: ${e.message}", e)
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                Log.d("MainViewModel", "Starting to fetch messages...")
                val response = repository.getMessages()
                Log.d("MainViewModel", "Raw response: $response")
                _messages.value = response
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load messages: ${e.message}")
                Log.e("MainViewModel", "Error fetching messages: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
} 