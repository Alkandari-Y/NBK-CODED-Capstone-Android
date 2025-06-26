import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun RoundTransferVerticalBoldIcon(modifier: Modifier = Modifier) {
    val vector = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(12f, 2f)
            arcToRelative(10f, 10f, 0f, true, true, 0f, 20f)
            arcToRelative(10f, 10f, 0f, true, true, 0f, -20f)

            moveTo(14.75f, 17f)
            verticalLineTo(9f)
            lineTo(16.435f, 10.93f)
            lineTo(17.565f, 9.944f) // simulate curve with line
            lineTo(14.565f, 6.507f)
            lineTo(13.25f, 7f)
            verticalLineTo(17f)
            lineTo(14.75f, 17f)

            moveTo(6.507f, 12.997f)
            lineTo(7.565f, 13.069f)
            lineTo(9.25f, 15f)
            verticalLineTo(7f)
            lineTo(10.75f, 7f)
            verticalLineTo(17f)
            lineTo(9.435f, 17.493f)
            lineTo(6.435f, 14.056f)
            lineTo(6.507f, 12.997f)
        }
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Transfer Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
