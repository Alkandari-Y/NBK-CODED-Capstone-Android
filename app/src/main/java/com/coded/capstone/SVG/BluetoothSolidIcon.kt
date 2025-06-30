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
fun BluetoothSolidIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val path = PathParser().parsePathString(
        "M6.272.142a1 1 0 0 1 1.042.083l4.404 3.213A1 1 0 0 1 11.65 5.1L8.542 7l3.108 1.9a1 1 0 0 1 .068 1.662l-4.404 3.213a1 1 0 0 1-1.59-.808V8.722l-2.33 1.426A1 1 0 0 1 2.35 8.442L4.708 7L2.35 5.558a1 1 0 1 1 1.043-1.706l2.332 1.426V1.033a1 1 0 0 1 .547-.891m1.453 8.703l1.606.982L7.725 11zm0-3.69V3.001L9.33 4.173z"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 14.dp,
        defaultHeight = 14.dp,
        viewportWidth = 14f,
        viewportHeight = 14f
    ).apply {
        addPath(
            pathData = path,
            fill = SolidColor(color)
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Bluetooth Solid Icon",
        modifier = modifier.size(14.dp),
        tint = Color.Unspecified
    )
}
