package com.video.entitlement.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1677FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF722ED1),
    tertiary = Color(0xFF13C2C2),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    error = Color(0xFFFF4D4F),
)

@Composable
fun VideoEntitlementTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColorScheme, content = content)
}
