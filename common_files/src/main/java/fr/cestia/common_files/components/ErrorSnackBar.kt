package fr.cestia.common_files.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.cestia.common_files.ui.theme.DarkRed
import fr.cestia.common_files.ui.theme.LightRed
import fr.cestia.common_files.ui.theme.Typography

@Composable
fun ErrorSnackBar (snackBarHostState: SnackbarHostState, modifier: Modifier) {
    SnackbarHost(
        hostState = snackBarHostState,
        modifier = modifier,
        snackbar = { snackbarData ->
            Snackbar(
                containerColor = LightRed,
                contentColor = DarkRed
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    style = Typography.titleMedium
                )
            }
        }
    )
}