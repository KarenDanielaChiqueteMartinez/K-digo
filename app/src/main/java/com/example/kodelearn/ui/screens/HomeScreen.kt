package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.components.KodeLearnButton
import com.example.kodelearn.ui.components.StatCard
import com.example.kodelearn.ui.theme.*
import com.example.kodelearn.ui.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    repository: KodeLearnRepository,
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
            // Welcome Header
            WelcomeHeader(userName = uiState.user?.name ?: "Estudiante")
        }
        
        item {
            // Quick Stats
            QuickStatsSection(
                streak = uiState.user?.dailyStreak ?: 0,
                xp = uiState.user?.totalXP ?: 0,
                hearts = uiState.user?.hearts ?: 5,
                coins = uiState.user?.coins ?: 0
            )
        }
        
        item {
            // Continue Learning Section
            ContinueLearningSection()
        }
        
        item {
            // Today's Goal
            TodaysGoalSection()
        }
        
        item {
            // Recent Achievements
            RecentAchievementsSection()
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "¡Hola, $userName! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "¿Listo para seguir aprendiendo programación?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    streak: Int,
    xp: Int,
    hearts: Int,
    coins: Int
) {
    Column {
        Text(
            text = "Tu progreso de hoy",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                listOf(
                    Triple("$streak", "Racha", Icons.Default.LocalFireDepartment),
                    Triple("$xp", "XP Total", Icons.Default.Star),
                    Triple("$hearts", "Vidas", Icons.Default.Favorite),
                    Triple("$coins", "Monedas", Icons.Default.EmojiEvents)
                )
            ) { (value, label, icon) ->
                StatCard(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    value = value,
                    label = label,
                    modifier = Modifier.width(100.dp),
                    iconColor = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun ContinueLearningSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Continúa aprendiendo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Python Básico",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Progreso: 40% completado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            KodeLearnButton(
                text = "Continuar lección",
                onClick = { /* Navigate to learning */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TodaysGoalSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Meta de hoy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Completar 1 lección",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "0/1 completadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = "🎯",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = 0.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecentAchievementsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Logros recientes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "¡Sigue así! Completa tu primera lección para desbloquear tu primer logro.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    KodeLearnTheme {
        HomeScreenContent()
    }
}

@Composable
private fun HomeScreenContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            WelcomeHeader(userName = "Estudiante")
        }
        
        item {
            QuickStatsSection(
                streak = 3,
                xp = 130,
                hearts = 5,
                coins = 220
            )
        }
        
        item {
            ContinueLearningSection()
        }
        
        item {
            TodaysGoalSection()
        }
        
        item {
            RecentAchievementsSection()
        }
    }
}
