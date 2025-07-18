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
fun CurrencyExchangeIcon(modifier: Modifier = Modifier,color: Color = Color.Unspecified) {
    val path1 = PathParser().parsePathString(
        "M15.068 14.278c0-4.719-8.7-4.411-8.7-7.261c0-1.379 1.32-2.053 2.85-2.053c2.574 0 3.032 1.654 4.198 1.654c.824 0 1.223-.521 1.223-1.103c0-1.353-2.053-2.378-4.021-2.732V1.476a1.476 1.476 0 0 0-2.952 0v1.353c-2.147.489-3.992 1.978-3.992 4.404c0 4.532 8.698 4.349 8.698 7.532c0 1.103-1.193 2.207-3.155 2.207c-2.941 0-3.921-1.992-5.115-1.992c-.582 0-1.103.49-1.103 1.23c0 1.17 1.965 2.581 4.667 2.976l-.001.01v1.473a1.477 1.477 0 1 0 2.953 0v-1.473c0-.018-.008-.031-.009-.047c2.431-.453 4.459-2.039 4.459-4.871m8.828 11.598h-4.104c-.688 0-1.227-.327-1.227-.985c0-.661.539-.99 1.227-.99h2.876l-4.792-7.399c-.298-.449-.449-.775-.449-1.195c0-.571.57-.99 1.049-.99c.481 0 .958.21 1.378.839l5.36 8.058l5.362-8.058c.419-.629.897-.839 1.378-.839c.477 0 1.046.419 1.046.99c0 .42-.148.746-.448 1.195L27.76 23.9h2.875c.689 0 1.229.329 1.229.99c0 .658-.539.985-1.229.985h-4.102v2.126h4.102c.689 0 1.229.332 1.229.99s-.539.987-1.229.987h-4.102v4.611c0 .868-.539 1.41-1.319 1.41c-.778 0-1.317-.542-1.317-1.41v-4.611h-4.104c-.688 0-1.227-.329-1.227-.987s.539-.99 1.227-.99h4.104v-2.125z"
    ).toNodes()

    val path2 = PathParser().parsePathString(
        "M23.875 6.125L17 2l4.125 6.875L17 13h11V2zm-14.75 23.75L16 34l-4.125-6.875L16 23H5v11z"
    ).toNodes()

    val vector = ImageVector.Builder(
        defaultWidth = 36.dp,
        defaultHeight = 36.dp,
        viewportWidth = 36f,
        viewportHeight = 36f
    ).apply {
        addPath(pathData = path1, fill = SolidColor(color))
        addPath(pathData = path2, fill = SolidColor(color))
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Currency Exchange Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified // preserve original fill colors
    )
}
