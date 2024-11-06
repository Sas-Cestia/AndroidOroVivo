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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import fr.cestia.sinex_orvx.R
import fr.cestia.sinex_orvx.viewmodel.SelectionMagasinViewModel
import fr.cestia.sinex_orvx.component.BaseTopAppBar
import fr.cestia.sinex_orvx.component.ErrorSnackBar
import fr.cestia.sinex_orvx.component.ExitButton
import fr.cestia.sinex_orvx.state.SelectionMagasinState
import fr.cestia.sinex_orvx.ui.theme.Typography
import fr.cestia.sinex_orvx.util.ScanSoundManager
import fr.cestia.sinex_orvx.util.hideKeyboard
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
    var scannedCode by rememberSaveable { mutableStateOf("") }

    BaseScreen(
        topBar = { BaseTopAppBar() },
        snackbarHostState = snackbarHostState,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
//        DisposableEffect(Unit) {
//            // Enregistrer le BroadcastReceiver quand le composable est affiché
//            Log.d("SelectionMagasinScreen", "Enregistrement du ScanReceiver")
//            val selectionMagScanReceiver = ScanReceiver { scannedData ->
//                // Action au scan d'un code
//                scope.launch {
//                    viewModel.handleQrCodeScan(scannedData)
//                    if (errorMessage.isNotBlank()) {
//                        scope.launch {
//                            errorSnackBarHostState.showSnackbar(
//                                errorMessage,
//                                duration = SnackbarDuration.Indefinite,
//                                withDismissAction = true
//                            )
//                        }
//                    } else {
//                        scanSoundManager.playSuccessSound()
//                    }
//                }
//            }
//            val filter = IntentFilter("fr.cestia.BARCODE_SCAN")
//            context.registerReceiver(selectionMagScanReceiver, filter)
//
//            // Désenregistrer le BroadcastReceiver quand le composable est détruit
//            onDispose {
//                Log.d("InventaireSuccessScreen", "Unregistering receiver")
//                context.unregisterReceiver(selectionMagScanReceiver)
//            }
//        }

        LaunchedEffect(selectionMagasinState) {
            if (selectionMagasinState is SelectionMagasinState.Success) {
                navController.navigate("accueil")
            }
        }

        LaunchedEffect(errorMessage) {
            if (errorMessage.isNotEmpty()) {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }

        LaunchedEffect(Unit) {
            focusManager.moveFocus(FocusDirection.Down)
            hideKeyboard(context)
        }

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
                        value = scannedCode,
                        onValueChange = { scannedCode = it },
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
                                scope.launch {
                                    viewModel.handleQrCodeScan(scannedCode)
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
                    ErrorSnackBar(errorSnackBarHostState, Modifier.align(Alignment.BottomCenter))
                }
            }

            Row (
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ExitButton(context)
                }
            }
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
