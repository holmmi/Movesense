package fi.metropolia.movesense.view.measure

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseGauge
import fi.metropolia.movesense.component.MovesenseGraph
import fi.metropolia.movesense.component.ShowAnimation

@ExperimentalMaterial3Api
@Composable
fun MeasureView(
    navController: NavController,
    address: String?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    var gauge by rememberSaveable { mutableStateOf(false) }
    val isConnected by measureViewModel.isConnected.observeAsState()
    val selectedData by measureViewModel.dataAvg.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (address != null) {
                            measureViewModel.disconnect(address)
                        }
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (address != null) {
                            measureViewModel.disconnect(address)
                        }
                        navController.navigateUp()
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BluetoothDisabled,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        gauge = !gauge
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = {
            if (isConnected == true && selectedData != null) {
                if (gauge) {
                    MovesenseGauge(measureViewModel = measureViewModel)
                } else {
                    MovesenseGraph(measureViewModel = measureViewModel)
                }

            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()) {
                        ShowAnimation(assetName = "animations/48244-dashboard-data-visualization.json")
                    }
                        Text(
                            stringResource(id = R.string.loading),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                    }
                }
        }
    )
    if (address != null) {
        LaunchedEffect(Unit) {
            measureViewModel.connect(address)
        }
    }
}