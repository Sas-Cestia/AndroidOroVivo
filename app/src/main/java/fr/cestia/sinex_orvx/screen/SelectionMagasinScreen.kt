package fr.cestia.sinex_orvx.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.cestia.common_files.R
import fr.cestia.common_files.components.BaseTopAppBar
import fr.cestia.common_files.components.ErrorSnackBar
import fr.cestia.common_files.components.ExitButton
import fr.cestia.common_files.screens.BaseScreen
import fr.cestia.common_files.screens.ErrorScreen
import fr.cestia.common_files.screens.LoadingScreen
import fr.cestia.common_files.tools.exitApplication
import fr.cestia.common_files.tools.scan.ScanSoundManager
import fr.cestia.common_files.ui.theme.Typography
import fr.cestia.sinex_orvx.state.SelectionMagasinState
import fr.cestia.sinex_orvx.viewmodel.SelectionMagasinViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMagasinScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val viewModel: SelectionMagasinViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val errorSnackBarHostState = remember { SnackbarHostState() }
    val scanSoundManager = remember { ScanSoundManager(context) }
    val focusManager = LocalFocusManager.current

    val selectionMagasinState by viewModel.selectionMagasinState.observeAsState(
        SelectionMagasinState.Initial
    )
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val enteredCode by viewModel.enteredCode.observeAsState("")

    LaunchedEffect(enteredCode) {
        val currentCode = enteredCode
        if (currentCode.isNotEmpty()) {
            scope.launch {
                viewModel.handleQrCodeScan(currentCode)
            }
        }
    }

    LaunchedEffect(selectionMagasinState) {
        if (selectionMagasinState is SelectionMagasinState.Success) {
            navController.navigate("accueil") {
                // Clear backstack pour éviter de retourner à l'écran de chargement
                popUpTo("selectionMagasin") { inclusive = true }
            }
        }
    }

//    LaunchedEffect(errorMessage) {
//        if (errorMessage.isNotEmpty()) {
//            snackbarHostState.showSnackbar(errorMessage)
//        }
//    }

    when (selectionMagasinState) {
        is SelectionMagasinState.Loading -> {
            LoadingScreen()
        }

        is SelectionMagasinState.Error -> {
            ErrorScreen(errorMessage)
        }

        is SelectionMagasinState.Initial -> {
            BaseScreen(
                topBar = { BaseTopAppBar() },
                snackbarHostState = snackbarHostState,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {


                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Text(
                                stringResource(R.string.bonjour) + ".\n" +
                                        stringResource(R.string.selection_magasin),
                                style = Typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            TextField(
                                value = enteredCode,
                                onValueChange = { newValue ->

                                    // Mettre à jour scannedCode si nécessaire via le ViewModel
                                    scope.launch {
                                        viewModel.updateScannedCode(newValue)
                                    }

                                },
                                singleLine = true,
                                label = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            stringResource(R.string.scan_qr_code),
                                            modifier = Modifier.align(Alignment.Center),
                                            style = Typography.labelMedium
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    autoCorrectEnabled = false,
                                    keyboardType = KeyboardType.Number
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        val currentCode = enteredCode
                                        scope.launch {
                                            if (currentCode.isNotEmpty()) {
                                                viewModel.handleQrCodeScan(currentCode)
                                            }
                                        }

                                    }
                                ),
                                textStyle = Typography.titleLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }

                        Box()
                        {
                            ErrorSnackBar(
                                errorSnackBarHostState,
                                Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            ExitButton { exitApplication(context) }
                        }
                    }
                }
            }
        }

        else -> {

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 568
)
@Composable
fun PreviewSelectionMagasinScreen() {
    val navController = rememberNavController()

    SelectionMagasinScreen(
        navController = navController,
        snackbarHostState = SnackbarHostState()
    )
}
