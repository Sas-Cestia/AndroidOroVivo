package fr.cestia.sinex_orvx.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.cestia.sinex_orvx.state.AccueilState
import javax.inject.Inject

@HiltViewModel
class AccueilViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableLiveData<AccueilState>(AccueilState.Initial)
    val state: LiveData<AccueilState> = _state

    fun handleClickOnSaisieInventaire() {
        // Gérer l'événement de clic sur le bouton "Saisie d'inventaire"
        _state.value = AccueilState.Loading
    }

    fun handleClickOnConsultationInventaire() {
        // Gérer l'événement de clic sur le bouton "Consultation d'inventaire"
        _state.value = AccueilState.Loading
    }
}