package fr.cestia.sinex_orvx.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cestia.sinex_orvx.ui.theme.Typography

@Composable
fun LoadingAppScreen() {
    BaseScreen {
        Text(
            "Chargement de l'application...",
            style = Typography.titleMedium
        )

        Spacer(Modifier.height(20.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}


// Fonction de preview
@Preview(showBackground = true)
@Composable
fun PreviewLoadingAppScreen() {
    LoadingAppScreen()
}
