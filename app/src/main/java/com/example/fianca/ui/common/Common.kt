package com.example.fianca.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.fianca.ui.theme.RedPrimary
import com.example.fianca.ui.theme.TextColor
import com.example.fianca.ui.theme.WhiteBackground
import com.example.fianca.ui.theme.WineSecondary

data class FreelancerDisplayInfo(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double
)

@Composable
fun FreelancerUI(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = RedPrimary,
            secondary = WineSecondary,
            background = WhiteBackground,
            surface = WhiteBackground,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = TextColor,
            onSurface = TextColor
        ),
        content = content
    )
}
