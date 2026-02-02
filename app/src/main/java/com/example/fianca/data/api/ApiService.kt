package com.example.fianca.data.api

import com.example.fianca.data.api.dto.*
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("cadastro")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("criarservico")
    suspend fun createService(@Body request: ServiceRequestDto): ServiceResponseDto

    @GET("verservico/{id}")
    suspend fun getService(@Path("id") id: Int): ServiceResponseDto

    @PUT("atualizarservico/{id}")
    suspend fun updateService(@Path("id") id: Int, @Body request: ServiceRequestDto): ServiceResponseDto

    @DELETE("eliminarservico/{id}")
    suspend fun deleteService(@Path("id") id: Int)

    @GET("buscarservico")
    suspend fun searchServices(@Query("q") query: String): List<ServiceResponseDto>

    @GET("buscarcliente")
    suspend fun searchClients(@Query("q") query: String): List<UserDto>

    @GET("buscarfrelancer")
    suspend fun searchFreelancers(@Query("q") query: String): List<UserDto>
}
