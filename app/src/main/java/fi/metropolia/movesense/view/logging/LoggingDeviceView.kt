package fi.metropolia.movesense.view.logging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun LoggingDeviceView(
    navController: NavController,
    macAddress: String?,
    loggingDeviceViewModel: LoggingDeviceViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        if (macAddress != null) {
            loggingDeviceViewModel.connect(macAddress)
        }
    }
}