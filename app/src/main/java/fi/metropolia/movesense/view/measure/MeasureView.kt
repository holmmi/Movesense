package fi.metropolia.movesense.view.measure

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import fi.metropolia.movesense.component.MovesenseGraph
import fi.metropolia.movesense.extension.round
import fi.metropolia.movesense.model.MovesenseDataResponse
import fi.metropolia.movesense.types.MeasureType

@ExperimentalMaterial3Api
@Composable
fun MeasureView(
    navController: NavController,
    address: String?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    val graphData = measureViewModel.graphData.observeAsState()
    val selectedData = measureViewModel.dataAvg.observeAsState()
    var measureType = measureViewModel.measureType.observeAsState()

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

            val selectedBtnColor = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.surface
            )

            /*selectedData = when (measureType) {
                MeasureType.Acceleration -> {
                    graphData.value?.map { it.arrayAcc[0] } ?: listOf()
                }

                MeasureType.Gyro -> {
                    graphData.value?.map { it.arrayGyro[0] } ?: listOf()
                }

                MeasureType.Magnetic -> {
                    graphData.value?.map { it.arrayMagn[0] } ?: listOf()
                }
            }*/
            if (selectedData.value != null && graphData.value != null && measureViewModel.isConnected.value == true) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxWidth()
                            .weight(2F),
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(40.dp)
                                .weight(3F),
                            onClick = { measureViewModel.changeMeasureType(MeasureType.Acceleration) },
                            colors =
                            if (measureType.value == MeasureType.Acceleration) {
                                selectedBtnColor
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            if (measureType.value == MeasureType.Acceleration) {
                                Icon(
                                    modifier = Modifier.padding(end = 8.dp),
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                            Text(text = stringResource(id = R.string.acc))
                        }
                        OutlinedButton(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(40.dp)
                                .weight(2F),
                            onClick = { measureViewModel.changeMeasureType(MeasureType.Gyro) },
                            colors =
                            if (measureType.value == MeasureType.Gyro) {
                                selectedBtnColor
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            if (measureType.value == MeasureType.Gyro) {
                                Icon(
                                    modifier = Modifier.padding(end = 8.dp),
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                            Text(text = stringResource(id = R.string.gyro))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .weight(2F),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(40.dp),
                            onClick = { measureViewModel.changeMeasureType(MeasureType.Magnetic) },
                            colors =
                            if (measureType.value == MeasureType.Magnetic) {
                                selectedBtnColor
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            if (measureType.value == MeasureType.Magnetic) {
                                Icon(
                                    modifier = Modifier.padding(end = 8.dp),
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                            Text(text = stringResource(id = R.string.magn))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(13F)
                    ) {
                        MovesenseGraph(graphData.value!!)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(3F)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .background(color = MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(48.dp),
                                    imageVector = when (measureType.value) {
                                        MeasureType.Acceleration -> Icons.Outlined.DirectionsRun
                                        MeasureType.Gyro -> Icons.Outlined.FlipCameraAndroid
                                        MeasureType.Magnetic -> Icons.Outlined.Directions
                                        else -> {
                                            Icons.Outlined.ErrorOutline
                                        }
                                    }, contentDescription = null
                                )
                            }
                            Text(
                                text = measureType.value!!.name, modifier = Modifier
                                    .padding(8.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("x: ${selectedData.value!!.x.round(3)}")
                                Text("y: ${selectedData.value!!.y.round(3)}")
                                Text("z: ${selectedData.value!!.z.round(3)}")
                            }
                        }
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