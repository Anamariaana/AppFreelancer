package com.example.fianca.ui.freelancer

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianca.data.FreelanceRepository
import com.example.fianca.ui.client.ClientNotificationsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelancerHomeScreen(
    userId: Int,
    repository: FreelanceRepository,
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
                }
            
            }
        }
    }
}
