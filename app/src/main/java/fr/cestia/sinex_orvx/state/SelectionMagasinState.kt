package fr.cestia.sinex_orvx.state


sealed class SelectionMagasinState {
    data object Initial : SelectionMagasinState()
    data object Loading : SelectionMagasinState()
    data object Success : SelectionMagasinState()
    data object Error : SelectionMagasinState()
}