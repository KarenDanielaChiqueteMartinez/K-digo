package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.components.KodeLearnButton
import com.example.kodelearn.ui.components.StatCard
import com.example.kodelearn.ui.theme.*
import com.example.kodelearn.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: KodeLearnRepository,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            // Profile Header
            ProfileHeader(
                name = uiState.user?.name ?: "Usuario",
                biography = uiState.user?.biography ?: "",
                onBiographyChanged = { viewModel.updateBiography(it) },
                onNavigateToSettings = onNavigateToSettings
            )
        }
        
        item {
            // Statistics Section
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Daily Streak",
                            tint = WarningColor,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = uiState.user?.dailyStreak?.toString() ?: "0",
                    label = "Racha diaria",
                    modifier = Modifier.weight(1f),
                    iconColor = WarningColor
                )
                
                StatCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Total XP",
                            tint = XPColor,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = uiState.user?.totalXP?.toString() ?: "0",
                    label = "XP total",
                    modifier = Modifier.weight(1f),
                    iconColor = XPColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            StatCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "League",
                        tint = WoodenLeague,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = uiState.user?.league ?: "Wooden",
                label = "Liga",
                iconColor = WoodenLeague
            )
        }
        
        item {
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KodeLearnButton(
                    text = "Compartir mi progreso",
                    onClick = { viewModel.shareProgress() },
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                
                KodeLearnButton(
                    text = "Prueba PRO Gratis",
                    onClick = { /* TODO: Navigate to PRO screen */ },
                    modifier = Modifier.fillMaxWidth(),
                    variant = com.example.kodelearn.ui.components.ButtonVariant.Secondary
                )
            }
        }
        
        item {
            // Friends Section
            FriendsSection()
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    biography: String,
    onBiographyChanged: (String) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    var isEditingBio by remember { mutableStateOf(false) }
    var bioText by remember { mutableStateOf(biography) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Settings Icon - Top Right
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Biography
            if (isEditingBio) {
                OutlinedTextField(
                    value = bioText,
                    onValueChange = { bioText = it },
                    placeholder = { Text("Añadir biografía") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            isEditingBio = false
                            bioText = biography
                        }
                    ) {
                        Text("Cancelar")
                    }
                    
                    TextButton(
                        onClick = {
                            onBiographyChanged(bioText)
                            isEditingBio = false
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            } else {
                TextButton(
                    onClick = { isEditingBio = true }
                ) {
                    Text(
                        text = if (biography.isEmpty()) "Añadir biografía" else biography,
                        color = if (biography.isEmpty()) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun FriendsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Amigos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Aún sin amigos/as",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            KodeLearnButton(
                text = "Añadir amigos",
                onClick = { /* TODO: Navigate to add friends */ },
                variant = com.example.kodelearn.ui.components.ButtonVariant.Outline,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Friends",
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    KodeLearnTheme {
        ProfileScreenContent(
            userName = "Estudiante Preview",
            biography = "Apasionado por la programación",
            dailyStreak = 5,
            totalXP = 350,
            league = "Bronze",
            friendsCount = 0,
            onBiographyChanged = {},
            onShareProgress = {},
            onTryPro = {},
            onAddFriends = {}
        )
    }
}

@Composable
private fun ProfileScreenContent(
    userName: String,
    biography: String,
    dailyStreak: Int,
    totalXP: Int,
    league: String,
    friendsCount: Int,
    onBiographyChanged: (String) -> Unit,
    onShareProgress: () -> Unit,
    onTryPro: () -> Unit,
    onAddFriends: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            // Profile Header
            ProfileHeader(
                name = userName,
                biography = biography,
                onBiographyChanged = onBiographyChanged
            )
        }
        
        item {
            // Statistics Section
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Daily Streak",
                            tint = WarningColor,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = dailyStreak.toString(),
                    label = "Racha diaria",
                    modifier = Modifier.weight(1f),
                    iconColor = WarningColor
                )
                
                StatCard(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Total XP",
                            tint = XPColor,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = totalXP.toString(),
                    label = "XP total",
                    modifier = Modifier.weight(1f),
                    iconColor = XPColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            StatCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "League",
                        tint = WoodenLeague,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = league,
                label = "Liga",
                iconColor = WoodenLeague
            )
        }
        
        item {
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KodeLearnButton(
                    text = "Compartir mi progreso",
                    onClick = onShareProgress,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                
                KodeLearnButton(
                    text = "Prueba PRO Gratis",
                    onClick = onTryPro,
                    modifier = Modifier.fillMaxWidth(),
                    variant = com.example.kodelearn.ui.components.ButtonVariant.Secondary
                )
            }
        }
        
        item {
            // Friends Section
            FriendsSection()
        }
    }
}
