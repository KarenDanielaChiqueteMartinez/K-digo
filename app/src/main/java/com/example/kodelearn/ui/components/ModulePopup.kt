package com.example.kodelearn.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kodelearn.data.database.entities.Module
import com.example.kodelearn.data.database.entities.Progress
import com.example.kodelearn.data.repository.ModuleWithProgress
import com.example.kodelearn.ui.theme.*

@Composable
fun ModulePopup(
    moduleWithProgress: ModuleWithProgress?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onStartModule: (ModuleWithProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible && moduleWithProgress != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            ModulePopupContent(
                moduleWithProgress = moduleWithProgress,
                onDismiss = onDismiss,
                onStartModule = onStartModule,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModulePopupContent(
    moduleWithProgress: ModuleWithProgress,
    onDismiss: () -> Unit,
    onStartModule: (ModuleWithProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    val responsiveDimensions = rememberResponsiveDimensions()
    val responsivePadding = rememberResponsivePadding()
    val module = moduleWithProgress.module
    val progress = moduleWithProgress.progress
    
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    val progressPercentage = progress?.progressPercentage ?: 0f
    
    // Animation states
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(responsiveDimensions.animationDuration),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f * alpha)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(responsiveDimensions.popupWidth)
                .padding(responsivePadding.large)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(responsivePadding.extraLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Module icon and status
                ModuleIcon(
                    module = module,
                    progress = progress,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Module title
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Module description
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Module stats
                ModuleStats(
                    module = module,
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                ModuleActions(
                    moduleWithProgress = moduleWithProgress,
                    onStartModule = onStartModule,
                    onDismiss = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ModuleIcon(
    module: Module,
    progress: Progress?,
    modifier: Modifier = Modifier
) {
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    val progressPercentage = progress?.progressPercentage ?: 0f
    
    val backgroundColor = when {
        isCompleted -> ModuleCompleted
        isLocked -> ModuleLocked.copy(alpha = 0.7f)
        else -> ModuleAvailable
    }
    
    val iconColor = when {
        isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else -> Color.White
    }
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            isCompleted -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completado",
                    tint = iconColor,
                    modifier = Modifier.size(40.dp)
                )
            }
            isLocked -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Comenzar",
                    tint = iconColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun ModuleStats(
    module: Module,
    progress: Progress?,
    modifier: Modifier = Modifier
) {
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    val progressPercentage = progress?.progressPercentage ?: 0f
    val lessonsCompleted = progress?.lessonsCompleted ?: 0
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Lessons stat
        StatItem(
            icon = Icons.Default.Book,
            label = "Lecciones",
            value = "${lessonsCompleted}/${module.totalLessons}",
            color = PrimaryGreen
        )
        
        // XP reward
        StatItem(
            icon = Icons.Default.Star,
            label = "XP",
            value = module.xpReward.toString(),
            color = XPColor
        )
        
        // Progress
        if (!isLocked && progressPercentage > 0) {
            StatItem(
                icon = Icons.Default.TrendingUp,
                label = "Progreso",
                value = "${progressPercentage.toInt()}%",
                color = SuccessColor
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ModuleActions(
    moduleWithProgress: ModuleWithProgress,
    onStartModule: (ModuleWithProgress) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val module = moduleWithProgress.module
    val progress = moduleWithProgress.progress
    
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    
    when {
        isCompleted -> {
            // Completed module - show review button
            Button(
                onClick = {
                    onStartModule(moduleWithProgress)
                    onDismiss()
                },
                modifier = modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ModuleCompleted
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Repasar")
            }
        }
        isLocked -> {
            // Locked module - show unlock requirements
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = WarningColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completa los módulos anteriores para desbloquear este contenido",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Entendido")
                }
            }
        }
        else -> {
            // Available module - show start button
            Button(
                onClick = {
                    onStartModule(moduleWithProgress)
                    onDismiss()
                },
                modifier = modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ModuleAvailable
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (progress?.progressPercentage != null && progress.progressPercentage > 0) 
                        "Continuar" 
                    else 
                        "Comenzar"
                )
            }
        }
    }
}
