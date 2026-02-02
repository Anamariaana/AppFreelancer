package com.example.fianca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fianca.data.FiancaDatabase
import com.example.fianca.data.FreelanceRepository
import com.example.fianca.ui.admin.AdminHomeScreen
import com.example.fianca.ui.auth.AuthViewModel
import com.example.fianca.ui.auth.LoginScreenAuth
import com.example.fianca.ui.auth.RegisterScreenAuth
import com.example.fianca.ui.client.ClientHomeScreen
import com.example.fianca.ui.common.FreelancerUI
import com.example.fianca.ui.freelancer.FreelancerHomeScreen
import com.example.fianca.ui.navigation.Routes
import com.example.fianca.ui.splash.SplashScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            FreelancerUI {
                FreelancerApp()
            }
        }
    }
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
    LaunchedEffect(Unit) {
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    NavHost(navController = navController, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
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
