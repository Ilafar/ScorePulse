package com.score.pulse.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// ScorePulse Dark Neon — raw palette values from the design spec.
val NeonEmerald = Color(0xFF4EDEA3)
val NeonEmeraldDim = Color(0xFF4EDEA3)
val OnNeonEmerald = Color(0xFF003824)
val EmeraldContainer = Color(0xFF10B981)
val OnEmeraldContainer = Color(0xFF00422B)
val InverseEmerald = Color(0xFF006C49)
val EmeraldFixed = Color(0xFF6FFBBE)
val OnEmeraldFixed = Color(0xFF002113)
val OnEmeraldFixedVariant = Color(0xFF005236)

val ElectricCyan = Color(0xFF4CD7F6)
val OnElectricCyan = Color(0xFF003640)
val CyanContainer = Color(0xFF03B5D3)
val OnCyanContainer = Color(0xFF00424E)
val CyanFixed = Color(0xFFACEDFF)
val OnCyanFixed = Color(0xFF001F26)
val OnCyanFixedVariant = Color(0xFF004E5C)

val NeonCrimson = Color(0xFFFFB0CD)
val OnNeonCrimson = Color(0xFF640039)
val CrimsonContainer = Color(0xFFFF72B1)
val OnCrimsonContainer = Color(0xFF740043)
val CrimsonFixed = Color(0xFFFFD9E4)
val OnCrimsonFixed = Color(0xFF3E0022)
val OnCrimsonFixedVariant = Color(0xFF8C0053)

val ObsidianSurface = Color(0xFF10141A)
val ObsidianBright = Color(0xFF353940)
val ObsidianContainerLowest = Color(0xFF0A0E14)
val ObsidianContainerLow = Color(0xFF181C22)
val ObsidianContainer = Color(0xFF1C2026)
val ObsidianContainerHigh = Color(0xFF262A31)
val ObsidianContainerHighest = Color(0xFF31353C)
val ObsidianVariant = Color(0xFF31353C)

val CrispWhite = Color(0xFFDFE2EB)
val MutedSlate = Color(0xFFBBCABF)
val InverseOnSurface = Color(0xFF2D3137)

val HudOutline = Color(0xFF86948A)
val HudOutlineVariant = Color(0xFF3C4A42)

val NeonError = Color(0xFFFFB4AB)
val OnNeonError = Color(0xFF690005)
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

/** The single dark ColorScheme every ScorePulse screen reads via `MaterialTheme.colorScheme`. */
val ScorePulseDarkColorScheme: ColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = OnNeonEmerald,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    inversePrimary = InverseEmerald,
    secondary = ElectricCyan,
    onSecondary = OnElectricCyan,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = OnCyanContainer,
    tertiary = NeonCrimson,
    onTertiary = OnNeonCrimson,
    tertiaryContainer = CrimsonContainer,
    onTertiaryContainer = OnCrimsonContainer,
    background = ObsidianSurface,
    onBackground = CrispWhite,
    surface = ObsidianSurface,
    onSurface = CrispWhite,
    surfaceVariant = ObsidianVariant,
    onSurfaceVariant = MutedSlate,
    surfaceTint = NeonEmerald,
    inverseSurface = CrispWhite,
    inverseOnSurface = InverseOnSurface,
    error = NeonError,
    onError = OnNeonError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = HudOutline,
    outlineVariant = HudOutlineVariant,
    surfaceBright = ObsidianBright,
    surfaceContainer = ObsidianContainer,
    surfaceContainerHigh = ObsidianContainerHigh,
    surfaceContainerHighest = ObsidianContainerHighest,
    surfaceContainerLow = ObsidianContainerLow,
    surfaceContainerLowest = ObsidianContainerLowest,
    surfaceDim = ObsidianSurface,
    primaryFixed = EmeraldFixed,
    primaryFixedDim = NeonEmeraldDim,
    onPrimaryFixed = OnEmeraldFixed,
    onPrimaryFixedVariant = OnEmeraldFixedVariant,
    secondaryFixed = CyanFixed,
    secondaryFixedDim = ElectricCyan,
    onSecondaryFixed = OnCyanFixed,
    onSecondaryFixedVariant = OnCyanFixedVariant,
    tertiaryFixed = CrimsonFixed,
    tertiaryFixedDim = NeonCrimson,
    onTertiaryFixed = OnCrimsonFixed,
    onTertiaryFixedVariant = OnCrimsonFixedVariant,
)
