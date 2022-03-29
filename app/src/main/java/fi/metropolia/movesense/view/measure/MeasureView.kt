package fi.metropolia.movesense.view.measure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.bluetooth.MovesenseDevice

@ExperimentalMaterial3Api
@Composable
fun MeasureView(
    navController: NavController,
    device: MovesenseDevice?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) }
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {

            }
        }
    )

    if (device != null) {
        LaunchedEffect(Unit) {
            measureViewModel.connect(device.macAddress)
        }
    }

}