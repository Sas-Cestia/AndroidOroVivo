package fr.cestia.data.webservices

import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere

interface RemoteDataSource {
    suspend fun fetchMatieresFamilles(): Pair<List<Matiere>, List<Famille>>

}