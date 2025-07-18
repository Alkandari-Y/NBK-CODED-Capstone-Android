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
fun QueenCrownIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    val path = PathParser().parsePathString(
        "M188.28 59.47c-19.086 0-34.56 15.468-34.56 34.56c0 16.077 10.983 29.57 25.843 33.44l-35.344 81.936c15.877 2.885 27.905 16.784 27.905 33.5c0 18.806-15.23 34.063-34.03 34.063c-18.802 0-34.032-15.258-34.032-34.064c0-13.37 7.703-24.924 18.906-30.5l-50.814-79.22c8.007-5.82 13.22-15.24 13.22-25.905c0-17.693-14.314-32.06-32-32.06c-17.688 0-32.032 14.37-32.032 32.06c0 17.693 14.344 32.032 32.03 32.032c.734 0 1.468-.014 2.188-.062l41.907 227h316l41.936-227c.72.048 1.455.063 2.188.063c17.686 0 32.03-14.34 32.03-32.032c0-17.693-14.344-32.06-32.03-32.06c-17.687 0-32.03 14.37-32.03 32.06c-.002 10.723 5.286 20.187 13.373 26l-50.656 79.532c10.778 5.72 18.126 17.04 18.126 30.094c0 18.806-15.23 34.063-34.03 34.063s-34.032-15.258-34.032-34.064c0-17.11 12.602-31.267 29.03-33.687l-34.75-81.532c15.275-3.577 26.657-17.287 26.657-33.657c0-19.094-15.474-34.56-34.56-34.56c-19.09 0-34.564 15.468-34.564 34.56c0 14.798 9.308 27.415 22.375 32.345L268 202.345c14.62 4.52 25.25 18.112 25.25 34.218c0 19.796-16.053 35.843-35.844 35.843c-19.79 0-35.812-16.047-35.812-35.844c0-15.158 9.403-28.102 22.687-33.343l-44.124-76.72c13.234-4.845 22.688-17.552 22.688-32.47c0-19.094-15.475-34.56-34.563-34.56zM97.438 384.936c-23.978 3.763-22.86 39.844 4.188 39.844h6.656l.064.345h294.28l.063-.344h7.625c26.034 0 27.88-35.928 4.313-39.842H97.437z"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 48.dp,
        defaultHeight = 48.dp,
        viewportWidth = 512f,
        viewportHeight = 512f
    ).apply {
        addPath(
            pathData = path,
            fill = SolidColor(color)
        )
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Queen Crown Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}
