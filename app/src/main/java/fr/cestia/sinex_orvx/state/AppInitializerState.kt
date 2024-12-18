package fr.cestia.sinex_orvx.state

data class AppInitializerState(
    val isLoading: Boolean = true,
    val isDatawedgeInitialized: Boolean = false,
    val isMatieresFamillesLoaded: Boolean = false,
    val errorMessage: String? = null,
    val actionOnRetry: String? = null
)