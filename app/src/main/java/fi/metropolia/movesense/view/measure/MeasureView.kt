package fi.metropolia.movesense.view.measure

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import fi.metropolia.movesense.R
import fi.metropolia.movesense.model.DataResponse

@ExperimentalMaterial3Api
@Composable
fun MeasureView(
    navController: NavController,
    address: String?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    val dataResp = measureViewModel.dataResp.observeAsState()
    val graphData = measureViewModel.graphData.observeAsState()
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            val accArray = dataResp.value?.body?.arrayAcc?.get(0)
            val gyroArray = dataResp.value?.body?.arrayGyro?.get(0)
            val magnArray = dataResp.value?.body?.arrayGyro?.get(0)

            Column(modifier = Modifier.fillMaxSize()) {
                if (measureViewModel.isConnected.value == true) {
                    Log.d("graph", graphData.value.toString())
                    if (graphData.value != null) {
                        ShowGraph(getValue(graphData.value!!))
                    }
                }
            }
        }
    )

    if (address != null) {
        LaunchedEffect(Unit) {
            measureViewModel.connect(address)
        }
    }
}

fun getValue(graphData: List<DataResponse.Body>) = graphData.map { it.arrayAcc[0] }


@Composable
fun ShowGraph(graphData: List<DataResponse.Array>) {
    Log.d("graph", graphData.toString())
    val entriesX = mutableListOf<Entry>()
    val entriesY = mutableListOf<Entry>()
    val entriesZ = mutableListOf<Entry>()

    fun setChartData(): LineData {
        val xSet = LineDataSet(entriesX, "x")

        xSet.axisDependency = AxisDependency.LEFT
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
        val ySet = LineDataSet(entriesY, "DataSet 2")
        ySet.axisDependency = AxisDependency.RIGHT
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
        val zSet = LineDataSet(entriesZ, "z")
        zSet.axisDependency = AxisDependency.RIGHT
        zSet.color = Color.Yellow.hashCode()
        zSet.setCircleColor(Color.White.hashCode())
        zSet.lineWidth = 2f
        zSet.circleRadius = 3f
        zSet.fillAlpha = 65
        zSet.fillColor = Color.Yellow.hashCode()
        zSet.setDrawCircleHole(false)
        zSet.highLightColor = Color.Yellow.hashCode()

        // create a data object with the data sets

        // create a data object with the data sets
        val data = LineData(xSet, ySet, zSet)
        data.setValueTextColor(R.color.md_theme_light_background)
        data.setValueTextSize(9f)
        return data
    }

    graphData.forEachIndexed { index, value ->
        entriesX.add(Entry(index.toFloat(), value.x.toFloat()))
        entriesX.add(Entry(index.toFloat(), value.y.toFloat()))
        entriesX.add(Entry(index.toFloat(), value.z.toFloat()))
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            val view = LineChart(context)
            view.legend.isEnabled = false

            val desc = Description()

            desc.text = "Test graph for Acc data"
            view.description = desc;
            view.data = setChartData()
            view
        },
        update = { view ->
            view.data = setChartData()
            view.invalidate()
        }
    )
}