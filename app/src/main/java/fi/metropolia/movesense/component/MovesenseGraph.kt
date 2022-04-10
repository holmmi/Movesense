package fi.metropolia.movesense.component

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import fi.metropolia.movesense.R
import fi.metropolia.movesense.model.MovesenseDataResponse

@Composable
fun MovesenseGraph(graphData: List<MovesenseDataResponse.Array?>) {
    val entriesX = mutableListOf<Entry>()
    val entriesY = mutableListOf<Entry>()
    val entriesZ = mutableListOf<Entry>()
    lateinit var chart: LineChart

    val xSet: LineDataSet = LineDataSet(entriesX, "x")
    val ySet: LineDataSet = LineDataSet(entriesY, "y")
    val zSet: LineDataSet = LineDataSet(entriesZ, "z")

    fun setData(): LineData {
        graphData.forEachIndexed { index, value ->
            if (value != null) {
                entriesX.add(Entry(index.toFloat(), value.x.toFloat()))
                entriesX.add(Entry(index.toFloat(), value.y.toFloat()))
                entriesX.add(Entry(index.toFloat(), value.z.toFloat()))
            }
        }
        xSet.axisDependency = YAxis.AxisDependency.LEFT
        xSet.color = ColorTemplate.getHoloBlue()
        xSet.setCircleColor(R.color.md_theme_light_background)
        xSet.lineWidth = 2f
        xSet.circleRadius = 3f
        xSet.fillAlpha = 65
        xSet.fillColor = ColorTemplate.getHoloBlue()
        xSet.highLightColor = R.color.md_theme_light_onPrimary
        xSet.setDrawCircleHole(false)
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);

        // create a dataset and give it a type
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);

        // create a dataset and give it a type
        ySet.axisDependency = YAxis.AxisDependency.RIGHT
        ySet.color = Color.Red.hashCode()
        ySet.setCircleColor(Color.White.hashCode())
        ySet.lineWidth = 2f
        ySet.circleRadius = 3f
        ySet.fillAlpha = 65
        ySet.fillColor = Color.Red.hashCode()
        ySet.setDrawCircleHole(false)
        ySet.highLightColor = Color.Green.hashCode()
        //set2.setFillFormatter(new MyFillFormatter(900f));

        //set2.setFillFormatter(new MyFillFormatter(900f));
        // zSet = LineDataSet(entriesZ, "z")
        zSet.axisDependency = YAxis.AxisDependency.RIGHT
        zSet.color = Color.Yellow.hashCode()
        zSet.setCircleColor(Color.White.hashCode())
        zSet.lineWidth = 2f
        zSet.circleRadius = 3f
        zSet.fillAlpha = 65
        zSet.fillColor = Color.Yellow.hashCode()
        zSet.setDrawCircleHole(false)
        zSet.highLightColor = Color.Yellow.hashCode()

        val data = LineData(xSet, ySet, zSet)
        data.setValueTextColor(R.color.md_theme_light_background)
        data.setValueTextSize(9f)

        return data
    }

    fun updateData() {
        graphData.forEachIndexed { index, value ->
            if (value != null) {
                entriesX.add(Entry(index.toFloat(), value.x.toFloat()))
                entriesX.add(Entry(index.toFloat(), value.y.toFloat()))
                entriesX.add(Entry(index.toFloat(), value.z.toFloat()))
            }
        }

        xSet.values = entriesX;
        ySet.values = entriesY;
        zSet.values = entriesZ;
        chart.data.notifyDataChanged();
        chart.notifyDataSetChanged();
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            chart = LineChart(context)
            chart.legend.isEnabled = false

            val desc = Description()

            desc.text = "Test graph"
            chart.description = desc
            chart.data = setData()
            chart
        },
        update = { view ->
            if (view.data != null &&
                view.data.dataSetCount > 0
            ) {
                updateData()
            } else {
                view.data = setData()
            }
            view.invalidate()
        }
    )
}