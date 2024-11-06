package fr.cestia.sinex_orvx.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.cestia.data.dao.MainDao
import fr.cestia.data.model.inventaire.InventaireEnCours
import fr.cestia.sinex_orvx.state.SelectionMagasinState
import javax.inject.Inject

@HiltViewModel
class SelectionMagasinViewModel @Inject constructor(
    private val mainDao: MainDao
) : ViewModel() {
    private val _selectionMagasinState =
        MutableLiveData<SelectionMagasinState>(SelectionMagasinState.Initial)
    val selectionMagasinState: LiveData<SelectionMagasinState> = _selectionMagasinState

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

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