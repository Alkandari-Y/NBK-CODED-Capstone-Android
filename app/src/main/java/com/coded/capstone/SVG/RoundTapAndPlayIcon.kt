package com.coded.capstone.SVG


import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun RoundTapAndPlayIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val path = PathParser().parsePathString(
        "M3.14 16.09c-.6-.1-1.14.39-1.14 1c0 .49.36.9.85.98c2.08.36 3.72 2 4.08 4.08c.08.49.49.85.98.85c.61 0 1.09-.54 1-1.14a7 7 0 0 0-5.77-5.77M2 20v3h3c0-1.66-1.34-3-3-3m1.11-7.94c-.59-.06-1.11.4-1.11.99c0 .5.37.94.87.99c4.27.41 7.67 3.81 8.08 8.08c.05.5.48.88.99.88c.59 0 1.06-.51 1-1.1c-.51-5.2-4.63-9.32-9.83-9.84M17 1.01L7 1c-1.1 0-2 .9-2 2v7.37c.69.16 1.36.37 2 .64V5h10v13h-3.03c.52 1.25.84 2.59.95 4H17c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 26.dp,
        defaultHeight = 26.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        addPath(
            pathData = path,
            fill = SolidColor(color)
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Tap and Play Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
