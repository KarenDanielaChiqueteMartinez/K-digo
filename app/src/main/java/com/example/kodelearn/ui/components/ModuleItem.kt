package com.example.kodelearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kodelearn.data.database.entities.Module
import com.example.kodelearn.data.database.entities.Progress
import com.example.kodelearn.ui.theme.*

@Composable
fun ModuleItem(
    module: Module,
    progress: Progress?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    val progressPercentage = progress?.progressPercentage ?: 0f
    
    val containerColor = when {
        isCompleted -> SuccessColor.copy(alpha = 0.15f)
        isLocked -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val iconColor = when {
        isCompleted -> SuccessColor
        isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else -> PrimaryGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLocked) 1.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Module Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                    isLocked -> Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    else -> Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Module Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLocked) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${progress?.lessonsCompleted ?: 0}/${module.totalLessons} lecciones",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                if (!isLocked && progressPercentage > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ProgressBarBackground)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressPercentage / 100f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ProgressBarFill)
                        )
                    }
                }
            }
            
            // Progress Percentage
            if (!isLocked && progressPercentage > 0) {
                Text(
                    text = "${progressPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) SuccessColor else PrimaryGreen
                )
            }
        }
    }
}
