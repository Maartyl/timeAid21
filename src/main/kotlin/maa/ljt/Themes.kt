package maa.ljt

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colors = darkColors(
      primary = Color(0xFF0d47a1),
      primaryVariant = Color(0xFF002171),
      secondary = Color(0xFF37474f),
      onPrimary = Color.White,
      onSecondary = Color.White,
    ),
    content = content
  )
}