package fi.metropolia.movesense.component

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import fi.metropolia.movesense.R
import fi.metropolia.movesense.model.MovesenseDataResponse

@Composable
fun MovesenseGraph(graphData: List<MovesenseDataResponse.Array?>) {
    val entriesX = mutableListOf<Entry>()
    val entriesY = mutableListOf<Entry>()
    val entriesZ = mutableListOf<Entry>()

    graphData.forEachIndexed { index, value ->
        if (value != null) {
            entriesX.add(Entry(index.toFloat(), value.x.toFloat()))
            entriesY.add(Entry(index.toFloat(), value.y.toFloat()))
            entriesZ.add(Entry(index.toFloat(), value.z.toFloat()))
        }
    }

    fun setData(): LineData {
        Log.d("chart", "setData")
        val xSet = LineDataSet(entriesZ, "x")
        xSet.axisDependency = YAxis.AxisDependency.LEFT
        xSet.color = Color.Blue.hashCode()
        xSet.lineWidth = 2f
        xSet.setDrawCircles(false)
        xSet.fillAlpha = 255
        xSet.setDrawFilled(false)
        xSet.setDrawCircleHole(false)

        // create a dataset and give it a type
        val ySet = LineDataSet(entriesZ, "z")
        ySet.axisDependency = YAxis.AxisDependency.LEFT
        ySet.color = Color.Yellow.hashCode()
        ySet.lineWidth = 2f
        ySet.setDrawCircles(false)
        ySet.fillAlpha = 255
        ySet.setDrawFilled(false)

        val zSet = LineDataSet(entriesZ, "z")
        zSet.axisDependency = YAxis.AxisDependency.LEFT
        zSet.color = Color.Green.hashCode()
        zSet.setDrawCircles(false)
        zSet.lineWidth = 2f
        zSet.circleRadius = 0f
        zSet.fillAlpha = 255
        zSet.setDrawFilled(false)

        val data = LineData(xSet, ySet, zSet)
        data.setValueTextColor(R.color.md_theme_light_background)
        data.setValueTextSize(9f)

        return data
    }

    fun updateData(chart: LineChart) {
        Log.d("chart", "updateData")

        val xSet = chart.data.getDataSetByIndex(0) as LineDataSet
        val ySet = chart.data.getDataSetByIndex(1) as LineDataSet
        val zSet = chart.data.getDataSetByIndex(2) as LineDataSet

        xSet.values = entriesX;
        ySet.values = entriesY;
        zSet.values = entriesZ;
        chart.data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            val chart = LineChart(context)
            chart.legend.isEnabled = true

            val desc = Description()

            desc.text = "Test graph"
            chart.description = desc
            chart.data = setData()
            chart
        },
        update = { chart ->
            if (chart.data != null &&
                chart.data.dataSetCount > 0
            ) {
                updateData(chart)
            } else {
                chart.data = setData()
                chart.invalidate()
            }
        }
    )
}