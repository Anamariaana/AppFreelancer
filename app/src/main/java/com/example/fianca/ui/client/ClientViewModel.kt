package com.example.fianca.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianca.data.FreelanceRepository
import com.example.fianca.data.ServiceRequestEntity
import com.example.fianca.ui.common.FreelancerDisplayInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: FreelanceRepository, private val userId: Int) : ViewModel() {
    private val _myRequests = MutableStateFlow<List<ServiceRequestEntity>>(emptyList())
    val myRequests: StateFlow<List<ServiceRequestEntity>> = _myRequests

    private val _myFreelancers = MutableStateFlow<List<FreelancerDisplayInfo>>(emptyList())
    val myFreelancers: StateFlow<List<FreelancerDisplayInfo>> = _myFreelancers

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _myRequests.value = repository.getClientRequests(userId)
            
            val pastRequests = repository.getClientRequests(userId).filter { it.selectedFreelancerId != null }
            val freelancerIds = pastRequests.mapNotNull { it.selectedFreelancerId }.distinct()
            
            if (freelancerIds.isNotEmpty()) {
                val freelancersList = repository.getUsersByIds(freelancerIds)
                _myFreelancers.value = freelancersList.map { u ->
                    FreelancerDisplayInfo(u.id, u.name, "Freelancer", 4.5) // Placeholder category/rating
                }
            }
        }
    }
}
