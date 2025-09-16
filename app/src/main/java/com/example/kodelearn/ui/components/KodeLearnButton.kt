package com.example.kodelearn.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kodelearn.ui.theme.PrimaryGreen
import com.example.kodelearn.ui.theme.SecondaryBlue

@Composable
fun KodeLearnButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: @Composable (() -> Unit)? = null
) {
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen,
            contentColor = Color.Black,
            disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
        )
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = SecondaryBlue,
            contentColor = Color.White,
            disabledContainerColor = SecondaryBlue.copy(alpha = 0.5f)
        )
        ButtonVariant.Outline -> ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryGreen
        )
    }

    when (variant) {
        ButtonVariant.Outline -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled,
            colors = colors,
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp
            )
        ) {
            ButtonContent(text = text, icon = icon)
        }
        else -> Button(
            onClick = onClick,
            modifier = modifier.height(56.dp),
            enabled = enabled,
            colors = colors,
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp
            )
        ) {
            ButtonContent(text = text, icon = icon)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: @Composable (() -> Unit)?
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Outline
}
