package fi.metropolia.movesense.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseSearcher

@ExperimentalMaterial3Api
@Composable
fun DeviceSettings(
    navController: NavController,
    deviceSettingsViewModel: DeviceSettingsViewModel = viewModel()
) {
    val movesenseDevices = deviceSettingsViewModel.movesenseDevices.observeAsState()
    val isConnected = deviceSettingsViewModel.isConnected.observeAsState()
    val isSearching = deviceSettingsViewModel.isSearching.observeAsState()
    val advSettings = deviceSettingsViewModel.advSettings.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = {
                        deviceSettingsViewModel.disconnect()
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                })
        },
        content = {
            if (!isConnected.value!! || isSearching.value!!) {
                MovesenseSearcher(
                    movesenseDevices = movesenseDevices.value,
                    onConnect = {
                        deviceSettingsViewModel.stopScan()
                        deviceSettingsViewModel.connect(movesenseDevices.value!![it].macAddress)
                    },
                    isSearching = isSearching.value ?: false,
                    onStartScan = { deviceSettingsViewModel.startScan() }
                )
            } else {
                val settings = advSettings.value?.content

                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = R.string.advertising_settings))
                        Text(text = stringResource(id = R.string.interval, settings?.interval ?: 0))
                        Text(text = stringResource(id = R.string.timeout, settings?.timeout ?: 0))
                    }
                }
            }
        }
    )
}