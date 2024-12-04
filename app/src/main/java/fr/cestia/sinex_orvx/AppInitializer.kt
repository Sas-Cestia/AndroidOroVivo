package fr.cestia.sinex_orvx

import fr.cestia.common_files.datawedge.DWConfig
import fr.cestia.data.dao.MainDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val mainDao: MainDao,
    private val dwConfig: DWConfig
) {

    fun initializeDataWedge(): Boolean {
        return dwConfig.initialize()
    }

    suspend fun hasExistingInventaire(): Boolean {
        return mainDao.getAllInventairesEnCours().isNotEmpty()
    }

}