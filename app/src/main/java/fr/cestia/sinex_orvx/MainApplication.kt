package fr.cestia.sinex_orvx

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.HiltAndroidApp
import fr.cestia.data.dao.MainDao
import fr.cestia.data.model.produit.Famille
import fr.cestia.data.model.produit.Matiere
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication : Application() {

    private fun initializeDatabase(mainDao: MainDao) {

        Log.d("MainApplication", "initializeDatabase called")
        CoroutineScope(Dispatchers.IO).launch {
            // Vérifier si la table "matière" est vide
            if (mainDao.getMatiereCount() == 0) {
                // TODO: A remplacer par l'appel du webservice pour récupérer les matieres
                val matieres = listOf(
                    Matiere(code = "K", libelle = "Matière 1"),
                )
                matieres.forEach {
                    mainDao.insertMatiere(it)
                    Log.d("MainApplication", "Matière insérée: $it")
                }
            }

            // Vérifier si la table "famille" est vide
            if (mainDao.getFamilleCount() == 0) {
                // TODO: A remplacer par l'appel du webservice pour récupérer les familles
                val familles = listOf(
                    Famille(code = "G", libelle = "Famille 1"),
                )
                familles.forEach {
                    mainDao.insertFamille(it)
                    Log.d("MainApplication", "Famille insérée: $it")
                }
            }
        }
    }

    @Composable
    fun ScreenSizeInfo() {
        val configuration = LocalConfiguration.current

        // Largeur et hauteur en pixels
        val screenWidthPx = configuration.screenWidthDp * Resources.getSystem().displayMetrics.density
        val screenHeightPx = configuration.screenHeightDp * Resources.getSystem().displayMetrics.density

        Log.d("ScreenSizeInfo", "Largeur en dp: ${configuration.screenWidthDp}, Hauteur en dp: ${configuration.screenHeightDp}")
        Log.d("ScreenSizeInfo", "Largeur en pixels: $screenWidthPx, Hauteur en pixels: $screenHeightPx")
    }

    @Composable
    fun App(mainDao: MainDao) {
        val navController = rememberNavController()

        val isDatabaseInitialized = remember { mutableStateOf(false) }
        val isExistingInventaire = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            initializeDatabase(mainDao)
            isDatabaseInitialized.value = true
            isExistingInventaire.value = mainDao.getAllInventairesEnCours().isNotEmpty()
        }

        LaunchedEffect(isDatabaseInitialized) {
            if (isDatabaseInitialized.value) {
                if (isExistingInventaire.value) {
                    navController.navigate("accueil") {
                        // Clear backstack pour éviter de retourner à l'écran de chargement
                        popUpTo("loading") { inclusive = true }
                    }
                }
                else {
                    navController.navigate("selectionMagasin") {
                        // Clear backstack pour éviter de retourner à l'écran de chargement
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }
        }

        MainNavGraph(navController, mainDao)
    }
}