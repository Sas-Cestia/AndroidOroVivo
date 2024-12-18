package fr.cestia.sinex_orvx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.data.repositories.produit.ProduitRepository
import fr.cestia.sinex_orvx.AppInitializer
import fr.cestia.sinex_orvx.state.AppInitializerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppInitializerViewModel @Inject constructor(
    private val appInitializer: AppInitializer,
    private val produitRepository: ProduitRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AppInitializerState())
    val state: StateFlow<AppInitializerState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            initializeApp()
        }
    }

    private suspend fun performInitialization() {
        var isDatawedgeInitialized = false
        var isMatieresFamillesLoaded = false


        // Lancer des coroutines pour chaque tâche
        val dataWedgeJob = viewModelScope.launch {
            isDatawedgeInitialized = appInitializer.initializeDataWedge()
        }
        val matieresFamillesJob = viewModelScope.launch {
            isMatieresFamillesLoaded = syncMatieresFamilles()
        }

        // Attendre la fin des coroutines
        dataWedgeJob.join()
        matieresFamillesJob.join()

        if (isDatawedgeInitialized && isMatieresFamillesLoaded) {
            Log.d("AppInitialization", "Application initialisée avec succès")
            // Retourner l'état mis à jour
            _state.value = AppInitializerState(
                isLoading = false,
                isDatawedgeInitialized = isDatawedgeInitialized,
                isMatieresFamillesLoaded = isMatieresFamillesLoaded,
            )
        }
    }

    private suspend fun initializeApp() {
        _state.value = _state.value.copy(isLoading = true)

        try {
            performInitialization()
        } catch (e: Exception) {
            handleInitializationError(e)
        }

    }

    fun retryInitialization() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                performInitialization()
            } catch (e: Exception) {
                handleInitializationError(e)
            }
        }
    }

    private fun handleInitializationError(e: Exception) {
        val errorMessage = e.message ?: "Erreur inconnue lors de l'initialisation"

        Log.e("AppInitialization", errorMessage, e)

        _state.value = _state.value.copy(
            isLoading = false,
            errorMessage = errorMessage,
            actionOnRetry = "retryInitialization"
        )
    }

    fun loadMatieresFamilles() {
        viewModelScope.launch {
            try {
                produitRepository.getMatieresFamilles()
            } catch (e: Exception) {
                val errorMessage =
                    e.message
                        ?: "Erreur inconnue lors du chargement des matières et familles stockées localement."

                Log.e("AppInitialization", errorMessage, e)

                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    actionOnRetry = "retryInitialization"
                )
            }
        }
    }

    suspend fun syncMatieresFamilles(): Boolean {
        var responseIsSuccessful = false
        try {
            responseIsSuccessful = produitRepository.syncMatieresFamilles()
            Log.d("AppInitialization", "Matières et familles synchronisées")
        } catch (e: Exception) {
            val errorMessage = if (e.message != null) {
                "Erreur de chargement des matières et familles:\n" + e.message!!
            } else {
                "Erreur inconnue lors du chargement des matières et familles depuis le webservice. "
            }

            Log.e(
                "AppInitialization",
                errorMessage,
                e
            )
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = errorMessage,
                actionOnRetry = "retryInitialization"
            )
        }
        return responseIsSuccessful
    }

    override fun onCleared() {
        super.onCleared()
    }
}