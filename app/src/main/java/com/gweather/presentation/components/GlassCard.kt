package com.gweather.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.gweather.ui.theme.White04
import com.gweather.ui.theme.White07

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    backgroundColor: Color = White04,
    borderColor: Color = White07,
    content: @Composable BoxScope.() -> Unit
) {
    val resolvedShape = shape ?: MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .clip(resolvedShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, resolvedShape),
        content = content
    )
}
