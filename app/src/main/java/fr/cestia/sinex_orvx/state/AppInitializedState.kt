package fr.cestia.sinex_orvx.state

data class AppInitializedState(
    val isLoading: Boolean = true,
    val isDatawedgeInitialized: Boolean = false,
    val isDatabaseInitialized: Boolean = true,
    val isExistingInventaire: Boolean = false,
    val errorMessage: String? = null
)