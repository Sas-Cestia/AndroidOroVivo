package fr.cestia.sinex_orvx

import android.util.Log
import fr.cestia.common_files.barcode.DWManager
import fr.cestia.data.dao.inventaire.InventaireDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val inventaireDao: InventaireDao,
    private val dwManager: DWManager
) {

    fun initializeDataWedge(): Boolean {
        try {
            dwManager.configure()
            dwManager.registerDWReceiver()
            return true
        } catch (e: Exception) {
            Log.e("AppInitializer", "Erreur lors de la configuration de DataWedge", e)
            return false
        }
    }

}