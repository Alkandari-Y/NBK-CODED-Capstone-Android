package com.coded.capstone.SVG

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp




@Composable
fun CreditCardCloseIcon(modifier: Modifier = Modifier) {
    val paths = listOf(
        "M18.8984 15.0312C16.6884 15.0312 14.8984 16.8213 14.8984 19.0313C14.8984 21.2413 16.6884 23.0313 18.8984 23.0313C21.1084 23.0313 22.8984 21.2413 22.8984 19.0313C22.8984 16.8213 21.1084 15.0312 18.8984 15.0312ZM20.4984 20.6813C20.3484 20.8313 20.1584 20.9012 19.9684 20.9012C19.7784 20.9012 19.5884 20.8313 19.4384 20.6813L18.9084 20.1512L18.3584 20.7013C18.2084 20.8513 18.0184 20.9213 17.8284 20.9213C17.6384 20.9213 17.4484 20.8513 17.2984 20.7013C17.0084 20.4113 17.0084 19.9312 17.2984 19.6412L17.8484 19.0913L17.3184 18.5612C17.0284 18.2712 17.0284 17.7913 17.3184 17.5013C17.6084 17.2113 18.0884 17.2113 18.3784 17.5013L18.9084 18.0313L19.4084 17.5313C19.6984 17.2413 20.1784 17.2413 20.4684 17.5313C20.7584 17.8213 20.7584 18.3013 20.4684 18.5913L19.9684 19.0913L20.4984 19.6213C20.7884 19.9113 20.7884 20.3913 20.4984 20.6813Z",
        "M22 7.54844V7.99844C22 8.54844 21.55 8.99844 21 8.99844H3C2.45 8.99844 2 8.54844 2 7.99844V7.53844C2 5.24844 3.85 3.39844 6.14 3.39844H17.85C20.14 3.39844 22 5.25844 22 7.54844Z",
        "M2 11.4983V16.4583C2 18.7483 3.85 20.5983 6.14 20.5983H12.4C12.98 20.5983 13.48 20.1083 13.43 19.5283C13.29 17.9983 13.78 16.3383 15.14 15.0183C15.7 14.4683 16.39 14.0483 17.14 13.8083C18.39 13.4083 19.6 13.4583 20.67 13.8183C21.32 14.0383 22 13.5683 22 12.8783V11.4883C22 10.9383 21.55 10.4883 21 10.4883H3C2.45 10.4983 2 10.9483 2 11.4983ZM8 17.2483H6C5.59 17.2483 5.25 16.9083 5.25 16.4983C5.25 16.0883 5.59 15.7483 6 15.7483H8C8.41 15.7483 8.75 16.0883 8.75 16.4983C8.75 16.9083 8.41 17.2483 8 17.2483Z"
    )

    val vector = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        paths.forEach { pathString ->
            addPath(
                pathData = PathParser().parsePathString(pathString).toNodes(),
                fill = SolidColor(Color(0xFF8AAEBD))
            )
        }
    }.build()

    Icon(
        painter = rememberVectorPainter(vector),
        contentDescription = "Card Remove Icon",
        modifier = modifier.size(48.dp),
        tint = Color.Unspecified
    )
}