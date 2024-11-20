package fr.cestia.sinex_orvx.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.cestia.common_files.R
import fr.cestia.common_files.components.BaseTopAppBar
import fr.cestia.common_files.components.ExitButton
import fr.cestia.common_files.tools.exitApplication
import fr.cestia.common_files.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccueilScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
//    val viewModel: AccueilViewModel = hiltViewModel<>()

    fr.cestia.common_files.screens.BaseScreen(
        topBar = { BaseTopAppBar() },
        snackbarHostState = snackbarHostState,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("saisieInventaire") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 78.dp)
                ) {
                    Text(
                        stringResource(R.string.saisie) + " " + stringResource(R.string.inventaire),
                        style = Typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = stringResource(R.string.inventaire)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { /*navController.navigate("consultationInventaire")*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 78.dp)
                ) {
                    Text(
                        stringResource(R.string.consultation) + " " + stringResource(R.string.inventaire),
                        style = Typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = stringResource(R.string.inventaire)
                    )
                }

//                Button(
//                    onClick = { viewModel.restartDataWedge },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .heightIn(min = 78.dp)
//                ) {
//                    Text(
//                        stringResource(R.string.consultation) + " " + stringResource(R.string.inventaire),
//                        style = Typography.titleLarge,
//                        modifier = Modifier
//                            .weight(1f)
//                    )
//                    Icon(
//                        painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
//                        contentDescription = stringResource(R.string.inventaire)
//                    )
//                }
            }
        }

        Row {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                ExitButton { exitApplication(context) }
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
fun PreviewAccueilScreen() {
    val navController = rememberNavController()

    AccueilScreen(
        navController = navController,
        snackbarHostState = SnackbarHostState()
    )
}
