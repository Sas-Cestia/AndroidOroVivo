package fr.cestia.data.repository

import fr.cestia.data.dao.MainDao
import fr.cestia.data.model.inventaire.InventaireEnCours
import fr.cestia.data.model.inventaire.Saisie
import fr.cestia.data.model.inventaire.StockInitial
import fr.cestia.data.model.parametres_generaux.ParametresGeneraux
import fr.cestia.data.model.produit.Famille
import fr.cestia.data.model.produit.Matiere

class MainRepository(private val dao: MainDao) {

    // ** Fonctions pour les paramètres généraux **

    suspend fun insertParametresGeneraux(parametresGeneraux: ParametresGeneraux) {
        dao.insertParametresGeneraux(parametresGeneraux)
    }

    suspend fun updateCodeLangage(id: Int, newCodeLangage: String): Boolean {
        return if (newCodeLangage.length <= ParametresGeneraux.CODE_LANGAGE_MAX_LENGTH) {
            dao.updateCodeLangage(id, newCodeLangage)
            true
        } else {
            false // Longueur non valide
        }
    }

    suspend fun deleteAllParametresGeneraux() {
        dao.deleteAllParametresGeneraux()
    }

    // ** Fonctions pour les matières **

    suspend fun insertMatiere(matiere: Matiere): Boolean {
        if (matiere.code.length <= Matiere.CODE_MAX_LENGTH) {
            dao.insertMatiere(matiere)
            return true
        } else {
            // Longueur non valide
            throw IllegalArgumentException("Longueur de code de matière non valide")
        }
    }

    suspend fun insertAllMatieres(matieres: List<Matiere>) {
        if (matieres.any { it.code.length > Matiere.CODE_MAX_LENGTH }) {
            // Une ou plusieurs matières ont une longueur de code invalide
            throw IllegalArgumentException("Longueur de code de matière non valide")
        }
        dao.insertMatieres(matieres)
    }

    suspend fun getAllMatieres(): List<Matiere> {
        return dao.getAllMatieres()
    }

    suspend fun getMatiereByCode(code: String): Matiere? {
        if (code.length > Matiere.CODE_MAX_LENGTH) {
            throw IllegalArgumentException("Longueur de code de matière non valide")
        }
        return dao.getMatiereByCode(code)
    }

    suspend fun updateMatiere(matiere: Matiere): Boolean {
        if (matiere.code.length <= Matiere.CODE_MAX_LENGTH) {
            dao.updateMatiere(matiere)
            return true
        } else {
            // Longueur non valide
            throw IllegalArgumentException("Longueur de code de matière non valide")
        }
    }

    suspend fun updateAllMatieres(matieres: List<Matiere>) {
        if (matieres.any { it.code.length > Matiere.CODE_MAX_LENGTH }) {
            // Une ou plusieurs matières ont une longueur de code invalide
            throw IllegalArgumentException("Longueur de code de matière non valide")
        }
        for (matiere in matieres) {
            dao.updateMatiere(matiere)
        }
    }

    suspend fun deleteAllMatieres() {
        dao.deleteAllMatieres()
    }

    // ** Fonctions pour les familles **

    suspend fun insertFamille(famille: Famille): Boolean {
        if (famille.code.length <= Famille.CODE_MAX_LENGTH) {
            dao.insertFamille(famille)
            return true
        } else {
            // Longueur non valide
            throw IllegalArgumentException("Longueur de code de famille non valide")
        }
    }

    suspend fun insertAllFamilles(familles: List<Famille>) {
        if (familles.any { it.code.length > Famille.CODE_MAX_LENGTH }) {
            // Une ou plusieurs familles ont une longueur de code invalide
            throw IllegalArgumentException("Longueur de code de famille non valide")
        }
        dao.insertFamilles(familles)
    }

    suspend fun getAllFamilles(): List<Famille> {
        return dao.getAllFamilles()
    }

    suspend fun getFamilleByCode(code: String): Famille? {
        if (code.length > Famille.CODE_MAX_LENGTH) {
            throw IllegalArgumentException("Longueur de code de famille non valide")
        }
        return dao.getFamilleByCode(code)
    }

    suspend fun updateFamille(famille: Famille): Boolean {
        if (famille.code.length <= Famille.CODE_MAX_LENGTH) {
            dao.updateFamille(famille)
            return true
        } else {
            // Longueur non valide
            throw IllegalArgumentException("Longueur de code de famille non valide")
        }
    }

