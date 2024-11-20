package fr.cestia.sinex_orvx

import android.util.Log
import fr.cestia.data.dao.MainDao
import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere
import javax.inject.Inject

class DatabaseInitializer @Inject constructor(private val mainDao: MainDao) {
    suspend fun initializeDatabase(): Boolean {
        Log.d("DatabaseInitializer", "initializeDatabase called")

        var isDatabaseInitialized = false

        try {
            if (mainDao.getMatiereCount() == 0) {
                // TODO: A remplacer par l'appel du webservice pour récupérer les matieres
                val matieres = listOf(
                    Matiere(code = "K", libelle = "Matière 1"),
                )
                mainDao.insertMatieres(matieres)
            }
            if (mainDao.getFamilleCount() == 0) {
                // TODO: A remplacer par l'appel du webservice pour récupérer les familles
                val familles = listOf(
                    Famille(code = "G", libelle = "Famille 1"),
                )
                mainDao.insertFamilles(familles)
            }

            isDatabaseInitialized = true

            Log.d("DatabaseInitializer", "Database initialized")

        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Error initializing database", e)
            isDatabaseInitialized = false
        }

        return isDatabaseInitialized
    }
}