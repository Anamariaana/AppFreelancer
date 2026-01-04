package com.example.fianca.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String? = null,
    val location: String? = null,
    val photoUri: String? = null,
    val isSuspended: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "freelancer_categories", indices = [Index(value = ["userId","categoryId"], unique = true)])
data class FreelancerCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Int
)

@Entity(tableName = "freelancer_profile")
data class FreelancerProfileEntity(
    @PrimaryKey val userId: Int,
    val description: String? = null,
    val portfolioUris: String? = null,
    val radiusKm: Int? = null,
    val cities: String? = null,
    val availability: String = "Offline"
)

@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val categoryId: Int,
    val description: String,
    val location: String,
    val dateTime: Long,
    val budget: Double? = null,
    val status: String = "Aberto",
    val selectedFreelancerId: Int? = null
)

@Entity(tableName = "service_interests", indices = [Index(value = ["requestId","freelancerId"], unique = true)])
data class ServiceInterestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestId: Int,
    val freelancerId: Int,
    val status: String = "Aceito"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestId: Int,
    val senderUserId: Int,
    val receiverUserId: Int,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "ratings", indices = [Index(value = ["toUserId"])])
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requestId: Int,
    val fromUserId: Int,
    val toUserId: Int,
    val score: Int,
    val comment: String? = null
)

