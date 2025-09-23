package com.example.kodelearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kodelearn.data.database.entities.Module
import com.example.kodelearn.data.repository.ModuleWithProgress
import com.example.kodelearn.ui.theme.*

@Composable
fun WormModulePath(
    modules: List<ModuleWithProgress>,
    onModuleClick: (Module) -> Unit,
    onModulePreview: (Module) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(modules.size) { index ->
            val moduleWithProgress = modules[index]
            val isLast = index == modules.size - 1
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Module Circle
                WormModuleCircle(
                    module = moduleWithProgress.module,
                    progress = moduleWithProgress.progress,
                    onClick = { onModuleClick(moduleWithProgress.module) },
                    onPreview = { onModulePreview(moduleWithProgress.module) }
                )
                
                // Connection Line (except for last item)
                if (!isLast) {
                    Spacer(modifier = Modifier.width(8.dp))
                    WormConnectionLine(
                        isCompleted = moduleWithProgress.progress?.isCompleted ?: false,
                        isNextUnlocked = index < modules.size - 1 && modules[index + 1].module.isLocked.not()
                    )
                }
            }
        }
    }
}

@Composable
private fun WormModuleCircle(
    module: Module,
    progress: com.example.kodelearn.data.database.entities.Progress?,
    onClick: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = progress?.isCompleted ?: false
    val isUnlocked = !module.isLocked
    val progressPercentage = progress?.progressPercentage ?: 0f
    
    val backgroundColor = when {
        isCompleted -> SuccessColor
        isUnlocked -> PrimaryGreen
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    
    val borderColor = when {
        isCompleted -> SuccessColor
        isUnlocked -> PrimaryGreen
        else -> MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = modifier
            .size(60.dp)
            .clickable { 
                if (isUnlocked) onClick() else onPreview() 
            },
        contentAlignment = Alignment.Center
    ) {
        // Background Circle
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable { 
                    if (isUnlocked) onClick() else onPreview() 
                },
            contentAlignment = Alignment.Center
        ) {
            // Progress Circle (for unlocked modules)
            if (isUnlocked && !isCompleted) {
                CircularProgressIndicator(
                    progress = progressPercentage / 100f,
                    modifier = Modifier.size(56.dp),
                    color = PrimaryGreen,
                    strokeWidth = 3.dp,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }
            
            // Icon or Content
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completado",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                isUnlocked -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // Border
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun WormConnectionLine(
    isCompleted: Boolean,
    isNextUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = when {
        isCompleted && isNextUnlocked -> SuccessColor
        isCompleted -> SuccessColor.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    
    Box(
        modifier = modifier
            .width(40.dp)
            .height(4.dp)
            .background(
                color = lineColor,
                shape = RoundedCornerShape(2.dp)
            )
    )
}

@Composable
fun ModulePreviewDialog(
    module: Module,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = module.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(
                        icon = Icons.Default.School,
                        text = "${module.totalLessons} lecciones"
                    )
                    InfoChip(
                        icon = Icons.Default.Star,
                        text = "${module.xpReward} XP"
                    )
                }
            }
        },
        confirmButton = {
            if (!module.isLocked) {
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    )
                ) {
                    Text("Iniciar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
