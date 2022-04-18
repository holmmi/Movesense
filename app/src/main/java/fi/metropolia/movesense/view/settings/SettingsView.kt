package fi.metropolia.movesense.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseSearcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun SettingsView(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    val movesenseDevices = settingsViewModel.movesenseDevices.observeAsState()
    val isConnected = settingsViewModel.isConnected.observeAsState()
    val isSearching = settingsViewModel.isSearching.observeAsState()
    val advSettings = settingsViewModel.advSettings.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (!isConnected.value!!) {
                OutlinedButton(
                    onClick = {
                        settingsViewModel.startScan()
                    },
                    enabled = !isSearching.value!!
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = R.string.scan)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = {
                        settingsViewModel.disconnect()
                    },
                    enabled = !isSearching.value!!
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Disconnect"
                    )
                }
            }

        },
        content = {
            if (!isConnected.value!! || isSearching.value!!) {
                MovesenseSearcher(
                    movesenseDevices = movesenseDevices.value,
                    onConnect = {
                        settingsViewModel.stopScan()
                        settingsViewModel.connect(movesenseDevices.value!![it].macAddress)
                    },
                    isSearching = isSearching.value ?: false
                )
            } else {
                val settings = advSettings.value?.content
                var interval by rememberSaveable { mutableStateOf("") }
                var timeout by rememberSaveable { mutableStateOf("") }
                var success by rememberSaveable { mutableStateOf<Boolean?>(null) }
                val coroutineScope = rememberCoroutineScope()

                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Advertising settings")
                        Text(text = "interval ${settings?.interval}")
                        Text(text = "timeout ${settings?.timeout}")
                    }
                    Column(
                        modifier = Modifier
                            .height(500.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = interval,
                            onValueChange = { interval = it },
                            placeholder = { Text(text = stringResource(id = R.string.interval)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = timeout,
                            onValueChange = { timeout = it },
                            placeholder = { Text(text = stringResource(id = R.string.timeout)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedButton(onClick = {
                                settingsViewModel.changeAdvertisementSettings(
                                    interval.toInt(),
                                    timeout.toInt()
                                )

                        }) {
                            Text(text = "Apply")
                        }
                        if (success == true) {
                            Text("success")
                        }
                    }
                }
            }
        }
    )
}
