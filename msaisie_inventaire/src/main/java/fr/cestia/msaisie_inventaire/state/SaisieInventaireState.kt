package fr.cestia.msaisie_inventaire.state

sealed class SaisieInventaireState {
    data object Initial : SaisieInventaireState()
    data object Loading : SaisieInventaireState()
    data object Success : SaisieInventaireState()
    data object Error : SaisieInventaireState()
}