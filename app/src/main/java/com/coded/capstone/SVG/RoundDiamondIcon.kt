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
fun RoundDiamondIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val path = PathParser().parsePathString(
        "M12.16 3h-.32L9.21 8.25h5.58zm4.3 5.25h5.16l-2.07-4.14A2 2 0 0 0 17.76 3h-3.93zm4.92 1.5h-8.63V20.1zM11.25 20.1V9.75H2.62zM7.54 8.25L10.16 3H6.24a2 0 0 0-1.79 1.11L2.38 8.25z"
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
        contentDescription = "Round Diamond Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
