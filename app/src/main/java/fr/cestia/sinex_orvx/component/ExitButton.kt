package fr.cestia.sinex_orvx.component

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import fr.cestia.sinex_orvx.R
import fr.cestia.sinex_orvx.ui.theme.Typography
import fr.cestia.sinex_orvx.util.exitApplication

@Composable
fun ExitButton(context: Context) {
    Button(
        onClick = { exitApplication(context) },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_close_24),
                contentDescription = "Quitter",
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Quitter",
                style = Typography.titleMedium,
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}