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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.fianca.ui.theme.white100
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RomanticQuizTheme {
                FreelancerApp()
            }
        }
    }
}

@Composable
fun RomanticQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF6B6B),
            secondary = Color(0xFFFFB3B3),
            background = Color(0xFF4A0E0E),
            surface = Color(0xFF6B1515),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

class AuthViewModel(private val repository: FreelanceRepository) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    private val _authResult = MutableStateFlow<Boolean?>(null)
    val authResult: StateFlow<Boolean?> = _authResult

    fun setEmail(v: String) { _email.value = v }
    fun setPassword(v: String) { _password.value = v }
    fun setName(v: String) { _name.value = v }
    fun resetAuthResult() { _authResult.value = null }

    fun login() {
        viewModelScope.launch {
            val u = repository.login(_email.value, _password.value)
            _authResult.value = u != null
        }
    }

    fun register(role: String = "CLIENT") {
        viewModelScope.launch {
            val u = repository.registerUser(_name.value, _email.value, _password.value, role)
            _authResult.value = u != null
        }
    }
}

object Routes {
    const val Login = "auth/login"
    const val Register = "auth/register"
    const val SelectProfile = "select/profile"
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
                onLoginSuccess = {
                    navController.navigate(Routes.SelectProfile) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
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
        composable(Routes.SelectProfile) {
            SelectProfileScreen(
                onClient = {
                    navController.navigate(Routes.ClientHome) {
                        launchSingleTop = true
                    }
                },
                onFreelancer = {
                    navController.navigate(Routes.FreelancerHome) {
                        launchSingleTop = true
                    }
                },
                onAdmin = {
                    navController.navigate(Routes.AdminHome) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.ClientHome) {
            ClientHomeScreen(
                onSelectProfile = {
                    navController.navigate(Routes.SelectProfile) {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.FreelancerHome) {
            FreelancerHomeScreen(
                onSelectProfile = {
                    navController.navigate(Routes.SelectProfile) {
                        launchSingleTop = true
                    }
                },
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
fun LoginScreenAuth(viewModel: AuthViewModel, onLoginSuccess: () -> Unit, onGoRegister: () -> Unit) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.login() }, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
            Button(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) { Text("Cadastrar") }
        }

        when (authResult) {
            true -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                    viewModel.resetAuthResult()
                }
            }
            false -> {
                Text(
                    text = "Credenciais inválidas. Tente novamente.",
                    color = Color.Red,
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
    val authResult by viewModel.authResult.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.setName(it) },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.register() }, modifier = Modifier.fillMaxWidth()) { Text("Cadastrar") }
            Button(onClick = onRegistered, modifier = Modifier.fillMaxWidth()) { Text("Voltar ao Login") }
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
                    text = "Falha no cadastro. Verifique os dados.",
                    color = Color.Red,
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
fun SelectProfileScreen(onClient: () -> Unit, onFreelancer: () -> Unit, onAdmin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onClient, modifier = Modifier.fillMaxWidth()) { Text("Cliente") }
        Button(onClick = onFreelancer, modifier = Modifier.fillMaxWidth()) { Text("Freelancer") }
        Button(onClick = onAdmin, modifier = Modifier.fillMaxWidth()) { Text("Admin") }
    }
}

@Composable
fun ClientHomeScreen(onSelectProfile: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Home do Cliente", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onSelectProfile, modifier = Modifier.fillMaxWidth()) { Text("Selecionar outro perfil") }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Sair") }
    }
}

@Composable
fun FreelancerHomeScreen(onSelectProfile: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Home do Freelancer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onSelectProfile, modifier = Modifier.fillMaxWidth()) { Text("Selecionar outro perfil") }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Sair") }
    }
}

@Composable
fun AdminHomeScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) { Text("Home do Admin") }
}
