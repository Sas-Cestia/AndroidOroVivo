package fr.cestia.data.repositories.produit

import fr.cestia.data.models.produit.MatieresFamilles

interface ProduitRepository {
    suspend fun getMatieresFamilles(): MatieresFamilles
    suspend fun syncMatieresFamilles(): Boolean
}