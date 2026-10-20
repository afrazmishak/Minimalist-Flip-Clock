
package com.afraz.minimalflipclock

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import java.time.LocalTime

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent the tablet from automatically sleeping.
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        // Landscape orientation.
        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Immersive fullscreen mode.
        val controller = WindowCompat.getInsetsController(
            window,
            window.decorView
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            FlipClockScreen()
        }
    }
}

@Composable
fun FlipClockScreen() {

    // Read local tablet time
    val currentTime by produceState(
        initialValue = LocalTime.now()
    ) {
        while (true) {
            value = LocalTime.now()
            delay(250)
        }
    }

    val hour24 = currentTime.hour
    val hour12 = (hour24 % 12).let {
        if (it == 0) 12 else it
    }

    val hours = hour12.toString().padStart(2, '0')
    val minutes = currentTime.minute.toString().padStart(2, '0')
    val period = if (hour24 < 12) "AM" else "PM"

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val cardHeight = maxHeight * 0.78f
        val clockFontSize = (maxWidth.value * 0.19f).sp

        Row(
            modifier = Modifier.fillMaxWidth(0.94f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            FlipClockCard(
                value = hours,
                label = period,
                fontSize = clockFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
            )

            FlipClockCard(
                value = minutes,
                fontSize = clockFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
            )
        }
    }
}

@Composable
fun FlipClockCard(
    value: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    label: String? = null
) {

    var displayedValue by remember {
        mutableStateOf(value)
    }

    var previousValue by remember {
        mutableStateOf(value)
    }

    val rotation = remember { Animatable(0f) }

    // Animate only when the displayed time changes.
    LaunchedEffect(value) {
        if (value != displayedValue) {
            previousValue = displayedValue
            rotation.snapTo(0f)

            rotation.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = 700)
            )

            displayedValue = value
            rotation.snapTo(0f)
        }
    }

    val angle = rotation.value

    // First half: outgoing number
    // Second half: incoming number
    val visibleValue = when {
        angle == 0f -> displayedValue
        angle < 90f -> previousValue
        else -> value
    }

    val visibleRotation = when {
        angle < 90f -> -angle
        else -> 180f - angle
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = visibleRotation
                        cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = visibleValue,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF5F5F5),
            maxLines = 1
        )

        if (label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.LightGray,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }

        // Thin line resembling a flip clock.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF303030))
        )
    }
}
