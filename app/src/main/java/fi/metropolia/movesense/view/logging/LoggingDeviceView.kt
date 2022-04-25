package fi.metropolia.movesense.view.logging

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = deviceName ?: "Connecting...")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                }
            )
        },
        content = {
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
                            loggingDeviceViewModel.startLogging()
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