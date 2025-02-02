package com.example.voicemessage.data

import retrofit2.http.*

interface ApiService {
    @GET("get_messages")
    suspend fun getMessages(): List<Message>
    
    @GET("test_message")
    suspend fun getTestMessage(): List<Message>
    
    @GET("message/{id}")
    suspend fun getMessage(@Path("id") id: String): Message
    
    @POST("login")
    suspend fun login(@Body passcode: Map<String, String>): Map<String, Boolean>
} 