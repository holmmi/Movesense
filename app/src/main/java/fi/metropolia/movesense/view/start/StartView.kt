package fi.metropolia.movesense.view.start

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseSearcher
import fi.metropolia.movesense.navigation.NavigationRoutes
import fi.metropolia.movesense.util.PermissionUtil

@ExperimentalMaterial3Api
@Composable
fun StartView(navController: NavController, startViewModel: StartViewModel = viewModel()) {
    startViewModel.startScan()
    val movesenseDevices = startViewModel.movesenseDevices.observeAsState()
    var permissionsGiven by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val permissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsGiven = it.values.all { value -> value }
        }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.start)) }
            )
        },
        content = {
            PermissionUtil.checkBluetoothPermissions(
                context,
                onCheckPermissions = { permissionsLauncher.launch(it) })

            MovesenseSearcher(
                movesenseDevices = movesenseDevices.value,
                onDismissRequest = { /*TODO*/ },
                onConnect = { /*TODO*/ },
                onSelect = {}
            )
        }
    )
}
