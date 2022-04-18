package fi.metropolia.movesense.view.start

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
fun StartView(navController: NavController) {
    val context = LocalContext.current
    val startViewModel = StartViewModel(context.applicationContext as Application)
    val movesenseDevices = startViewModel.movesenseDevices.observeAsState()
    var permissionsGiven by rememberSaveable { mutableStateOf(false) }
    val isSearching = startViewModel.isSearching.observeAsState()
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
    )

    LaunchedEffect(Unit) {
        permissionsGiven =
            PermissionUtil.checkBluetoothPermissions(
                context,
                onCheckPermissions = { permissionsLauncher.launch(it) }
            )
    }
}
