package fr.cestia.data.models.inventaire

data class QteInventaireVitrine(
    val codeInventaire: String,
    val codeVitrine: String,
    val libVitrine: String,
    val qte: Float,
    val qteConfie: Float,
)