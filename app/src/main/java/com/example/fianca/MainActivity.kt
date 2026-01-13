package com.example.fianca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.rememberCoroutineScope
import com.example.fianca.data.CategoryEntity
import com.example.fianca.data.ServiceRequestEntity
import com.example.fianca.data.UserEntity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fianca.data.FiancaDatabase
import com.example.fianca.data.FreelanceRepository
import com.example.fianca.ui.theme.RedPrimary
import com.example.fianca.ui.theme.WineSecondary
import com.example.fianca.ui.theme.WhiteBackground
import com.example.fianca.ui.theme.TextColor
import com.example.fianca.ui.theme.white100
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreelancerUI {
                FreelancerApp()
            }
        }
    }
}

@Composable
fun FreelancerUI(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = RedPrimary,
            secondary = WineSecondary,
            background = WhiteBackground,
            surface = WhiteBackground,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = TextColor,
            onSurface = TextColor
        ),
        content = content
    )
}

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

data class FreelancerDisplayInfo(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double
)

object Routes {
    const val Login = "auth/login"
    const val Register = "auth/register"
    const val ClientHome = "client/home"
    const val FreelancerHome = "freelancer/home"
    const val AdminHome = "admin/home"
}

@Composable
fun FreelancerApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember(context) { FreelanceRepository(FiancaDatabase.getInstance(context)) }
    val authViewModel: AuthViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(repository) as T
        }
    })

    NavHost(navController = navController, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginScreenAuth(
                viewModel = authViewModel,
                onNavigateToHome = { role ->
                    when (role) {
                        "CLIENTE" -> navController.navigate(Routes.ClientHome) {
                            popUpTo(Routes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                        "FREELANCER" -> navController.navigate(Routes.FreelancerHome) {
                            popUpTo(Routes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                        "ADMIN" -> navController.navigate(Routes.AdminHome) {
                            popUpTo(Routes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                        else -> navController.navigate(Routes.ClientHome) // Default
                    }
                },
                onGoRegister = {
                    navController.navigate(Routes.Register) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Register) {
            RegisterScreenAuth(
                viewModel = authViewModel,
                onRegistered = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Register) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.ClientHome) {
            val userId = authViewModel.loggedInUserId.collectAsState().value ?: 0
            ClientHomeScreen(
                userId = userId,
                repository = repository,
                onSelectProfile = { /* No longer needed or different logic */ },
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.FreelancerHome) {
            val userId = authViewModel.loggedInUserId.collectAsState().value ?: 0
            FreelancerHomeScreen(
                userId = userId,
                repository = repository,
                onSelectProfile = { /* No longer needed */ },
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.AdminHome) { AdminHomeScreen() }
    }
}

@Composable
fun LoginScreenAuth(viewModel: AuthViewModel, onNavigateToHome: (String) -> Unit, onGoRegister: () -> Unit) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authResult by viewModel.authResult.collectAsState()
    val loggedInRole by viewModel.loggedInRole.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Entrar", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onGoRegister,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cadastrar", fontSize = 18.sp)
            }
        }

        when (authResult) {
            true -> {
                LaunchedEffect(loggedInRole) {
                    loggedInRole?.let { role ->
                        onNavigateToHome(role)
                        viewModel.resetAuthResult()
                    }
                }
            }
            false -> {
                Text(
                    text = "Credenciais inválidas. Tente novamente.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
            null -> {}
        }
    }
}

@Composable
fun RegisterScreenAuth(viewModel: AuthViewModel, onRegistered: () -> Unit) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Criar Conta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.setName(it) },
                    label = { Text("Nome Completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.setConfirmPassword(it) },
                    label = { Text("Confirmar Senha") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == "CLIENTE",
                            onClick = { viewModel.setRole("CLIENTE") }
                        )
                        Text("Cliente")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == "FREELANCER",
                            onClick = { viewModel.setRole("FREELANCER") }
                        )
                        Text("Freelancer")
                    }
                }

                if (selectedRole == "FREELANCER") {
                    Text("Selecione os serviços que presta:", fontWeight = FontWeight.Bold)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        items(categories) { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleCategory(cat.id) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedCategories.contains(cat.id),
                                    onCheckedChange = { viewModel.toggleCategory(cat.id) }
                                )
                                Text(cat.name)
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cadastrar", fontSize = 18.sp, color = Color.White)
                }

                androidx.compose.material3.TextButton(onClick = onRegistered) {
                    Text("Já tem uma conta? Faça Login", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        when (authResult) {
            true -> {
                LaunchedEffect(Unit) {
                    onRegistered()
                    viewModel.resetAuthResult()
                }
            }
            false -> {
                Text(
                    text = if (password != confirmPassword) "As senhas não coincidem." else "Falha no cadastro. Verifique os dados.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
            null -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    userId: Int,
    repository: com.example.fianca.data.FreelanceRepository,
    onSelectProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val clientViewModel = remember { ClientViewModel(repository, userId) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Olá, Cliente", 
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Text("Sair", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Buscar serviços ou freelancers") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Search results content
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Meus Serviços") },
                    label = { Text("Meus Serviços") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Meus Freelancers") },
                    label = { Text("Meus Freelancers") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { 
                        BadgedBox(
                            badge = {
                                Badge { Text("3") }
                            }
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificações") 
                        }
                    },
                    label = { Text("Notificações") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> ClientHomeContent()
                1 -> ClientMyServicesContent(clientViewModel)
                2 -> ClientMyFreelancersContent(clientViewModel)
                3 -> ClientNotificationsContent()
            }
        }
    }
}

@Composable
fun ClientNotificationsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nenhuma notificação nova", fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun ClientHomeContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo à Home do Cliente", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Use a barra de pesquisa para encontrar serviços.", color = Color.Gray)
    }
}

@Composable
fun ClientMyServicesContent(viewModel: ClientViewModel) {
    val requests by viewModel.myRequests.collectAsState()

    if (requests.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nenhum serviço solicitado ainda.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Meus Serviços Solicitados", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            }
            items(requests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(req.description, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val (icon, color) = when (req.status) {
                                "Concluido" -> Icons.Filled.CheckCircle to Color.Green
                                "Cancelado" -> Icons.Filled.Close to Color.Red
                                else -> Icons.Filled.Info to Color.Yellow
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Status: ${req.status}", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientMyFreelancersContent(viewModel: ClientViewModel) {
    val freelancers by viewModel.myFreelancers.collectAsState()

    if (freelancers.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Você ainda não tem freelancers preferidos.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Meus Freelancers", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            }
            items(freelancers) { freelancer ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle, 
                            contentDescription = null, 
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(freelancer.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(freelancer.category, color = Color.Gray, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Text(" ${freelancer.rating}", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelancerHomeScreen(
    userId: Int,
    repository: com.example.fianca.data.FreelanceRepository,
    onSelectProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel = remember { FreelancerViewModel(repository, userId) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Olá, Freelancer", 
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Text("Sair", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Buscar clientes ou serviços") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Search results content
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Clientes") },
                    label = { Text("Clientes") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Serviços") },
                    label = { Text("Serviços") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { 
                         BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificações") 
                         }
                    },
                    label = { Text("Notificações") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> FreelancerHomeContent(viewModel)
                1 -> FreelancerClientsContent(viewModel)
                2 -> FreelancerServicesContent(viewModel)
                3 -> ClientNotificationsContent()
            }
        }
    }
}

@Composable
fun FreelancerHomeContent(viewModel: FreelancerViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Painel do Freelancer", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Bem-vindo de volta! Verifique suas notificações e novos serviços disponíveis.", textAlign = TextAlign.Center, color = Color.Gray)
    }
}

@Composable
fun FreelancerClientsContent(viewModel: FreelancerViewModel) {
    val clients by viewModel.myClients.collectAsState()
    
    if (clients.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
             Text("Nenhum cliente ainda.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Meus Clientes", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp)) }
            items(clients) { client ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                         Spacer(modifier = Modifier.width(16.dp))
                         Column {
                             Text(client.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                             Text(client.email, color = Color.Gray, fontSize = 14.sp)
                         }
                    }
                }
            }
        }
    }
}

@Composable
fun FreelancerServicesContent(viewModel: FreelancerViewModel) {
    val opportunities by viewModel.opportunities.collectAsState()
    
    if (opportunities.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
             Text("Nenhuma oportunidade disponível no momento.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Text("Oportunidades de Serviço", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp)) }
            items(opportunities) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(req.description, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Local: ${req.location}", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* TODO: Implement Accept/Interest Logic */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ver Detalhes")
                        }
                    }
                }z
            }
        }
    }
}

@Composable
fun AdminHomeScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) { Text("Home do Admin") }
}
