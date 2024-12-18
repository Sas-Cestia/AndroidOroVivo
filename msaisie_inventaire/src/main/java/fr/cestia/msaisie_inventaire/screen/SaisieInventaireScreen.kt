package fr.cestia.msaisie_inventaire.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import fr.cestia.common_files.R
import fr.cestia.common_files.components.BaseTopAppBar
import fr.cestia.common_files.components.ExitButton
import fr.cestia.common_files.screens.BaseScreen
import fr.cestia.common_files.screens.ErrorScreen
import fr.cestia.common_files.screens.LoadingScreen
import fr.cestia.msaisie_inventaire.state.SaisieInventaireState
import fr.cestia.msaisie_inventaire.viewmodel.SaisieInventaireViewModel
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun SaisieInventaireScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: SaisieInventaireViewModel = hiltViewModel()
    val state by viewModel.saisieInventaireState.observeAsState(SaisieInventaireState.Loading)
    val scannedTags by viewModel.scannedTags.observeAsState(emptyList<String>())
    val loadingMessage by viewModel.loadingMessage.observeAsState("")
    val errorMessage by viewModel.errorMessage.observeAsState("")

    BaseScreen(
        topBar = { BaseTopAppBar() },
        snackbarHostState = snackbarHostState,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is SaisieInventaireState.SelectDevice -> {
                val devices = (state as SaisieInventaireState.SelectDevice).devices
                LazyColumn {
                    items(devices) { device ->
                        if (device != null && device.name != null) {
                            Text(text = device.name, modifier = Modifier.clickable {
                                scope.launch {
                                    viewModel.onDeviceSelected(device)
                                }
                            })
                        } else {
                            Text(text = "")
                        }
                    }
                }
            }

            is SaisieInventaireState.Initial -> {
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.scan_rfid_tags))
                    }
                }

                Row {
                    Text(text = "Tags RFID scannés :")
                }

                Row {
                    LazyColumn {
                        items(scannedTags) { tag ->
                            Text(text = tag)
                        }
                    }
                }

                Row {
                    Column(
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        ExitButton(
                            btnText = "Retour à l'accueil",
                            icon = R.drawable.baseline_arrow_back_24
                        ) {
                            navController.navigate("accueil") {
                                popUpTo("saisieInventaire") { inclusive = true }
                            }
                        }
                    }
                }
            }

            is SaisieInventaireState.Loading -> {
                LoadingScreen(loadingMessage = loadingMessage)
            }

            is SaisieInventaireState.Error -> {
                ErrorScreen(
                    errorMessage,
                    displayRetryButton = true,
                    actionOnRetry = {
                        scope.launch {
                            viewModel.initialize()
                        }
                    }
                )
            }

            else -> {

            }
        }

    }
}