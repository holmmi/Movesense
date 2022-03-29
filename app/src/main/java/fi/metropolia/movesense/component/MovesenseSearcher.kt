package fi.metropolia.movesense.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.metropolia.movesense.R
import fi.metropolia.movesense.bluetooth.MovesenseDevice

@ExperimentalMaterial3Api
@Composable
fun MovesenseSearcher(
    movesenseDevices: List<MovesenseDevice>?,
    onConnect: (Int) -> Unit,
    isSearching: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isSearching) {
            Text(
                text =
                stringResource(id = R.string.searching),
                style = MaterialTheme.typography.labelSmall,
            )
        }
        if (movesenseDevices.isNullOrEmpty() && !isSearching) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.devices_not_found),
                        modifier = Modifier
                            .align(CenterVertically)
                            .padding(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(125.dp)
                            .background(color = MaterialTheme.colorScheme.secondaryContainer),
                    ) {
                        Icon(
                            Icons.Filled.ErrorOutline,
                            null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp),
                        )
                    }
                }

            }
        }
        movesenseDevices?.let {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxHeight()
                    .padding(bottom = 16.dp)
            ) {
                it.forEachIndexed { index, device ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .clickable(
                                onClick = {
                                    onConnect(index)
                                },
                                role = Role.Button
                            )
                            .padding(8.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Text(
                                    text = device.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "MAC: ${device.macAddress}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "RSSI: ${device.rssi} dBm",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(125.dp)
                                    .background(color = MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Icon(
                                    Icons.Filled.Bluetooth,
                                    null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(48.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}