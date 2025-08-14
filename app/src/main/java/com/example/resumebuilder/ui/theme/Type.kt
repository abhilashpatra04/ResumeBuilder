package com.example.resumebuilder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleMedium   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodyMedium    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelLarge    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
)

object ResumeText {
    val Heading  = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val Subhead  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    val Body     = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val Muted    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, color = TextMuted)
}
