package fr.cestia.sinex_orvx.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.common_files.barcode.DWManager
import fr.cestia.data.dao.MainDao
import fr.cestia.data.models.inventaire.InventaireEnCours
import fr.cestia.sinex_orvx.state.SelectionMagasinState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectionMagasinViewModel @Inject constructor(
    private val mainDao: MainDao,
    private val dwManager: DWManager,
) : ViewModel() {
    private val _selectionMagasinState =
        MutableLiveData<SelectionMagasinState>(SelectionMagasinState.Initial)
    val selectionMagasinState: LiveData<SelectionMagasinState> = _selectionMagasinState

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    val scannedCode = dwManager.scannedCode

    private val _enteredCode = MutableLiveData("")
    val enteredCode: LiveData<String> = _enteredCode

    init {
        // Synchronise enteredCode avec scannedCode
        viewModelScope.launch {
            scannedCode.collect { newCode ->
                _enteredCode.value = newCode
            }
        }
    }

    fun updateScannedCode(newCode: String) {
        _enteredCode.value = newCode // Mettre à jour localement
    }

    suspend fun handleQrCodeScan(qrCodeContent: String) {
        _errorMessage.value = ""
        _selectionMagasinState.value = SelectionMagasinState.Loading

        try {
            // TODO: Appeler webservice pour récupérer date d'ouverture
            if (qrCodeContent != "042") {
                throw Exception("Code QR invalide")
            }
            mainDao.insertInventaireEnCours(
                InventaireEnCours(id = 0, codeMagasin = qrCodeContent, dateOuverture = "2024-11-06")
            )
            _selectionMagasinState.value = SelectionMagasinState.Success
        } catch (e: Exception) {
            Log.e("SelectionMagasinViewModel", "Erreur lors de la sélection du magasin", e)
            _errorMessage.value = e.message ?: "Erreur lors de la sélection du magasin: ${e.message}"
            _selectionMagasinState.value = SelectionMagasinState.Error
        }
    }
}