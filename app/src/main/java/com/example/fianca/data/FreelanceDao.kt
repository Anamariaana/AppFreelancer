package com.example.fianca.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?

    @Query("UPDATE users SET isSuspended = :suspended WHERE id = :id")
    suspend fun setSuspended(id: Int, suspended: Boolean)

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAll(): List<CategoryEntity>
}

@Dao
interface FreelancerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: FreelancerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(link: FreelancerCategoryEntity)

    @Query("DELETE FROM freelancer_categories WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun removeCategory(userId: Int, categoryId: Int)

    @Query("SELECT * FROM freelancer_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: Int): FreelancerProfileEntity?

    @Query("SELECT c.* FROM categories c INNER JOIN freelancer_categories fc ON c.id = fc.categoryId WHERE fc.userId = :userId ORDER BY c.name ASC")
    suspend fun getCategories(userId: Int): List<CategoryEntity>
}

@Dao
interface RequestsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createRequest(request: ServiceRequestEntity): Long

    @Query("SELECT * FROM service_requests WHERE clientId = :clientId ORDER BY id DESC")
    suspend fun getRequestsByClient(clientId: Int): List<ServiceRequestEntity>

    @Query("UPDATE service_requests SET status = 'Cancelado' WHERE id = :requestId")
    suspend fun cancelRequest(requestId: Int)

    @Query("UPDATE service_requests SET selectedFreelancerId = :freelancerId, status = 'Aceito' WHERE id = :requestId")
    suspend fun selectFreelancer(requestId: Int, freelancerId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInterest(interest: ServiceInterestEntity): Long

    @Query("SELECT u.* FROM users u INNER JOIN service_interests si ON u.id = si.freelancerId WHERE si.requestId = :requestId AND si.status = 'Aceito'")
    suspend fun getInterestedFreelancers(requestId: Int): List<UserEntity>

    @Query("SELECT sr.* FROM service_requests sr WHERE sr.categoryId IN (:categoryIds) AND sr.status = 'Aberto' ORDER BY sr.id DESC")
    suspend fun getOpenRequestsForCategories(categoryIds: List<Int>): List<ServiceRequestEntity>
}

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun send(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages WHERE requestId = :requestId ORDER BY timestamp ASC")
    suspend fun getConversation(requestId: Int): List<ChatMessageEntity>
}

@Dao
interface RatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(rating: RatingEntity): Long

    @Query("SELECT AVG(score) FROM ratings WHERE toUserId = :userId")
    suspend fun getAverage(userId: Int): Double?

    @Query("SELECT * FROM ratings WHERE toUserId = :userId ORDER BY id DESC")
    suspend fun getAllForUser(userId: Int): List<RatingEntity>
}

