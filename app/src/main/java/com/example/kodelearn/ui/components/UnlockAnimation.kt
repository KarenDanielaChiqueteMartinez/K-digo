package com.example.kodelearn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

/**
 * Animación de desbloqueo de módulo
 */
@Composable
fun UnlockAnimationDialog(
    moduleTitle: String,
    moduleDescription: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            UnlockAnimationContent(
                moduleTitle = moduleTitle,
                moduleDescription = moduleDescription,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun UnlockAnimationContent(
    moduleTitle: String,
    moduleDescription: String,
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var showStars by remember { mutableStateOf(false) }
    
    // Animaciones
    val scaleAnimation by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val alphaAnimation by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )
    
    val rotationAnimation by animateFloatAsState(
        targetValue = if (showStars) 360f else 0f,
        animationSpec = tween(2000, easing = EaseInOutCubic),
        label = "rotation"
    )
    
    // Efectos de partículas
    val particleAnimation = rememberInfiniteTransition(label = "particles")
    val particleOffset by particleAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particleOffset"
    )
    
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        delay(500)
        showStars = true
        delay(2000)
        onDismiss()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        // Partículas de fondo
        repeat(20) { index ->
            Particle(
                offset = particleOffset,
                delay = index * 100,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Contenido principal
        Card(
            modifier = Modifier
                .scale(scaleAnimation)
                .alpha(alphaAnimation)
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de desbloqueo con animación
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500),
                                    Color(0xFFFF8C00)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 4.dp,
                            color = Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Desbloqueado",
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scaleAnimation),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Estrellas animadas
                if (showStars) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .scale(scaleAnimation)
                                    .alpha(alphaAnimation),
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Título del módulo
                Text(
                    text = "¡Módulo Desbloqueado!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = moduleTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = moduleDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón de continuar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "¡Empezar a Aprender!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Particle(
    offset: Float,
    delay: Int,
    modifier: Modifier = Modifier
) {
    val particleAlpha by animateFloatAsState(
        targetValue = if (offset > 0.1f) 1f else 0f,
        animationSpec = tween(500),
        label = "particleAlpha"
    )
    
    val particleScale by animateFloatAsState(
        targetValue = if (offset > 0.1f) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "particleScale"
    )
    
    Box(
        modifier = modifier
            .alpha(particleAlpha)
            .scale(particleScale)
    ) {
        // Partículas dispersas por la pantalla
        val positions = listOf(
            Pair(0.1f, 0.2f),
            Pair(0.3f, 0.1f),
            Pair(0.7f, 0.3f),
            Pair(0.9f, 0.2f),
            Pair(0.2f, 0.8f),
            Pair(0.6f, 0.9f),
            Pair(0.8f, 0.7f),
            Pair(0.4f, 0.6f)
        )
        
        positions.forEach { (x, y) ->
            Box(
                modifier = Modifier
                    .offset(
                        x = (x * 300).dp,
                        y = (y * 600).dp
                    )
                    .size(8.dp)
                    .background(
                        color = Color(0xFFFFD700).copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Componente de módulo con estado de desbloqueo
 */
@Composable
fun ModuleCard(
    title: String,
    description: String,
    isUnlocked: Boolean,
    progress: Float = 0f,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardAlpha by animateFloatAsState(
        targetValue = if (isUnlocked) 1f else 0.6f,
        animationSpec = tween(300),
        label = "cardAlpha"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        }
                    )
                }
                
                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            if (isUnlocked && progress > 0f) {
                Spacer(modifier = Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Progreso: ${progress.toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onStartClick,
                enabled = isUnlocked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUnlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    }
                )
            ) {
                Text(
                    text = if (isUnlocked) "Comenzar" else "Bloqueado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
