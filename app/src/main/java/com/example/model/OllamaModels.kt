package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OllamaResponse(
    val response: String
)
