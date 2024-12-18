package fr.cestia.data.repositories.inventaire

import fr.cestia.data.models.inventaire.InventaireEnCours

interface InventaireRepository {
    suspend fun getInventaireEnCours(): InventaireEnCours
    suspend fun syncInventaireEnCours(codeMagasin: String): Boolean
}