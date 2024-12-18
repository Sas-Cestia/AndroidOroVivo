package fr.cestia.sinex_orvx.state

sealed class AccueilState {
    data object Initial : AccueilState()
    data object Loading : AccueilState()
    data object Success : AccueilState()
    data object Error : AccueilState()
}
