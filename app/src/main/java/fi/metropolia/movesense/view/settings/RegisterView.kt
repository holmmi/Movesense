package fi.metropolia.movesense.view.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R

@ExperimentalMaterial3Api
@Composable
fun RegisterView(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    var username by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConf by rememberSaveable { mutableStateOf("") }
    var organization by rememberSaveable { mutableStateOf(0) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val organizations by settingsViewModel.organizationResponse.observeAsState()
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text(text = stringResource(id = R.string.username)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(text = stringResource(id = R.string.name)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = VisualTransformation.None,
                    placeholder = { Text(text = stringResource(id = R.string.password)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = passwordConf,
                    onValueChange = { passwordConf = it },
                    visualTransformation = VisualTransformation.None,
                    placeholder = { Text(text = stringResource(id = R.string.password_conf)) }
                )
                if (organizations != null) {
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        organizations?.organizations?.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        organization =
                                            it.id!!
                                        expanded = false
                                    })
                            ) {
                                Text(text = it.name ?: stringResource(id = R.string.name_not_found))
                            }
                        }
                    }
                }

                OutlinedButton(onClick = {
                    settingsViewModel.register(
                        name,
                        username,
                        password,
                        passwordConf,
                        organization
                    )
                }) {
                    Text(text = stringResource(id = R.string.register))
                }
                Spacer(modifier = Modifier.weight(1f, false))
            }
        }
    )
    LaunchedEffect(Unit) {
        settingsViewModel.getOrganizations()
    }
}