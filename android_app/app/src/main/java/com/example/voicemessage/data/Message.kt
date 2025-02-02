package com.example.voicemessage.data

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("status") val status: String
) 