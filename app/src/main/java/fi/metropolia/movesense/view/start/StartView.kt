package fi.metropolia.movesense.view.start

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseSearcher
import fi.metropolia.movesense.navigation.NavigationRoutes

@ExperimentalMaterial3Api
@Composable
fun StartView(navController: NavController, startViewModel: StartViewModel = viewModel()) {
    startViewModel.startScan()
    val movesenseDevices = startViewModel.movesenseDevices.observeAsState()
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.start)) }
            )
        },
        content = {
            MovesenseSearcher(
                movesenseDevices = movesenseDevices.value,
                onDismissRequest = { /*TODO*/ },
                onConnect = { /*TODO*/ },
                onSelect = {}
            )
        }
    )
}
