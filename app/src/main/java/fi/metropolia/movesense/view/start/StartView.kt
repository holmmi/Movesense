package fi.metropolia.movesense.view.start

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
fun StartView(navController: NavController, startViewModel: StartViewModel = viewModel()) {
    val movesenseDevices = startViewModel.movesenseDevices.observeAsState()
    val isSearching = startViewModel.isSearching.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.select_sensor)) }
            )
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)) {
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
                        onStartScan = { startViewModel.startScan() }
                    )
            }
        }
    )
}
