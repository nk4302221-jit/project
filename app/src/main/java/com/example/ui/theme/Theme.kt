package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = ShopPrimaryDark,
  onPrimary = ShopOnPrimaryDark,
  primaryContainer = ShopPrimaryContainerDark,
  onPrimaryContainer = ShopOnPrimaryContainerDark,
  secondary = ShopSecondaryDark,
  onSecondary = ShopOnSecondaryDark,
  secondaryContainer = ShopSecondaryContainerDark,
  onSecondaryContainer = ShopOnSecondaryContainerDark,
  tertiary = ShopTertiaryDark,
  onTertiary = ShopOnTertiaryDark,
  background = ShopBackgroundDark,
  surface = ShopSurfaceDark,
  surfaceVariant = ShopSurfaceVariantDark,
  onBackground = ShopOnSurfaceDark,
  onSurface = ShopOnSurfaceDark,
  onSurfaceVariant = ShopOnSurfaceVariantDark,
  outline = ShopOutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = ShopPrimaryLight,
  onPrimary = ShopOnPrimaryLight,
  primaryContainer = ShopPrimaryContainerLight,
  onPrimaryContainer = ShopOnPrimaryContainerLight,
  secondary = ShopSecondaryLight,
  onSecondary = ShopOnSecondaryLight,
  secondaryContainer = ShopSecondaryContainerLight,
  onSecondaryContainer = ShopOnSecondaryContainerLight,
  tertiary = ShopTertiaryLight,
  onTertiary = ShopOnTertiaryLight,
  background = ShopBackgroundLight,
  surface = ShopSurfaceLight,
  surfaceVariant = ShopSurfaceVariantLight,
  onBackground = ShopOnSurfaceLight,
  onSurface = ShopOnSurfaceLight,
  onSurfaceVariant = ShopOnSurfaceVariantLight,
  outline = ShopOutlineLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun ShopWaveTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MyApplicationTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}


