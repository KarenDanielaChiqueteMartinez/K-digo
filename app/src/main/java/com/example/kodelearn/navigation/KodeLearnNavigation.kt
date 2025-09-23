package com.example.kodelearn.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.screens.HomeScreen
import com.example.kodelearn.ui.screens.LearningScreen
import com.example.kodelearn.ui.screens.LessonScreen
import com.example.kodelearn.ui.screens.LoginScreen
import com.example.kodelearn.ui.screens.ProfileScreen
import com.example.kodelearn.ui.screens.ProgressScreen
import com.example.kodelearn.ui.screens.RegisterScreen
import com.example.kodelearn.ui.screens.SettingsScreen
import com.example.kodelearn.ui.screens.SimpleHomeScreen
import com.example.kodelearn.ui.screens.SimpleLearningScreen
import com.example.kodelearn.ui.screens.SimpleProgressScreen
import com.example.kodelearn.ui.screens.SimpleProfileScreen
import com.example.kodelearn.ui.screens.WelcomeScreen

sealed class Screen(
    val route: String, 
    val title: String, 
    val selectedIcon: ImageVector? = null, 
    val unselectedIcon: ImageVector? = null
) {
    // Auth Screens
    object Welcome : Screen("welcome", "Bienvenido")
    object Login : Screen("login", "Iniciar Sesión")
    object Register : Screen("register", "Registrarse")
    
    // Main App Screens
    object Home : Screen("home", "Inicio", Icons.Filled.Home, Icons.Outlined.Home)
    object Learning : Screen("learning", "Aprender", Icons.Filled.School, Icons.Outlined.School)
    object Progress : Screen("progress", "Progreso", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp)
    object Profile : Screen("profile", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
    object Settings : Screen("settings", "Ajustes")
    object Lesson : Screen("lesson", "Lección")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KodeLearnNavigation(
    repository: KodeLearnRepository
) {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }
    
    // Mock user data - completely local
    val mockUser = remember {
        com.example.kodelearn.data.MockData.getDefaultUser()
    }
    
    val mainAppItems = listOf(
        Screen.Home,
        Screen.Learning,
        Screen.Progress,
        Screen.Profile
    )

    // Debug log
    println("KodeLearnNavigation: isLoggedIn = $isLoggedIn")
    
    if (isLoggedIn) {
        // Main App with Bottom Navigation
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    mainAppItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            label = { 
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Learning.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    SimpleHomeScreen()
                }
                composable(Screen.Learning.route) {
                    SimpleLearningScreen(
                        onNavigateToLesson = {
                            navController.navigate(Screen.Lesson.route)
                        }
                    )
                }
                composable(Screen.Progress.route) {
                    SimpleProgressScreen()
                }
                composable(Screen.Profile.route) {
                    SimpleProfileScreen(
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.Lesson.route) {
                    LessonScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onLessonComplete = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    } else {
        // Auth Screens
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = {
                        isLoggedIn = true
                        // No need to navigate, the UI will switch to main app
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    repository = repository,
                    onRegisterSuccess = {
                        isLoggedIn = true
                        // No need to navigate, the UI will switch to main app
                    }
                )
            }
        }
    }
}
