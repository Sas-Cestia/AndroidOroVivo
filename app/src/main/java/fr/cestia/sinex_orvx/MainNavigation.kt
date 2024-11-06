package fr.cestia.sinex_orvx

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.cestia.data.dao.MainDao
import fr.cestia.sinex_orvx.screen.AccueilScreen
import fr.cestia.sinex_orvx.screen.LoadingAppScreen
import fr.cestia.sinex_orvx.screen.SelectionMagasinScreen

@Composable
fun MainNavGraph(navController: NavHostController, mainDao: MainDao) {
    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") { LoadingAppScreen() }
        composable("accueil") { AccueilScreen(navController) }
        composable("selectionMagasin") { SelectionMagasinScreen(navController) }
//        composable("saisieInventaire") { SaisieInventaireScreen(navController) }
//        composable("consultationInventaire") { ConsultationInventaireScreen(navController) }
    }
}