
package com.afraz.minimalflipclock

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

        // Keep the display awake while this app is visible.
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        // Lock the clock interface to landscape orientation.
        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Hide Android system bars.
        val controller = WindowCompat.getInsetsController(
            window,
            window.decorView
        )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            ClockScreen()
        }
    }
}

@Composable
fun ClockScreen() {

    // Read the tablet's local system time.
    val currentTime by produceState(
        initialValue = LocalTime.now()
    ) {
        while (true) {
            value = LocalTime.now()
            delay(250)
        }
    }

    val hour = currentTime.hour
    val displayHour = (hour % 12).let {
        if (it == 0) 12 else it
    }

    val hours = displayHour.toString().padStart(2, '0')
    val minutes = currentTime.minute.toString().padStart(2, '0')
    val period = if (hour < 12) "AM" else "PM"

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

            ClockCard(
                value = hours,
                label = period,
                fontSize = clockFontSize,
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
            )

            ClockCard(
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
fun ClockCard(
    value: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Box(
        modifier = modifier
            .background(
                Color(0xFF1A1A1A),
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = value,
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
