package com.example.fianca.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

                TextButton(onClick = onRegistered) {
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
