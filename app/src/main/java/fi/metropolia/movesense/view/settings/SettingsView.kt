package fi.metropolia.movesense.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.navigation.NavigationRoutes

@ExperimentalMaterial3Api
@Composable
fun SettingsView(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val userToken by settingsViewModel.userToken.observeAsState()
    val userDetails by settingsViewModel.detailsResponse.observeAsState()
    val organizations by settingsViewModel.organizationResponse.observeAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showLoginDialog && userToken == null) {
                    Dialog(
                        onDismissRequest = { showLoginDialog = false },
                        content = {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .fillMaxHeight(0.5f)
                                    .fillMaxWidth(0.9f)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier.padding(8.dp),
                                        value = username,
                                        onValueChange = { username = it },
                                        placeholder = { Text(text = stringResource(id = R.string.username)) },
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        modifier = Modifier.padding(8.dp),
                                        value = password,
                                        onValueChange = { password = it },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        placeholder = { Text(text = stringResource(id = R.string.password)) }
                                    )

                                    Spacer(modifier = Modifier.weight(1f, false))

                                    Row(modifier = Modifier.align(Alignment.End)) {
                                        TextButton(onClick = { showLoginDialog = false }) {
                                            Text(text = stringResource(id = R.string.cancel))
                                        }
                                        TextButton(onClick = {
                                            settingsViewModel.login(
                                                username,
                                                password
                                            )
                                        }) {
                                            Text(text = stringResource(id = R.string.login))
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                if (!userToken.isNullOrEmpty() && userDetails != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(150.dp),
                        ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(id = R.string.welcome))
                            Text(
                                text = userDetails!!.name ?: "",
                                )
                            Text(
                                text = userDetails!!.username ?: "",
                            )
                            Text(
                                text = organizations?.get(userDetails!!.organization_id!!.minus(1))!!.name
                                    ?: "",
                            )
                        }

                    }
                }

                OutlinedButton(
                    onClick = {
                        if (userToken != null) {
                            settingsViewModel.logout()
                        } else {
                            showLoginDialog = true
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = if (userToken != null) Icons.Outlined.Logout else Icons.Outlined.Login,
                        contentDescription = stringResource(id = if (userToken != null) R.string.logout else R.string.login)
                    )
                    Text(text = stringResource(id = if (userToken != null) R.string.logout else R.string.login))
                }
                OutlinedButton(
                    onClick = { navController.navigate(NavigationRoutes.REGISTER) },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AppRegistration,
                        contentDescription = stringResource(id = R.string.register)
                    )
                    Text(text = stringResource(id = R.string.register))
                }
                OutlinedButton(
                    onClick = { navController.navigate(NavigationRoutes.DEVICE_SETTINGS) },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                    Text(text = stringResource(id = R.string.settings))
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        settingsViewModel.getOrganizations()
    }

    LaunchedEffect(userToken) {
        userToken?.let { settingsViewModel.getUserDetails(it) }
    }
}
