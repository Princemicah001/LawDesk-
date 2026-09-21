package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LawDeskColorScheme = darkColorScheme(
    primary = GoldPremium,
    onPrimary = PureBlack,
    secondary = Gray300,
    onSecondary = PureWhite,
    tertiary = GoldDark,
    background = PureBlack,
    onBackground = PureWhite,
    surface = Gray900,
    onSurface = PureWhite,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    outline = Gray600
)

private val LawDeskLightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = PureWhite,
    secondary = Color(0xFF707074),
    onSecondary = PureBlack,
    tertiary = GoldPremium,
    background = OffWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    surfaceVariant = Gray100,
    onSurfaceVariant = PureBlack,
    outline = Gray300
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) LawDeskColorScheme else LawDeskLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
