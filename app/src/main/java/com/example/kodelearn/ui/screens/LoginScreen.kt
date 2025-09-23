package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.components.KodeLearnButton
import com.example.kodelearn.ui.theme.*
import com.example.kodelearn.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    repository: KodeLearnRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle successful login - Direct approach
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            println("LoginScreen: Login successful, calling onLoginSuccess")
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        LoginHeader()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Login Form
        LoginForm(
            email = uiState.email,
            password = uiState.password,
            isPasswordVisible = uiState.isPasswordVisible,
            onEmailChange = viewModel::updateEmail,
            onPasswordChange = viewModel::updatePassword,
            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
            onLogin = viewModel::loginUser
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Error Message
        if (uiState.errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorColor,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Register Link
        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = PrimaryGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "App Icon",
                tint = PrimaryGreen,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "¡Bienvenido de vuelta!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Inicia sesión para continuar aprendiendo",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit
) {
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                placeholder = { Text("tu@email.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                placeholder = { Text("Tu contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Login Button
            KodeLearnButton(
                text = "Iniciar sesión",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    KodeLearnTheme {
        LoginScreenContent()
    }
}

@Composable
private fun LoginScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginHeader()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LoginForm(
            email = "",
            password = "",
            isPasswordVisible = false,
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {}
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextButton(
            onClick = { }
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
