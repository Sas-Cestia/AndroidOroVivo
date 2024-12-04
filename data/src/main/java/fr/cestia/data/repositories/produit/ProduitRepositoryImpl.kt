package fr.cestia.data.repositories.produit

import fr.cestia.data.dao.produit.ProduitDao
import fr.cestia.data.models.produit.MatieresFamilles
import fr.cestia.data.webservices.RemoteDataSource

class ProduitRepositoryImpl(
    private val produitDao: ProduitDao,
    private val remoteDataSource: RemoteDataSource
) : ProduitRepository {

    override suspend fun getMatieresFamilles(): MatieresFamilles {
        // Récupération des données
        val matieres = produitDao.getAllMatieres()
        val familles = produitDao.getAllFamilles()

        // Retourne les deux listes encapsulées dans un objet
        return MatieresFamilles(matieres = matieres, familles = familles)
    }

    override suspend fun syncMatieresFamilles(): Boolean {
        try {
            // Appelle le webservice pour récupérer les données distantes
            val (remoteMatieres, remoteFamilles) = remoteDataSource.fetchMatieresFamilles()
            // Met à jour les données locales
            produitDao.insertAllMatieres(remoteMatieres)
            produitDao.insertAllFamilles(remoteFamilles)
            return true
        } catch (e: Exception) {
            throw e
        }
        return false
    }
}