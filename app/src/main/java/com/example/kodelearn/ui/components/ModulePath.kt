package com.example.kodelearn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.example.kodelearn.data.database.entities.Module
import com.example.kodelearn.data.database.entities.Progress
import com.example.kodelearn.data.repository.ModuleWithProgress
import com.example.kodelearn.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulePath(
    modules: List<ModuleWithProgress>,
    onModuleClick: (ModuleWithProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val responsiveDimensions = rememberResponsiveDimensions()
    
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Animation for the path
    val infiniteTransition = rememberInfiniteTransition(label = "path")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(responsiveDimensions.animationDuration * 10, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pathAnimation"
    )
    
    // Module entrance animations
    val moduleAnimations = modules.mapIndexed { index, _ ->
        remember {
            Animatable(0f)
        }
    }
    
    LaunchedEffect(modules.size) {
        moduleAnimations.forEachIndexed { index, animatable ->
            delay(index * 200L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 600.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawSnakePath(
                modules = modules,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                animationOffset = animationOffset
            )
        }
        
        // Draw modules along the snake path
        modules.forEachIndexed { index, moduleWithProgress ->
            val position = calculateSnakeModulePosition(
                index = index,
                totalModules = modules.size,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                responsiveDimensions = responsiveDimensions
            )
            
            val animationValue = if (index < moduleAnimations.size) moduleAnimations[index].value else 1f
            
            PathModule(
                moduleWithProgress = moduleWithProgress,
                onClick = { onModuleClick(moduleWithProgress) },
                animationValue = animationValue,
                responsiveDimensions = responsiveDimensions,
                modifier = Modifier.offset(
                    x = position.x.dp,
                    y = position.y.dp
                )
            )
        }
    }
}

private fun DrawScope.drawSnakePath(
    modules: List<ModuleWithProgress>,
    screenWidth: Float,
    screenHeight: Float,
    animationOffset: Float
) {
    if (modules.isEmpty()) return
    
    // Calculate snake path positions
    val pathPoints = mutableListOf<Offset>()
    val moduleSpacing = screenHeight / (modules.size / 2 + 1)
    val sideMargin = 80f
    val centerX = screenWidth / 2
    
    modules.forEachIndexed { index, _ ->
        val row = index / 2
        val isLeft = index % 2 == 0
        
        val x = if (isLeft) sideMargin else screenWidth - sideMargin
        val y = 50f + (row * moduleSpacing)
        
        pathPoints.add(Offset(x, y))
    }
    
    // Draw connecting lines between modules
    for (i in 0 until pathPoints.size - 1) {
        val current = pathPoints[i]
        val next = pathPoints[i + 1]
        
        // Draw animated line
        drawLine(
            color = PathColor.copy(alpha = 0.6f),
            start = current,
            end = next,
            strokeWidth = 6.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(20f, 10f),
                animationOffset * 30f
            )
        )
        
        // Draw connection dots
        drawCircle(
            color = ConnectionDot,
            radius = 4.dp.toPx(),
            center = current
        )
        
        drawCircle(
            color = ConnectionGlow,
            radius = 8.dp.toPx(),
            center = current
        )
    }
    
    // Draw final dot
    if (pathPoints.isNotEmpty()) {
        val lastPoint = pathPoints.last()
        drawCircle(
            color = ConnectionDot,
            radius = 4.dp.toPx(),
            center = lastPoint
        )
        
        drawCircle(
            color = ConnectionGlow,
            radius = 8.dp.toPx(),
            center = lastPoint
        )
    }
}

private fun DrawScope.drawWormPath(
    modules: List<ModuleWithProgress>,
    screenWidth: Float,
    screenHeight: Float,
    animationOffset: Float
) {
    if (modules.isEmpty()) return
    
    val path = Path()
    val pathPaint = Paint().apply {
        color = PathColor.copy(alpha = 0.6f)
        style = PaintingStyle.Stroke
        strokeWidth = 8.dp.toPx()
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(20f, 10f),
            animationOffset * 30f
        )
    }
    
    val shadowPaint = Paint().apply {
        color = Color.Black.copy(alpha = 0.1f)
        style = PaintingStyle.Stroke
        strokeWidth = 12.dp.toPx()
    }
    
    // Create the curved path
    val startX = 50f
    val endX = screenWidth - 50f
    val controlY = screenHeight * 0.6f
    
    path.moveTo(startX, 50f)
    
    // Create a smooth S-curve
    path.cubicTo(
        x1 = startX + (endX - startX) * 0.3f,
        y1 = 50f,
        x2 = startX + (endX - startX) * 0.7f,
        y2 = controlY,
        x3 = endX,
        y3 = screenHeight - 100f
    )
    
    // Add some wavy motion
    val wavePath = Path()
    val wavePoints = mutableListOf<Offset>()
    
    // Create a simple curved path instead of using PathMeasure
    val pathLength = screenWidth - 100f
    val pathHeight = screenHeight - 150f
    
    for (i in 0..100) {
        val progress = i / 100f
        val x = 50f + progress * pathLength
        val baseY = 50f + progress * pathHeight
        
        // Add curve to the path
        val curveOffset = when {
            progress < 0.5f -> sin(progress * PI.toFloat()) * 80f
            else -> sin((1 - progress) * PI.toFloat()) * 80f
        }
        
        val waveOffset = sin(progress * 10f + animationOffset * 2 * PI.toFloat()) * 15f
        wavePoints.add(Offset(x, baseY + curveOffset + waveOffset))
    }
    
    if (wavePoints.isNotEmpty()) {
        wavePath.moveTo(wavePoints[0].x, wavePoints[0].y)
        for (i in 1 until wavePoints.size) {
            wavePath.lineTo(wavePoints[i].x, wavePoints[i].y)
        }
    }
    
    // Draw shadow first
    drawPath(wavePath, Color.Black.copy(alpha = 0.1f), style = Stroke(width = 12.dp.toPx()))
    // Draw main path
    drawPath(wavePath, PathColor.copy(alpha = 0.6f), style = Stroke(width = 8.dp.toPx()))
    
    // Draw connection dots
    val dotPaint = Paint().apply {
        color = ConnectionDot
        style = PaintingStyle.Fill
    }
    
    modules.forEachIndexed { index, _ ->
        val position = calculateSnakeModulePosition(
            index = index,
            totalModules = modules.size,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            responsiveDimensions = ResponsiveDimensions(
                screenWidth = screenWidth.toInt(),
                screenHeight = screenHeight.toInt(),
                isTablet = false,
                isLandscape = false
            )
        )
        
        // Draw connection dot
        drawCircle(
            color = ConnectionDot,
            radius = 4.dp.toPx(),
            center = Offset(position.x, position.y)
        )
        
        // Draw connection dot glow
        drawCircle(
            color = ConnectionGlow,
            radius = 8.dp.toPx(),
            center = Offset(position.x, position.y)
        )
    }
}

