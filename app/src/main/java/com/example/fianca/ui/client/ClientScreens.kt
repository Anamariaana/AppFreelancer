package com.example.fianca.ui.client

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianca.data.FreelanceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    userId: Int,
    repository: FreelanceRepository,
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
