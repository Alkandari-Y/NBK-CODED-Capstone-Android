package com.coded.capstone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(

//                        Color(0xFF7ABEFA), // Center (light gray)
////                        Color(0xFF384650),  // Edge (dark gray)
////                        Color(0xFF383850),  // Edge (dark gray)
////                        Color(0xFF555D6B),
//                        Color(0xFF141818)


                         Color(0xFF555D6B), // Center (light gray)
                         Color(0xFF262C2C)  // Edge (dark gray)

//                         Color(0xFF7ABEFA), // Center (light gray)
//                        Color(0xFF384650),  // Edge (dark gray)
//                        Color(0xFF383850),  // Edge (dark gray)
//                        Color(0xFF555D6B),
//                         Color(0xFF141818)


//                         Color(0xFF555D6B), // Center (light gray)
//                         Color(0xFF262C2C)  // Edge (dark gray)


                    ),
                    center = Offset(200f, 200f),
                    radius = 1600f
                )
            )
    ) {
        content()
    }
} 