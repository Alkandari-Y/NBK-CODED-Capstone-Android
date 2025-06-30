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
fun SeverityInfoIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val path = PathParser().parsePathString(
        "M12 6A6 6 0 1 1 0 6a6 6 0 0 1 12 0M6.75 3.75a.75.75 0 1 1-1.5 0a.75.75 0 0 1 1.5 0M6 5.25a.75.75 0 0 0-.75.75v2.25a.75.75 0 1 0 1.5 0V6A.75.75 0 0 0 6 5.25"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 12.dp,
        defaultHeight = 12.dp,
        viewportWidth = 12f,
        viewportHeight = 12f
    ).apply {
        addPath(
            pathData = path,
            fill = SolidColor(color)
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Severity Info Icon",
        modifier = modifier.size(12.dp),
        tint = Color.Unspecified
    )
}
