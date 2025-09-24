package com.example.kodelearn.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Extension functions and composables for responsive design
 */

@Composable
fun rememberResponsiveDimensions(): ResponsiveDimensions {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    
    return ResponsiveDimensions(
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        isTablet = screenWidth >= 600,
        isLandscape = screenWidth > screenHeight
    )
}

data class ResponsiveDimensions(
    val screenWidth: Int,
    val screenHeight: Int,
    val isTablet: Boolean,
    val isLandscape: Boolean
) {
    // Module size based on screen size
    val moduleSize: Dp
        get() = when {
            isTablet -> 100.dp
            else -> 80.dp
        }
    
    // Path spacing based on screen size
    val pathSpacing: Dp
        get() = when {
            isTablet -> 120.dp
            else -> 100.dp
        }
    
    // Popup width based on screen size
    val popupWidth: Float
        get() = when {
            isTablet -> 0.6f
            else -> 0.9f
        }
    
    // Animation duration based on screen size
    val animationDuration: Int
        get() = when {
            isTablet -> 400
            else -> 300
        }
}

/**
 * Responsive padding that adapts to screen size
 */
@Composable
fun rememberResponsivePadding(): ResponsivePadding {
    val dimensions = rememberResponsiveDimensions()
    
    return ResponsivePadding(
        small = if (dimensions.isTablet) 8.dp else 4.dp,
        medium = if (dimensions.isTablet) 16.dp else 12.dp,
        large = if (dimensions.isTablet) 24.dp else 16.dp,
        extraLarge = if (dimensions.isTablet) 32.dp else 24.dp
    )
}

data class ResponsivePadding(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)
