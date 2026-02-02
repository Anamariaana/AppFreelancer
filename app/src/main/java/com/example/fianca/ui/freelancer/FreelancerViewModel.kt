package com.example.fianca.ui.freelancer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianca.data.FreelanceRepository
import com.example.fianca.data.ServiceRequestEntity
import com.example.fianca.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FreelancerViewModel(private val repository: FreelanceRepository, private val userId: Int) : ViewModel() {
    private val _opportunities = MutableStateFlow<List<ServiceRequestEntity>>(emptyList())
    val opportunities: StateFlow<List<ServiceRequestEntity>> = _opportunities

    private val _myClients = MutableStateFlow<List<UserEntity>>(emptyList())
    val myClients: StateFlow<List<UserEntity>> = _myClients

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _opportunities.value = repository.getOpenRequestsForFreelancer(userId)
            
            val myWorks = repository.getFreelancerWorks(userId)
            val clientIds = myWorks.map { it.clientId }.distinct()
            if (clientIds.isNotEmpty()) {
                _myClients.value = repository.getUsersByIds(clientIds)
            }
        }
    }
}
