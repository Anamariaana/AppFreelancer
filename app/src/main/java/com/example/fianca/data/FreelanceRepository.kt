package com.example.fianca.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FreelanceRepository(private val db: FiancaDatabase) {
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val freelancerDao = db.freelancerDao()
    private val requestsDao = db.requestsDao()
    private val chatDao = db.chatDao()
    private val ratingDao = db.ratingDao()

    suspend fun registerUser(name: String, email: String, password: String, role: String): UserEntity = withContext(Dispatchers.IO) {
        val existing = userDao.getByEmail(email)
        if (existing != null) return@withContext existing
        val id = userDao.insert(UserEntity(name = name, email = email, password = password, role = role)).toInt()
        userDao.getById(id)!!
    }

    suspend fun login(email: String, password: String): UserEntity? = withContext(Dispatchers.IO) {
        val u = userDao.getByEmail(email)
        if (u != null && u.password == password && !u.isSuspended) u else null
    }

    suspend fun updateProfile(user: UserEntity) = withContext(Dispatchers.IO) { userDao.update(user) }

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
        val id = requestsDao.createRequest(req).toInt()
        req.copy(id = id)
    }
    suspend fun getClientRequests(clientId: Int) = withContext(Dispatchers.IO) { requestsDao.getRequestsByClient(clientId) }
    suspend fun cancelRequest(requestId: Int) = withContext(Dispatchers.IO) { requestsDao.cancelRequest(requestId) }
    suspend fun selectFreelancer(requestId: Int, freelancerId: Int) = withContext(Dispatchers.IO) { requestsDao.selectFreelancer(requestId, freelancerId) }
    suspend fun addInterest(requestId: Int, freelancerId: Int) = withContext(Dispatchers.IO) { requestsDao.addInterest(ServiceInterestEntity(requestId = requestId, freelancerId = freelancerId)) }
    suspend fun getInterestedFreelancers(requestId: Int) = withContext(Dispatchers.IO) { requestsDao.getInterestedFreelancers(requestId) }
    suspend fun getOpenRequestsForFreelancer(userId: Int): List<ServiceRequestEntity> = withContext(Dispatchers.IO) {
        val cats = freelancerDao.getCategories(userId).map { it.id }
        if (cats.isEmpty()) emptyList() else requestsDao.getOpenRequestsForCategories(cats)
    }

    suspend fun sendMessage(message: ChatMessageEntity) = withContext(Dispatchers.IO) { chatDao.send(message) }
    suspend fun getConversation(requestId: Int) = withContext(Dispatchers.IO) { chatDao.getConversation(requestId) }

    suspend fun addRating(r: RatingEntity) = withContext(Dispatchers.IO) { ratingDao.add(r) }
    suspend fun avgRating(userId: Int) = withContext(Dispatchers.IO) { ratingDao.getAverage(userId) ?: 0.0 }
    suspend fun adminUsers(): List<UserEntity> = withContext(Dispatchers.IO) { userDao.getAll() }
    suspend fun adminSetSuspended(userId: Int, suspended: Boolean) = withContext(Dispatchers.IO) { userDao.setSuspended(userId, suspended) }
}

