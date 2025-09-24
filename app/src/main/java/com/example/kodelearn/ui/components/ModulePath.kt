package com.example.kodelearn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kodelearn.data.repository.ModuleWithProgress
import com.example.kodelearn.ui.theme.PrimaryGreen
import com.example.kodelearn.ui.theme.SuccessColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulePath(
    modules: List<ModuleWithProgress>,
    onModuleClick: (ModuleWithProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    // Safety check to prevent crashes
    if (modules.isEmpty()) {
        return
    }
    
    // Animation for each module entrance
    val moduleAnimations = modules.mapIndexed { index, _ ->
        remember { Animatable(0f) }
    }
    
    // Staggered entrance animation
    LaunchedEffect(modules.size) {
        moduleAnimations.forEachIndexed { index, animatable ->
            delay(index * 150L) // Staggered delay
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }
    
    // Pulse animation for available modules
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        modules.forEachIndexed { index, moduleWithProgress ->
            val animationValue = if (index < moduleAnimations.size) moduleAnimations[index].value else 1f
            val isAvailable = !moduleWithProgress.module.isLocked && moduleWithProgress.progress?.isCompleted != true
            
            SimpleModule(
                moduleWithProgress = moduleWithProgress,
                onClick = { onModuleClick(moduleWithProgress) },
                animationValue = animationValue,
                pulseScale = if (isAvailable) pulseScale else 1f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleModule(
    moduleWithProgress: ModuleWithProgress,
    onClick: () -> Unit,
    animationValue: Float = 1f,
    pulseScale: Float = 1f
) {
    val module = moduleWithProgress.module
    val progress = moduleWithProgress.progress
    
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    
    val backgroundColor = when {
        isCompleted -> SuccessColor
        isLocked -> Color(0xFFE0E0E0)
        else -> PrimaryGreen
    }
    
    val textColor = when {
        isCompleted -> Color.White
        isLocked -> Color(0xFF757575)
        else -> Color.White
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .alpha(animationValue)
            .scale(
                scaleX = animationValue * pulseScale,
                scaleY = animationValue * pulseScale
            ),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${module.totalLessons} lecciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
            
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                isLocked -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = textColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Disponible",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}