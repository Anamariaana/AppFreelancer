package com.example.fianca.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fianca.data.CategoryEntity
import com.example.fianca.data.FreelanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: FreelanceRepository) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    private val _selectedRole = MutableStateFlow("CLIENTE")
    val selectedRole: StateFlow<String> = _selectedRole
    
    private val _authResult = MutableStateFlow<Boolean?>(null)
    val authResult: StateFlow<Boolean?> = _authResult
    private val _loggedInRole = MutableStateFlow<String?>(null)
    val loggedInRole: StateFlow<String?> = _loggedInRole
    private val _loggedInUserId = MutableStateFlow<Int?>(null)
    val loggedInUserId: StateFlow<Int?> = _loggedInUserId

    // Categories for registration
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories
    private val _selectedCategories = MutableStateFlow<Set<Int>>(emptySet())
    val selectedCategories: StateFlow<Set<Int>> = _selectedCategories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            var cats = repository.getCategories()
            if (cats.isEmpty()) {
                // Seed some categories if empty
                val initialCats = listOf("Eletricista", "Encanador", "Pedreiro", "Pintor", "Jardineiro", "Diarista", "Mecânico", "Informática")
                initialCats.forEach { repository.addCategory(it) }
                cats = repository.getCategories()
            }
            _categories.value = cats
        }
    }

    fun setEmail(v: String) { _email.value = v }
    fun setPassword(v: String) { _password.value = v }
    fun setConfirmPassword(v: String) { _confirmPassword.value = v }
    fun setName(v: String) { _name.value = v }
    fun setRole(v: String) { _selectedRole.value = v }
    
    fun toggleCategory(categoryId: Int) {
        val current = _selectedCategories.value.toMutableSet()
        if (current.contains(categoryId)) {
            current.remove(categoryId)
        } else {
            current.add(categoryId)
        }
        _selectedCategories.value = current
    }

    fun resetAuthResult() { 
        _authResult.value = null 
        _loggedInRole.value = null
        _loggedInUserId.value = null
    }

    fun login() {
        viewModelScope.launch {
            val u = repository.login(_email.value, _password.value)
            if (u != null) {
                _loggedInRole.value = u.role
                _loggedInUserId.value = u.id
                _authResult.value = true
            } else {
                _authResult.value = false
            }
        }
    }

    fun register() {
        if (_password.value != _confirmPassword.value) {
            _authResult.value = false
            return
        }
        viewModelScope.launch {
            val u = repository.registerUser(_name.value, _email.value, _password.value, _selectedRole.value)
            
            if (_selectedRole.value == "FREELANCER") {
                _selectedCategories.value.forEach { catId ->
                    repository.linkFreelancerCategory(u.id, catId)
                }
            }

            _loggedInRole.value = u.role
            _loggedInUserId.value = u.id
            _authResult.value = true 
        }
    }
}
