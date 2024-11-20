package fr.cestia.common_files.components

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
import fr.cestia.common_files.R
import fr.cestia.common_files.ui.theme.Typography

@Composable
fun ExitButton(
    btnText: String = "Quitter",
    icon: Int = R.drawable.baseline_close_24,
    actionOnClick: () -> Unit = {}
) {
    Button(
        onClick = actionOnClick,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Quitter",
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = btnText,
                style = Typography.titleMedium,
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}