private fun calculateSnakeModulePosition(
    index: Int,
    totalModules: Int,
    screenWidth: Float,
    screenHeight: Float,
    responsiveDimensions: ResponsiveDimensions
): Offset {
    val moduleSpacing = screenHeight / (totalModules / 2 + 1)
    val sideMargin = if (responsiveDimensions.isTablet) 100f else 80f
    
    val row = index / 2
    val isLeft = index % 2 == 0
    
    val x = if (isLeft) sideMargin else screenWidth - sideMargin
    val y = 50f + (row * moduleSpacing)
    
    return Offset(x, y)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PathModule(
    moduleWithProgress: ModuleWithProgress,
    onClick: () -> Unit,
    animationValue: Float,
    responsiveDimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    val module = moduleWithProgress.module
    val progress = moduleWithProgress.progress
    
    val isCompleted = progress?.isCompleted == true
    val isLocked = module.isLocked && progress == null
    val progressPercentage = progress?.progressPercentage ?: 0f
    
    // Module colors based on state with Candy Crush variety
    val candyColors = listOf(
        CandyRed, CandyOrange, CandyYellow, 
        CandyGreen, CandyBlue, CandyPurple, CandyPink
    )
    val moduleColorIndex = module.order % candyColors.size
    
    val backgroundColor = when {
        isCompleted -> ModuleCompleted
        isLocked -> ModuleLocked.copy(alpha = 0.7f)
        else -> candyColors[moduleColorIndex]
    }
    
    val borderColor = when {
        isCompleted -> SuccessColor
        isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> PrimaryGreen
    }
    
    val textColor = when {
        isLocked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        else -> Color.White
    }
    
    Card(
        modifier = modifier
            .size(responsiveDimensions.moduleSize)
            .clip(RoundedCornerShape(20.dp))
            .scale(animationValue)
            .alpha(animationValue),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLocked) 2.dp else 8.dp
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Module number
                Text(
                    text = module.order.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 16.sp
                )
                
                // Lock icon for locked modules
                if (isLocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔒",
                        fontSize = 12.sp
                    )
                }
                
                // Progress indicator for active modules
                if (!isLocked && progressPercentage > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${progressPercentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }
            
            // Completion checkmark
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = SuccessColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
