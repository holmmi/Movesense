package fi.metropolia.movesense.view.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.MovesenseGraph

@ExperimentalMaterial3Api
@Composable
fun HistoryDetailsView(
    navController: NavController,
    measurementId: String?,
    historyDetailsViewModel: HistoryDetailsViewModel = viewModel(),
) {
    val entriesX by historyDetailsViewModel.entriesX.observeAsState()
    val entriesY by historyDetailsViewModel.entriesY.observeAsState()
    val entriesZ by historyDetailsViewModel.entriesZ.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.history)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                measurementId?.let {
                    MovesenseGraph(
                        entriesX = entriesX,
                        entriesY = entriesY,
                        entriesZ = entriesZ,
                        selectedData = null,
                        onSelectMeasureType = { historyDetailsViewModel.changeMeasureType(it) },
                        onClearData = {},
                        onCombineAxis = { historyDetailsViewModel.toggleCombineAxis() },
                        isLiveGraph = false
                    )
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        if (measurementId != null) {
            historyDetailsViewModel.getData(measurementId.toLong())
        }
    }
}