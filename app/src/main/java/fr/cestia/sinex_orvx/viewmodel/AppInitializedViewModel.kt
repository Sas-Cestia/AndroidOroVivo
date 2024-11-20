package fr.cestia.sinex_orvx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.sinex_orvx.AppInitializer
import fr.cestia.sinex_orvx.state.AppInitializedState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppInitializationViewModel @Inject constructor(
    private val appInitializer: AppInitializer,
) : ViewModel() {

    private val _state = MutableStateFlow(AppInitializedState())
    val state: StateFlow<AppInitializedState> = _state.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Initialisation des composants en parallèle
                val dataWedgeJob = async { appInitializer.initializeDataWedge() }
                val databaseJob = async { appInitializer.initializeDatabase() }
                val inventaireJob = async { appInitializer.hasExistingInventaire() }

                val isDatawedgeInitialized = dataWedgeJob.await()
                val isDatabaseInitialized = databaseJob.await()

                //TODO: Supprimer cette ligne après le test
                appInitializer.deleteAllInventairesEnCours()

                val isExistingInventaire = inventaireJob.await()

                // Mise à jour de l'état
                _state.value = AppInitializedState(
                    isLoading = false,
                    isDatawedgeInitialized = isDatawedgeInitialized,
                    isDatabaseInitialized = isDatabaseInitialized,
                    isExistingInventaire = isExistingInventaire
                )

            } catch (e: Exception) {
                Log.e("AppInitialization", "Erreur lors de l'initialisation", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erreur inconnue"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}