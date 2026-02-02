package com.example.fianca.data

import com.example.fianca.data.api.ApiClient
import com.example.fianca.data.api.dto.LoginRequest
import com.example.fianca.data.api.dto.RegisterRequest
import com.example.fianca.data.api.dto.ServiceRequestDto
import com.example.fianca.data.api.dto.ServiceResponseDto
import com.example.fianca.data.api.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FreelanceRepository(private val db: FiancaDatabase) {
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val freelancerDao = db.freelancerDao()
    private val requestsDao = db.requestsDao()
    private val chatDao = db.chatDao()
    private val ratingDao = db.ratingDao()

    private val api = ApiClient.instance
    private var authToken: String? = null

    // Helper to map UserDto to UserEntity
    private fun UserDto.toEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            name = this.name,
            email = this.email,
            password = "", // Password not returned by API
            role = this.role,
            phone = this.phone,
            location = this.location,
            photoUri = this.photoUri
        )
    }

    // Helper to map ServiceResponseDto to ServiceRequestEntity
    private fun ServiceResponseDto.toEntity(): ServiceRequestEntity {
        return ServiceRequestEntity(
            id = this.id,
            clientId = this.clientId,
            categoryId = this.categoryId,
            description = this.description,
            location = this.location,
            dateTime = this.dateTime,
            budget = this.budget,
            status = this.status,
            selectedFreelancerId = this.selectedFreelancerId
        )
    }

    suspend fun registerUser(name: String, email: String, password: String, role: String): UserEntity = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(name, email, password, role))
            authToken = response.token
            val userEntity = response.user.toEntity()
            // Cache in local DB
            userDao.insert(userEntity.copy(password = password)) // Save with password locally for offline login if needed, or just cache
            userEntity
        } catch (e: Exception) {
            // Fallback to local DB or rethrow
            e.printStackTrace()
            // For now, if API fails, we try local for demo purposes, or just throw
             val existing = userDao.getByEmail(email)
            if (existing != null) return@withContext existing
            val id = userDao.insert(UserEntity(name = name, email = email, password = password, role = role)).toInt()
            userDao.getById(id)!!
        }
    }

    suspend fun login(email: String, password: String): UserEntity? = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(email, password))
            authToken = response.token
            val userEntity = response.user.toEntity()
            // Update local cache
            val existing = userDao.getByEmail(email)
            if (existing == null) {
                userDao.insert(userEntity.copy(password = password))
            } else {
                userDao.update(userEntity.copy(id = existing.id, password = password))
            }
            userEntity
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to local
            val u = userDao.getByEmail(email)
            if (u != null && u.password == password && !u.isSuspended) u else null
        }
    }

    suspend fun updateProfile(user: UserEntity) = withContext(Dispatchers.IO) { 
        // API doesn't have update profile endpoint listed, keeping local
        userDao.update(user) 
    }

    suspend fun getCategories(): List<CategoryEntity> = withContext(Dispatchers.IO) { categoryDao.getAll() }
    suspend fun addCategory(name: String): CategoryEntity = withContext(Dispatchers.IO) {
        val id = categoryDao.insert(CategoryEntity(name = name)).toInt()
        CategoryEntity(id = id, name = name)
    }
    suspend fun deleteCategory(id: Int) = withContext(Dispatchers.IO) { categoryDao.delete(id) }
    suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) { categoryDao.update(category) }

    suspend fun upsertFreelancerProfile(profile: FreelancerProfileEntity) = withContext(Dispatchers.IO) { freelancerDao.upsertProfile(profile) }
    suspend fun linkFreelancerCategory(userId: Int, categoryId: Int) = withContext(Dispatchers.IO) { freelancerDao.addCategory(FreelancerCategoryEntity(userId = userId, categoryId = categoryId)) }
    suspend fun unlinkFreelancerCategory(userId: Int, categoryId: Int) = withContext(Dispatchers.IO) { freelancerDao.removeCategory(userId, categoryId) }
    suspend fun getFreelancerCategories(userId: Int) = withContext(Dispatchers.IO) { freelancerDao.getCategories(userId) }
    suspend fun getFreelancerProfile(userId: Int) = withContext(Dispatchers.IO) { freelancerDao.getProfile(userId) }

    suspend fun createRequest(req: ServiceRequestEntity): ServiceRequestEntity = withContext(Dispatchers.IO) {
        try {
            val dto = ServiceRequestDto(
                categoryId = req.categoryId,
                description = req.description,
                location = req.location,
                budget = req.budget,
                clientId = req.clientId
            )
            val response = api.createService(dto)
            val entity = response.toEntity()
            requestsDao.createRequest(entity) // Cache
            entity
        } catch (e: Exception) {
            e.printStackTrace()
            // Local fallback
            val id = requestsDao.createRequest(req).toInt()
            req.copy(id = id)
        }
    }

    suspend fun getClientRequests(clientId: Int) = withContext(Dispatchers.IO) { 
        // API doesn't have "get client requests", using local
        requestsDao.getRequestsByClient(clientId) 
    }
    
    suspend fun cancelRequest(requestId: Int) = withContext(Dispatchers.IO) { 
        try {
            api.deleteService(requestId) // Assuming cancel = delete for now based on available endpoints
            requestsDao.cancelRequest(requestId)
        } catch (e: Exception) {
            e.printStackTrace()
            requestsDao.cancelRequest(requestId)
        }
    }

    suspend fun selectFreelancer(requestId: Int, freelancerId: Int) = withContext(Dispatchers.IO) { 
        // API update service
        requestsDao.selectFreelancer(requestId, freelancerId) 
    }
    
    suspend fun addInterest(requestId: Int, freelancerId: Int) = withContext(Dispatchers.IO) { requestsDao.addInterest(ServiceInterestEntity(requestId = requestId, freelancerId = freelancerId)) }
    suspend fun getInterestedFreelancers(requestId: Int) = withContext(Dispatchers.IO) { requestsDao.getInterestedFreelancers(requestId) }
    
    suspend fun getOpenRequestsForFreelancer(userId: Int): List<ServiceRequestEntity> = withContext(Dispatchers.IO) {
        try {
            // Use searchServices API? Or local. API has 'buscarservico'.
            // For now, let's try to fetch from API if possible, else local.
            val services = api.searchServices("")
            services.map { it.toEntity() }
        } catch (e: Exception) {
            val cats = freelancerDao.getCategories(userId).map { it.id }
            if (cats.isEmpty()) emptyList() else requestsDao.getOpenRequestsForCategories(cats)
        }
    }
    
    suspend fun getFreelancerWorks(freelancerId: Int) = withContext(Dispatchers.IO) { requestsDao.getRequestsByFreelancer(freelancerId) }
    
    suspend fun getUsersByIds(ids: List<Int>) = withContext(Dispatchers.IO) { 
        // Try to fetch from API if not found?
        userDao.getUsersByIds(ids) 
    }

    suspend fun sendMessage(message: ChatMessageEntity) = withContext(Dispatchers.IO) { chatDao.send(message) }
    suspend fun getConversation(requestId: Int) = withContext(Dispatchers.IO) { chatDao.getConversation(requestId) }

    suspend fun addRating(r: RatingEntity) = withContext(Dispatchers.IO) { ratingDao.add(r) }
    suspend fun avgRating(userId: Int) = withContext(Dispatchers.IO) { ratingDao.getAverage(userId) ?: 0.0 }
    suspend fun adminUsers(): List<UserEntity> = withContext(Dispatchers.IO) { userDao.getAll() }
    suspend fun adminSetSuspended(userId: Int, suspended: Boolean) = withContext(Dispatchers.IO) { userDao.setSuspended(userId, suspended) }
}

