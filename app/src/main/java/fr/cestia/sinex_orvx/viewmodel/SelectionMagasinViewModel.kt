package fr.cestia.sinex_orvx.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.common_files.datawedge.ScannerManager
import fr.cestia.data.dao.MainDao
import fr.cestia.data.models.inventaire.InventaireEnCours
import fr.cestia.sinex_orvx.state.SelectionMagasinState
import javax.inject.Inject

@HiltViewModel
class SelectionMagasinViewModel @Inject constructor(
    private val mainDao: MainDao,
    val scannerManager: ScannerManager
) : ViewModel() {
    private val _selectionMagasinState =
        MutableLiveData<SelectionMagasinState>(SelectionMagasinState.Initial)
    val selectionMagasinState: LiveData<SelectionMagasinState> = _selectionMagasinState

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _scannedCode = MutableLiveData<String?>()
    val scannedCode: LiveData<String?> = _scannedCode

    private val observer = Observer<String> { scannedData ->
        try {
            _scannedCode.value = scannedData
        } catch (e: Exception) {
            Log.e("SelectionMagasinViewModel", "Erreur de décodage des données scannées", e)
            _errorMessage.value = "Erreur de décodage des données scannées : ${e.message}"
        }
    }


    init {
        scannerManager.registerReceiver()
        scannerManager.scannedData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        scannerManager.unregisterReceiver()
        scannerManager.resetScannedData()
        scannerManager.scannedData.removeObserver(observer)
    }

    fun updateScannedCode(newCode: String) {
        _scannedCode.value = newCode // Mettre à jour localement
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