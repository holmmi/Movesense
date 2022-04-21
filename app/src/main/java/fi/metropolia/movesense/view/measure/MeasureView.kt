package fi.metropolia.movesense.view.measure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseGraph
import fi.metropolia.movesense.component.ShowAnimation
import fi.metropolia.movesense.extension.round
import fi.metropolia.movesense.types.MeasureType

@ExperimentalMaterial3Api
@Composable
fun MeasureView(
    navController: NavController,
    address: String?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    val selectedData by measureViewModel.dataAvg.observeAsState()
    val measureType by measureViewModel.measureType.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (address != null) {
                            measureViewModel.disconnect(address)
                        }
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (measureViewModel.isConnected.value == true) {
                        IconButton(onClick = {
                            if (address != null) {
                                measureViewModel.disconnect(address)
                            }
                            navController.navigateUp()
                        }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BluetoothDisabled,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) {
        val selectedBtnColor = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.surface
        )

        if (selectedData != null && measureViewModel.isConnected.value == true) {
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
                        onClick = {
                            measureViewModel.toggleClearData()
                            measureViewModel.changeMeasureType(MeasureType.Acceleration)
                        },
                        colors =
                        if (measureType == MeasureType.Acceleration) {
                            selectedBtnColor
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        if (measureType == MeasureType.Acceleration) {
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
                        onClick = {
                            measureViewModel.toggleClearData()
                            measureViewModel.changeMeasureType(MeasureType.Gyro)
                        },
                        colors =
                        if (measureType == MeasureType.Gyro) {
                            selectedBtnColor
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        if (measureType == MeasureType.Gyro) {
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
                        onClick = {
                            measureViewModel.toggleClearData()
                            measureViewModel.changeMeasureType(MeasureType.Magnetic)
                        },
                        colors =
                        if (measureType == MeasureType.Magnetic) {
                            selectedBtnColor
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        if (measureType == MeasureType.Magnetic) {
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
                        .weight(9F)
                ) {
                    MovesenseGraph(
                        measureViewModel = measureViewModel
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2F),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedButton(
                        onClick = {
                            measureViewModel.toggleClearData()
                            measureViewModel.toggleCombineAxis()
                        },
                        colors =
                        if (measureViewModel.combineAxis.value == true) {
                            selectedBtnColor
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        if (measureViewModel.combineAxis.value == true) {
                            Icon(
                                modifier = Modifier.padding(end = 8.dp),
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                        Text(stringResource(id = R.string.combine_axis))
                    }
                    OutlinedButton(onClick = { measureViewModel.toggleClearData() }) {
                        Text(stringResource(id = R.string.clear_graph))
                    }
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
                                imageVector = when (measureType) {
                                    MeasureType.Acceleration -> Icons.Outlined.DirectionsRun
                                    MeasureType.Gyro -> Icons.Outlined.FlipCameraAndroid
                                    MeasureType.Magnetic -> Icons.Outlined.Polymer
                                    else -> {
                                        Icons.Outlined.ErrorOutline
                                    }
                                }, contentDescription = null
                            )
                        }
                        Text(
                            text = measureType!!.name, modifier = Modifier
                                .padding(8.dp)
                                .align(
                                    Alignment.CenterVertically
                                )
                        )
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("x: ${selectedData!!.x.round(3)}")
                            Text("y: ${selectedData!!.y.round(3)}")
                            Text("z: ${selectedData!!.z.round(3)}")
                        }
                    }
                }
            }
        } else {
            Column() {
                ShowAnimation(assetName = "animations/48244-dashboard-data-visualization.json")
                Text(stringResource(id = R.string.loading), modifier = Modifier.padding(top = 300.dp))
            }
        }
    }

    if (address != null) {
        LaunchedEffect(Unit) {
            measureViewModel.connect(address)
        }
    }
}