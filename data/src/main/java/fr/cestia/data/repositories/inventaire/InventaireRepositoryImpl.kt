package fr.cestia.data.repositories.inventaire

import fr.cestia.data.dao.inventaire.InventaireDao
import fr.cestia.data.models.inventaire.InventaireEnCours
import fr.cestia.data.models.inventaire.StockInitial
import fr.cestia.data.webservices.RemoteDataSource

class InventaireRepositoryImpl(
    private val inventaireDao: InventaireDao,
    private val remoteDataSource: RemoteDataSource
) : InventaireRepository {

    override suspend fun getInventaireEnCours(): InventaireEnCours {
        return inventaireDao.getAllInventairesEnCours().first()
    }

    override suspend fun syncInventaireEnCours(codeMagasin: String): Boolean {
        try {
            val remoteInventaireEnCours =
                remoteDataSource.fetchQteInventaireVitrine(codeMagasin = codeMagasin)
            val codeInventaire = remoteInventaireEnCours.first().codeInventaire
            // TODO: Récupérer la date d'ouverture de l'inventaire depuis webservice
//            val dateOuverture = remoteInventaireEnCours.first().dateOuverture
            val dateOuverture = "2024-12-18"
            val inventaire = InventaireEnCours(
                codeMagasin = codeMagasin,
                codeInventaire = codeInventaire,
                dateOuverture = dateOuverture
            )
            inventaireDao.insertInventaireEnCours(inventaire)

            remoteInventaireEnCours.forEach { row ->
                val codeVitrine = row.codeVitrine
                val libVitrine = row.libVitrine
                val qte = row.qte
                val stockInitial = StockInitial(
                    codeVitrine = codeVitrine,
                    libVitrine = libVitrine,
                    quantite = qte
                )
                inventaireDao.insertStockInitial(stockInitial)
            }
            return true

        } catch (e: Exception) {
            throw e
        }
        return false
    }

}