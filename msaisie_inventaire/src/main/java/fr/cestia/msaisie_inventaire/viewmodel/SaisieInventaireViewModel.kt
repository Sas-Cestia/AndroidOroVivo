package fr.cestia.msaisie_inventaire.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.common_files.datawedge.ScannerManager
import fr.cestia.common_files.tools.decodeHex
import fr.cestia.data.dao.MainDao
import fr.cestia.msaisie_inventaire.state.SaisieInventaireState
import javax.inject.Inject

@HiltViewModel
class SaisieInventaireViewModel @Inject constructor(
    private val mainDao: MainDao,
    val scannerManager: ScannerManager,
) : ViewModel() {
    private val _saisieInventaireState =
        MutableLiveData<SaisieInventaireState>(SaisieInventaireState.Initial)
    val saisieInventaireState: LiveData<SaisieInventaireState> = _saisieInventaireState

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _scannedTags = MutableLiveData<List<String>>()
    val scannedTags: LiveData<List<String>> = _scannedTags

    private val observer = Observer<String> { scannedData ->
        try {
            Log.d("SaisieInventaireViewModel", "Scanned data: $scannedData")
            _scannedTags.value = scannedData.split("\n").map {
                Log.d("SaisieInventaireViewModel", "Chaine à décoder: $it")
                it.decodeHex()
            }

            Log.d("SaisieInventaireViewModel", "Decoded tags: ${_scannedTags.value}")
        } catch (e: Exception) {
            Log.e("SaisieInventaireViewModel", "Erreur de décodage des données scannées", e)
            _errorMessage.value = "Erreur de décodage des données scannées : ${e.message}"
        }
    }

    init {
        scannerManager.registerReceiver()
        scannerManager.scannedData.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        scannerManager.scannedData.removeObserver(observer)
        scannerManager.resetScannedData()
        scannerManager.unregisterReceiver()
    }
}
