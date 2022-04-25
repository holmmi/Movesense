package fi.metropolia.movesense.view.logging

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseSearcher
import fi.metropolia.movesense.navigation.NavigationRoutes

@ExperimentalMaterial3Api
@Composable
fun LoggingStartView(
    navController: NavController,
    loggingStartViewModel: LoggingStartViewModel = viewModel()
) {
    val isScanning by loggingStartViewModel.isScanning.observeAsState()
    val movesenseDevices by loggingStartViewModel.movesenseDevices.observeAsState()

    Scaffold(
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.choose_device),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 10.dp, top = 5.dp)
                )
                MovesenseSearcher(
                    movesenseDevices = movesenseDevices,
                    onConnect = { deviceIndex ->
                        movesenseDevices?.let {
                            navController.navigate(
                                NavigationRoutes.LOGGING_VIEW
                                    .replace("{macAddress}", it[deviceIndex].macAddress)
                            )
                        }
                    },
                    isSearching = isScanning!!,
                    onStartScan = { loggingStartViewModel.startScan() }
                )
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            isScanning?.let {
                if (it) {
                    loggingStartViewModel.stopScan()
                }
            }
        }
    }
}