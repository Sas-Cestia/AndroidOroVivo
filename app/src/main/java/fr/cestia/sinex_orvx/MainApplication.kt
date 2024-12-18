package fr.cestia.sinex_orvx

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.HiltAndroidApp
import fr.cestia.common_files.barcode.DWManager
import fr.cestia.sinex_orvx.viewmodel.AppInitializationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Default + job

    @Inject
    lateinit var dwManager: DWManager

    override fun onCreate() {
        super.onCreate()

    }

    fun endApp() {
        dwManager.unregisterDWReceiver()
        coroutineContext.cancel()
    }

    @Composable
    fun App(appInitializationViewModel: AppInitializationViewModel) {
        val navController = rememberNavController()
        val state by appInitializationViewModel.state.collectAsState()

        LaunchedEffect(state) {
            Log.d("NavigationDebug", "État actuel : $state")
            if (
                state.isExistingInventaire &&
                state.isMatieresFamillesLoaded &&
                state.isDatawedgeInitialized
            ) {
                navController.navigate("accueil") {
                    // Clear backstack pour éviter de retourner à l'écran de chargement
                    popUpTo("loading") { inclusive = true }
                }
            } else if (state.isMatieresFamillesLoaded && state.isDatawedgeInitialized) {
                navController.navigate("selectionMagasin") {
                    // Clear backstack pour éviter de retourner à l'écran de chargement
                    popUpTo("loading") { inclusive = true }
                }
            } else if (state.errorMessage != null) {
                navController.navigate("error/${state.errorMessage}/${state.actionOnRetry}") {
                    // Clear backstack pour éviter de retourner à l'écran de chargement
                    popUpTo("loading") { inclusive = true }
                }
            } else {
                navController.navigate("loading") {
                    // Clear backstack pour éviter de retourner à l'écran de chargement
                    popUpTo("loading") { inclusive = true }
                }
            }
        }

        MainNavGraph(navController)
    }
}
