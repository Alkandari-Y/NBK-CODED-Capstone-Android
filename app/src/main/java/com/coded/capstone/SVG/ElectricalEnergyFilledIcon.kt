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
fun ElectricalEnergyFilledIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val pathData = PathParser().parsePathString(
        "M362.667 42.667L325.51 192h106.667L171.17 469.334l58.389-234.667h-85.333l47.773-192z"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 512f,
        viewportHeight = 512f
    ).apply {
        addPath(
            pathData = pathData,
            fill = SolidColor(color),
            fillAlpha = 1.0f
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(image = vector),
        contentDescription = "Electrical Energy Filled Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
