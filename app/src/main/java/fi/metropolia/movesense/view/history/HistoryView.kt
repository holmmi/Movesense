package fi.metropolia.movesense.view.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.component.ShowAnimation
import fi.metropolia.movesense.util.DateUtil

@ExperimentalMaterial3Api
@Composable
fun HistoryView(navController: NavController, settingsViewModel: HistoryViewModel = viewModel()) {
    val measurementInformation by settingsViewModel.getMeasurementInformation().observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.history)) }
            )
        },
        content = {
            Column(Modifier.padding(8.dp)) {
                measurementInformation?.let {
                    if (it.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.choose_measurement),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            items(it) { item ->
                                Card(
                                    onClick = { /* TODO: Implement navigation to history details */ },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = item.description ?: stringResource(id = R.string.no_description),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = DateUtil.getFormattedDate(item.date),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ShowAnimation(assetName = "animations/13659-no-data.json")
                            Text(
                                text = stringResource(id = R.string.no_logs_collected),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}