package fi.metropolia.movesense.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import com.ekn.gruzer.gaugelibrary.FullGauge
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.MultiGauge
import com.ekn.gruzer.gaugelibrary.Range
import fi.metropolia.movesense.view.measure.MeasureViewModel
import kotlin.math.*

@Composable
fun MovesenseGauge(measureViewModel: MeasureViewModel) {
    var pitch by rememberSaveable { mutableStateOf(0.0) }
    var roll by rememberSaveable { mutableStateOf(0.0) }
    val G = 9.81F
    val rpm by measureViewModel.rpm.observeAsState()

    val entriesX by measureViewModel.entriesX.observeAsState()
    val entriesY by measureViewModel.entriesY.observeAsState()
    val entriesZ by measureViewModel.entriesZ.observeAsState()

    fun calculateRotation() {
        val x = entriesX?.last()?.y ?: 0.0F
        val y = entriesY?.last()?.y ?: 0.0F
        val z = entriesZ?.last()?.y ?: 0.0F

        pitch = atan2(-x, sqrt(y * y + z * z)) * 180 / PI
        roll = atan2(y, z) * 180 / PI;

        //pitch = Math.toDegrees(asin(x.div(G)).toDouble())
        // roll = Math.toDegrees(atan2(y,z).toDouble())

        Log.d("angle", "$pitch, $roll")
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context: Context ->
                    val gauge = MultiGauge(context)

                    val range = Range()
                    range.color = Color.Red.hashCode()
                    range.from = -180.0
                    range.to = 180.0

                    val range2 = Range()
                    range2.color = Color.Yellow.hashCode()
                    range.from = -180.0
                    range.to = 180.0

                    gauge.minValue = -180.0
                    gauge.maxValue = 180.0
                    gauge.secondMinValue = -180.0
                    gauge.secondMaxValue = 180.0
                    gauge.addRange(range)
                    gauge.addSecondRange(range2)
                    calculateRotation()
                    gauge.value = pitch
                    gauge.secondValue = roll
                    gauge
                },
                update = { gauge ->
                    calculateRotation()
                    gauge.value = pitch
                    gauge.secondValue = roll
                    gauge.invalidate()
                }
            )
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("RPM: $rpm", style = MaterialTheme.typography.bodyLarge)
        }
    }
/*
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            val gauge = FullGauge(context)

            val range = Range()
            range.color = Color.Red.hashCode()
            range.from = -180.0
            range.to = 180.0

            val range2 = Range()
            range2.color = Color.Yellow.hashCode()
            range.from = -180.0
            range.to = 180.0

            /*val range3 = Range()
            range3.color = Color.Green.hashCode()
            range3.from = 100.0
            range3.to = 150.0*/
            gauge.minValue = -180.0
            gauge.maxValue = 180.0
            gauge.
            gauge.setNeedleColor(Color.Black.hashCode())
            //gauge.secondMinValue = -180.0
           // gauge.secondMaxValue = 180.0
            calculateRotation()
            gauge.addRange(range)
            //gauge.addSecondRange(range2)
            gauge.value = pitch + roll
           // gauge.secondValue = roll
            gauge
        },
        update = { gauge ->
            calculateRotation()
            gauge.value = pitch
            //gauge.secondValue = roll
            gauge.invalidate()
        }
    )
*/
}
