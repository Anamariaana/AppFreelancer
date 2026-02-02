package com.example.fianca.data.api.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val categoryIds: List<Int>? = null
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val location: String? = null,
    val photoUri: String? = null
)