    suspend fun updateAllFamilles(familles: List<Famille>) {
        if (familles.any { it.code.length > Famille.CODE_MAX_LENGTH }) {
            // Une ou plusieurs familles ont une longueur de code invalide
            throw IllegalArgumentException("Longueur de code de famille non valide")
        }
        for (famille in familles) {
            dao.updateFamille(famille)
        }
    }

    suspend fun deleteAllFamilles() {
        dao.deleteAllFamilles()
    }

    // ** Fonctions pour l'inventaire en cours **

    // Fonction pour vérifier la longueur d'une chaîne et insérer un InventaireEnCours
    suspend fun insertInventaireEnCours(inventaire: InventaireEnCours): Boolean {
        return if (inventaire.codeMagasin.length <= InventaireEnCours.CODE_MAGASIN_MAX_LENGTH) {
            dao.insertInventaireEnCours(inventaire)
            true
        } else {
            false // Longueur non valide
        }
    }

    suspend fun updateInventaireEnCours(inventaire: InventaireEnCours): Boolean {
        return if (inventaire.codeMagasin.length <= InventaireEnCours.CODE_MAGASIN_MAX_LENGTH) {
            dao.updateInventaireEnCours(inventaire)
            true
        } else {
            false // Longueur non valide
        }
    }

    suspend fun deleteAllInventairesEnCours() {
        dao.deleteAllInventairesEnCours()
    }

    // ** Fonctions pour les stocks initiaux **

    // Fonction pour valider et insérer un StockInitial
    suspend fun insertStockInitial(stockInitial: StockInitial): Boolean {
        if (stockInitial.codeVitrine.length > StockInitial.CODE_VITRINE_MAX_LENGTH) {
            return false // Longueur non valide
        } else {
            dao.insertStockInitial(stockInitial)
            return true
        }
    }

    suspend fun getAllStockInitiaux(): List<StockInitial> {
        return dao.getAllStockInitiaux()
    }

    suspend fun getStockInitialByCodeVitrine(codeVitrine: String): StockInitial? {
        return dao.getStockInitialByCodeVitrine(codeVitrine)
    }

    suspend fun updateStockInitial(stockInitial: StockInitial): Boolean {
        if (stockInitial.codeVitrine.length > StockInitial.CODE_VITRINE_MAX_LENGTH) {
            return false // Longueur non valide
        } else {
            dao.updateStockInitial(stockInitial)
            return true
        }
    }

    suspend fun deleteStockInitialByCodeVitrine(codeVitrine: String) {
        dao.deleteStockInitialByCodeVitrine(codeVitrine)
    }

    suspend fun deleteAllStockInitials() {
        dao.deleteAllStockInitials()
    }

    // ** Fonctions pour les saisies **

    // Fonction pour valider et insérer une Saisie
    suspend fun insertSaisie(saisie: Saisie): Boolean {
        if (saisie.codeArticle.length > Saisie.CODE_ARTICLE_MAX_LENGTH ||
            saisie.codeVitrine.length > Saisie.CODE_VITRINE_MAX_LENGTH ||
            saisie.codeStat5.length > Saisie.CODE_STAT5_MAX_LENGTH
        ) {
            return false // Longueur non valide
        }
        dao.insertSaisie(saisie)
        return true
    }

    suspend fun getSaisieByIdRfid(idRfid: String): Saisie? {
        return dao.getSaisieByIdRfid(idRfid)
    }

    suspend fun getSaisiesByCodeArticle(codeArticle: String): List<Saisie> {
        return dao.getSaisiesByCodeArticle(codeArticle)
    }

    suspend fun getSaisiesByCodeVitrine(codeVitrine: String): List<Saisie> {
        return dao.getSaisiesByCodeVitrine(codeVitrine)
    }

    suspend fun getSaisiesByCodeStat5(codeStat5: String): List<Saisie> {
        return dao.getSaisiesByCodeStat5(codeStat5)
    }

    suspend fun getSaisiesByCodeMatiere(codeMatiere: String): List<Saisie> {
        return dao.getSaisiesByCodeMatiere(codeMatiere)
    }

    suspend fun getSaisiesByCodeFamille(codeFamille: String): List<Saisie> {
        return dao.getSaisiesByCodeFamille(codeFamille)
    }

    suspend fun getAllSaisies(): List<Saisie> {
        return dao.getAllSaisies()
    }

    suspend fun deleteSaisieByIdRfid(idRfid: String) {
        dao.deleteSaisieByIdRfid(idRfid)
    }

    suspend fun deleteAllSaisies() {
        dao.deleteAllSaisies()
    }
}