package com.example.fianca.data.api.dto

data class ServiceRequestDto(
    val categoryId: Int,
    val description: String,
    val location: String,
    val budget: Double? = null,
    val clientId: Int // Assuming we need to send who is creating it, or it's inferred from token
)

data class ServiceResponseDto(
    val id: Int,
    val clientId: Int,
    val categoryId: Int,
    val description: String,
    val location: String,
    val dateTime: Long,
    val budget: Double?,
    val status: String,
    val selectedFreelancerId: Int?
)
