package com.example.voicemessage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicemessage.data.ApiService
import com.example.voicemessage.data.MessageRepository

class MainViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(MessageRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 