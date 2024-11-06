package fr.cestia.sinex_orvx.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.cestia.sinex_orvx.R
import fr.cestia.sinex_orvx.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTopAppBar(
    icon: Int = remember { R.drawable.baseline_settings_24 },
    onIconClick: () -> Unit = {},
    text: Int = remember { R.string.bonjour }
) {
    /**
     * Barre de navigation au-dessus de l'écran
     */
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Spacer(Modifier.width(60.dp)) // TODO: Remplacer par icône Sinex

                Text(
                    stringResource(text),
                    style = Typography.titleLarge
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onIconClick()
                },
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = ""
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
