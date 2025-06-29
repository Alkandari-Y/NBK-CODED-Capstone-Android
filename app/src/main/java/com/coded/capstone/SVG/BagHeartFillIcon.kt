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
fun BagHeartFillIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val pathData = PathParser().parsePathString(
        "M11.5 4v-.5a3.5 3.5 0 1 0-7 0V4H1v10a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V4zM8 1a2.5 2.5 0 0 1 2.5 2.5V4h-5v-.5A2.5 2.5 0 0 1 8 1m0 6.993c1.664-1.711 5.825 1.283 0 5.132c-5.825-3.85-1.664-6.843 0-5.132"
    ).toNodes()

    val imageVector = ImageVector.Builder(
        defaultWidth = 16.dp,
        defaultHeight = 16.dp,
        viewportWidth = 16f,
        viewportHeight = 16f
    ).apply {
        addPath(
            pathData = pathData,
            fill = SolidColor(color),
            fillAlpha = 1.0f
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(image = imageVector),
        contentDescription = "Bag Heart Fill Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
