package fi.metropolia.movesense.view.logging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R

@ExperimentalMaterial3Api
@Composable
fun LoggingDeviceView(
    navController: NavController,
    macAddress: String?,
    loggingDeviceViewModel: LoggingDeviceViewModel = viewModel()
) {
    val loggingStarted by loggingDeviceViewModel.loggingStarted.observeAsState(false)
    val operationsAllowed by loggingDeviceViewModel.operationsAllowed.observeAsState(false)
    val deviceName by loggingDeviceViewModel.deviceName.observeAsState()

    var showStartLoggingDialog by rememberSaveable { mutableStateOf(false) }
    val selectedMeasurementTypes by loggingDeviceViewModel.selectedMeasurementTypes.observeAsState(listOf())

    val context = LocalContext.current
    val measurementTypes = context.resources.getStringArray(R.array.measurement_types)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = deviceName ?: stringResource(id = R.string.connecting))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                }
            )
        },
        content = {
            if (showStartLoggingDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showStartLoggingDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showStartLoggingDialog = false
                                loggingDeviceViewModel.startLogging()
                            },
                            enabled = selectedMeasurementTypes.isNotEmpty()
                        ) {
                            Text(text = stringResource(id = R.string.start_logging))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showStartLoggingDialog = false }
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    },
                    icon = {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.choose_what_to_log))
                    },
                    text = {
                        LazyColumn {
                            itemsIndexed(measurementTypes) { index, measurementType ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp, bottom = 6.dp)
                                        .selectable(
                                            selected = selectedMeasurementTypes.contains(index),
                                            onClick = {
                                                loggingDeviceViewModel.onMeasurementTypeToggle(
                                                    index
                                                )
                                            }
                                        )
                                ) {
                                    Checkbox(
                                        checked = selectedMeasurementTypes.contains(index),
                                        onCheckedChange = {
                                            loggingDeviceViewModel.onMeasurementTypeToggle(
                                                index
                                            )
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(text = measurementType)
                                }
                            }
                        }
                    }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (loggingStarted) {
                            loggingDeviceViewModel.stopLogging()
                        } else {
                            showStartLoggingDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .padding(8.dp),
                    enabled = operationsAllowed
                ) {
                    Text(text = stringResource(id = if (loggingStarted) R.string.stop_logging else R.string.start_logging ))
                }
                OutlinedButton(
                    onClick = {
                        loggingDeviceViewModel.retrieveLogs()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .padding(8.dp),
                    enabled = operationsAllowed
                ) {
                    Text(text = stringResource(id = R.string.retrieve_logs))
                }
                OutlinedButton(
                    onClick = {
                        loggingDeviceViewModel.deleteLogs()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .padding(8.dp),
                    enabled = operationsAllowed
                ) {
                    Text(text = stringResource(id = R.string.delete_logs))
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (macAddress != null) {
            loggingDeviceViewModel.connect(macAddress)
        }
    }
}