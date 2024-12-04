package fr.cestia.common_files.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import fr.cestia.common_files.components.BaseTopAppBar
import fr.cestia.common_files.components.ExitButton
import fr.cestia.common_files.components.RetryButton
import fr.cestia.common_files.tools.exitApplication
import fr.cestia.common_files.ui.theme.DarkRed

@Composable
fun ErrorScreen(
    message: String,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current,
    displayRetryButton: Boolean = false,
    actionOnRetry: () -> Unit = {},
    actionOnExit: () -> Unit = { exitApplication(context) }
) {

    BaseScreen(
        topBar = { BaseTopAppBar() },
        snackbarHostState = snackbarHostState,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = message, color = DarkRed)

            }
        }


        Row {

            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (displayRetryButton) {
                    RetryButton(actionOnClick = actionOnRetry)
                }
                ExitButton(actionOnClick = actionOnExit)
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
fun PreviewErrorScreen() {

    ErrorScreen(
        message = "Erreur de chargement des matières et familles:\n" +
                "Le serveur n'a pas pu traiter la demande. ---> Code entreprise obligatoire et sur 4 caractères.",
        snackbarHostState = SnackbarHostState()
    )
}