package fi.metropolia.movesense.view.settings

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.movesense.R
import fi.metropolia.movesense.navigation.NavigationRoutes

@ExperimentalMaterial3Api
@Composable
fun RegisterView(navController: NavController, settingsViewModel: SettingsViewModel = viewModel()) {
    var username by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConf by rememberSaveable { mutableStateOf("") }
    var organization by rememberSaveable { mutableStateOf(1) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current

    val organizations by settingsViewModel.organizationResponse.observeAsState()
    val registerResponse by settingsViewModel.registerResponse.observeAsState()
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

                if (registerResponse != null) {
                    val nameMsg = registerResponse!!.name
                    val organizationIdMsg = registerResponse!!.organizationId
                    val passwordMsg = registerResponse!!.password
                    val usernameMsg = registerResponse!!.username
                    if (nameMsg == null &&
                        organizationIdMsg == null &&
                        passwordMsg == null &&
                        usernameMsg == null
                    ) {
                        Toast.makeText(
                            context,
                            stringResource(id = R.string.register_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(NavigationRoutes.SETTINGS)
                    } else {
                        Text(text = "${stringResource(id = R.string.register_failed)} ${"$nameMsg, "}${"$organizationIdMsg, "}${"$passwordMsg, "},${"$usernameMsg"}")
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(text = stringResource(id = R.string.username)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.name)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(text = stringResource(id = R.string.password)) }
                )
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp),
                    value = passwordConf,
                    onValueChange = { passwordConf = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(text = stringResource(id = R.string.password_conf)) }
                )
                if (organizations != null) {
                    Column() {
                        OutlinedTextField(
                            value = organizations?.let { organizations!![organization - 1] }?.name
                                ?: stringResource(
                                    id = R.string.name_not_found
                                ),
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            trailingIcon = {
                                Icon(Icons.Default.ExpandMore, "")
                            },
                            label = { Text(text = stringResource(id = R.string.organization)) },
                            interactionSource = interactionSource,
                        )
                        if (isPressed) expanded = true
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            organizations?.forEach {
                                DropdownMenuItem(text = {
                                    Text(
                                        text = it.name
                                            ?: stringResource(id = R.string.name_not_found)
                                    )
                                }, onClick = {
                                    organization =
                                        it.id!!
                                    expanded = false
                                })
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
            }
        }
    )
    LaunchedEffect(Unit) {
        settingsViewModel.getOrganizations()
    }
}