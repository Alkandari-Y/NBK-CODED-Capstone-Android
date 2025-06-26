
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
fun Transfer2FillIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val path = PathParser().parsePathString(
        "M7.269 3.11c.974-.974 2.617-.34 2.725.991l.006.14V20a1.5 1.5 0 0 1-2.993.144L7 20V7.621l-1.44 1.44a1.5 1.5 0 0 1-2.224-2.008l.103-.114zM15.5 2.5a1.5 1.5 0 0 1 1.493 1.356L17 4v12.379l1.44-1.44a1.5 1.5 0 0 1 2.224 2.008l-.103.114l-3.83 3.829c-.974.974-2.617.34-2.725-.991l-.006-.14V4a1.5 1.5 0 0 1 1.5-1.5"
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
        contentDescription = "Transfer2Fill Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
