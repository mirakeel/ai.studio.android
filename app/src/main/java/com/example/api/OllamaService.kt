package com.example.api

import com.example.model.OllamaRequest
import com.example.model.OllamaResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OllamaService {
    @POST("/api/generate")
    suspend fun generate(@Body request: OllamaRequest): OllamaResponse
}
