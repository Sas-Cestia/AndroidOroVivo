package fr.cestia.sinex_orvx

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import fr.cestia.common_files.screens.LoadingScreen
import fr.cestia.msaisie_inventaire.screen.SaisieInventaireScreen
import fr.cestia.sinex_orvx.screen.AccueilScreen
import fr.cestia.sinex_orvx.screen.ErrorScreen
import fr.cestia.sinex_orvx.screen.SelectionMagasinScreen

@Composable
fun MainNavGraph(navController: NavHostController, startDestination: String = "loading") {

    NavHost(navController = navController, startDestination = startDestination) {
        composable("loading") { LoadingScreen() }
        composable(
            "loading/{loadingMessage}",
            arguments = listOf(navArgument("loadingMessage") { type = NavType.StringType })
        ) { backStackEntry ->
            val loadingMessage = backStackEntry.arguments?.getString("loadingMessage")
            LoadingScreen(loadingMessage = loadingMessage.toString())
        }
        composable(
            "error/{errorMessage}",
            arguments = listOf(navArgument("errorMessage") { type = NavType.StringType })
        ) { backStackEntry ->
            val errorMessage = backStackEntry.arguments?.getString("errorMessage")
            ErrorScreen(message = errorMessage.toString())
        }
        composable("accueil") { AccueilScreen(navController) }
        composable("selectionMagasin") { SelectionMagasinScreen(navController) }
        composable("saisieInventaire") { SaisieInventaireScreen(navController) }
//        composable("consultationInventaire") { ConsultationInventaireScreen(navController) }
    }
}