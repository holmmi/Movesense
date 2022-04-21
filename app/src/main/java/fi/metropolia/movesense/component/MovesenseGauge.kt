package fi.metropolia.movesense.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.ekn.gruzer.gaugelibrary.FullGauge
import com.ekn.gruzer.gaugelibrary.Range
import fi.metropolia.movesense.view.measure.MeasureViewModel
import kotlin.math.*

@Composable
fun MovesenseGauge(measureViewModel: MeasureViewModel) {
    var angle by rememberSaveable { mutableStateOf(0.0) }
    var rad by rememberSaveable { mutableStateOf(0F) }

    val entriesX by measureViewModel.entriesX.observeAsState()
    val entriesY by measureViewModel.entriesX.observeAsState()
    val entriesZ by measureViewModel.entriesX.observeAsState()

    fun calculateRotation() {
        val x = entriesX?.last()?.y?.div(10) ?: 0.0F
        val y = entriesY?.last()?.y?.div(10) ?: 0.0F
        val z = entriesZ?.last()?.y?.div(10) ?: 0.0F

        rad = z/(sqrt(x.pow(2) + y.pow(2) + z.pow(2)))

        angle = acos(rad) * 180/Math.PI
        Log.d("angle", angle.toString())
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            val gauge = FullGauge(context)

            val range = Range()
            range.color = Color.Red.hashCode()
            range.from = 0.0
            range.to = 360.0

            /* val range2 = Range()
             range2.color = Color.Yellow.hashCode()
             range2.from = 50.0
             range2.to = 100.0

             val range3 = Range()
             range3.color = Color.Green.hashCode()
             range3.from = 100.0
             range3.to = 150.0*/
            gauge.minValue = 0.0
            gauge.maxValue = 360.0
            calculateRotation()
            gauge.value = angle.toDouble()
            gauge
        },
        update = { gauge ->
            calculateRotation()
            gauge.value = angle.toDouble()
            gauge.invalidate()
        }
    )


}
