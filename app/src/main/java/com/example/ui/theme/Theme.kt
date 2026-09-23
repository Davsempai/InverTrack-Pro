package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val InverTrackColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    primaryContainer = DeepGreenContainer,
    onPrimaryContainer = LightMintGlow,
    secondary = BrightMint,
    onSecondary = Color.Black,
    secondaryContainer = DeepGreenContainer,
    onSecondaryContainer = PaleMint,
    tertiary = AccentLime,
    onTertiary = Color.Black,
    background = BlackBackground,
    onBackground = TextPrimary,
    surface = BlackSurface,
    onSurface = TextPrimary,
    surfaceVariant = BlackCard,
    onSurfaceVariant = TextSecondary,
    outline = BlackCardBorder,
    outlineVariant = DividerGreen,
    error = SignalRed,
    onError = Color.White,
    errorContainer = SignalRedDark,
    onErrorContainer = SignalRedGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Forced false to guarantee the user's requested black & green aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InverTrackColorScheme,
        typography = Typography,
        content = content
    )
}
