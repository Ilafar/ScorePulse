package com.score.pulse.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import scorepulse.shared.generated.resources.Res
import scorepulse.shared.generated.resources.inter_medium
import scorepulse.shared.generated.resources.inter_regular
import scorepulse.shared.generated.resources.inter_semibold
import scorepulse.shared.generated.resources.plus_jakarta_sans_bold
import scorepulse.shared.generated.resources.plus_jakarta_sans_extrabold
import scorepulse.shared.generated.resources.plus_jakarta_sans_semibold
import org.jetbrains.compose.resources.Font as ResourceFont

@Composable
private fun interFamily(): FontFamily = FontFamily(
    ResourceFont(Res.font.inter_regular, FontWeight.Normal),
    ResourceFont(Res.font.inter_medium, FontWeight.Medium),
    ResourceFont(Res.font.inter_semibold, FontWeight.SemiBold),
)

@Composable
private fun plusJakartaSansFamily(): FontFamily = FontFamily(
    ResourceFont(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    ResourceFont(Res.font.plus_jakarta_sans_bold, FontWeight.Bold),
    ResourceFont(Res.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

@Composable
fun scorePulseTypography(): Typography {
    val heading = plusJakartaSansFamily()
    val body = interFamily()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.03).em,
        ),
        displayMedium = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 44.sp,
            lineHeight = 48.sp,
            letterSpacing = (-0.04).em,
        ),
        displaySmall = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.03).em,
        ),
        headlineLarge = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.02).em,
        ),
        headlineMedium = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.01).em,
        ),
        headlineSmall = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.04.em,
        ),
        labelMedium = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.06.em,
        ),
        labelSmall = TextStyle(
            fontFamily = heading,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.08.em,
        ),
    )
}
