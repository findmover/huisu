package com.app.huisu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.huisu.ui.theme.BackgroundLight
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Sage400
import com.app.huisu.ui.theme.WarmSand

@Composable
fun ZenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLight,
                        Color(0xFFF8F6F2),
                        Color(0xFFFDFBF7)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-42).dp, y = (-68).dp)
                .clip(CircleShape)
                .background(Sage400.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 210.dp, y = 54.dp)
                .clip(CircleShape)
                .background(Mist400.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 280.dp, y = 520.dp)
                .clip(CircleShape)
                .background(WarmSand.copy(alpha = 0.25f))
        )

        content()
    }
}
