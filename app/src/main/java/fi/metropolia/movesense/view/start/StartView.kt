package fi.metropolia.movesense.view.start

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import fi.metropolia.movesense.component.MovesenseSearcher
import fi.metropolia.movesense.navigation.NavigationRoutes
import fi.metropolia.movesense.util.PermissionUtil

@ExperimentalMaterial3Api
@Composable
fun StartView(navController: NavController, startViewModel: StartViewModel = viewModel()) {
    val context = LocalContext.current
    val movesenseDevices = startViewModel.movesenseDevices.observeAsState()
    var permissionsGiven by rememberSaveable { mutableStateOf(false) }
    val isSearching = startViewModel.isSearching.observeAsState()
    var searched by rememberSaveable { mutableStateOf(false) }
    val permissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.values.all { value -> value }
        }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.select_sensor)) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            OutlinedButton(
                onClick = {
                    startViewModel.startScan()
                    searched = true
                },
                enabled = !isSearching.value!!
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.scan)
                )
            }
        },
        content = {
            if (!searched) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                        .padding(16.dp)
                ) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.search_devices),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterVertically)
                                .padding(8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .height(125.dp)
                                .background(color = MaterialTheme.colorScheme.secondaryContainer),
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                null,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                            )
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (permissionsGiven) {
                        MovesenseSearcher(
                            movesenseDevices = movesenseDevices.value,
                            onConnect = {
                                if (!movesenseDevices.value.isNullOrEmpty()) {
                                    navController.navigate(
                                        NavigationRoutes.MEASURE.replace(
                                            "{address}",
                                            movesenseDevices.value!![it].macAddress
                                        )
                                    )
                                }
                            },
                            isSearching = isSearching.value ?: false,
                        )
                    } else {
                        permissionsGiven =
                            PermissionUtil.checkBluetoothPermissions(
                                context,
                                onCheckPermissions = { permissionsLauncher.launch(it) }
                            )
                    }
                }
            }

        }
    )

    LaunchedEffect(Unit) {
        permissionsGiven =
            PermissionUtil.checkBluetoothPermissions(
                context,
                onCheckPermissions = { permissionsLauncher.launch(it) }
            )
    }
}
