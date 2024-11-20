package fr.cestia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.cestia.data.models.inventaire.InventaireEnCours
import fr.cestia.data.models.inventaire.Saisie
import fr.cestia.data.models.inventaire.StockInitial
import fr.cestia.data.models.parametres_generaux.ParametresGeneraux
import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere

@Dao
interface MainDao {

    // ** DAO pour ParametresGeneraux **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParametresGeneraux(parametresGeneraux: ParametresGeneraux)

    @Query("SELECT * FROM mparam_gen WHERE id = :id")
    suspend fun getParametresGenerauxById(id: Int): ParametresGeneraux?

    @Query("SELECT * FROM mparam_gen")
    suspend fun getAllParametresGeneraux(): List<ParametresGeneraux>

    @Update
    suspend fun updateParametresGeneraux(parametresGeneraux: ParametresGeneraux)

    @Query("UPDATE mparam_gen SET code_langage = :newCodeLangage WHERE id = :id")
    suspend fun updateCodeLangage(id: Int, newCodeLangage: String)

    @Delete
    suspend fun deleteParametresGeneraux(parametresGeneraux: ParametresGeneraux)

    @Query("DELETE FROM mparam_gen WHERE id = :id")
    suspend fun deleteParametresGenerauxById(id: Int)

    @Query("DELETE FROM mparam_gen")
    suspend fun deleteAllParametresGeneraux()

    // ** DAO pour Matiere Produit **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatiere(matiere: Matiere)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatieres(matieres: List<Matiere>)

    @Query("SELECT COUNT(*) FROM mproduit_matiere")
    suspend fun getMatiereCount(): Int

    @Query("SELECT * FROM mproduit_matiere WHERE code = :code")
    suspend fun getMatiereByCode(code: String): Matiere?

    @Query("SELECT * FROM mproduit_matiere")
    suspend fun getAllMatieres(): List<Matiere>

    @Update
    suspend fun updateMatiere(matiere: Matiere)

    @Delete
    suspend fun deleteMatiere(matiere: Matiere)

    @Query("DELETE FROM mproduit_matiere WHERE code = :code")
    suspend fun deleteMatiereByCode(code: String)

    @Query("DELETE FROM mproduit_matiere")
    suspend fun deleteAllMatieres()

    // ** DAO pour Famille Produit **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamille(famille: Famille)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilles(familles: List<Famille>)

    @Query("SELECT COUNT(*) FROM mproduit_famille")
    suspend fun getFamilleCount(): Int

    @Query("SELECT * FROM mproduit_famille WHERE code = :code")
    suspend fun getFamilleByCode(code: String): Famille?

    @Query("SELECT * FROM mproduit_famille")
    suspend fun getAllFamilles(): List<Famille>

    @Update
    suspend fun updateFamille(famille: Famille)

    @Delete
    suspend fun deleteFamille(famille: Famille)

    @Query("DELETE FROM mproduit_famille WHERE code = :code")
    suspend fun deleteFamilleByCode(code: String)

    @Query("DELETE FROM mproduit_famille")
    suspend fun deleteAllFamilles()

    // ** DAO pour InventaireEnCours **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventaireEnCours(inventaireEnCours: InventaireEnCours)

    @Query("SELECT * FROM minventaire_inventaire_en_cours WHERE id = :id")
    suspend fun getInventaireEnCoursById(id: Int): InventaireEnCours?

    @Query("SELECT * FROM minventaire_inventaire_en_cours WHERE code_magasin = :codeMagasin")
    suspend fun getInventaireEnCoursByCodeMagasin(codeMagasin: String): InventaireEnCours?

    @Query("SELECT * FROM minventaire_inventaire_en_cours")
    suspend fun getAllInventairesEnCours(): List<InventaireEnCours>

    @Update
    suspend fun updateInventaireEnCours(inventaireEnCours: InventaireEnCours)

    @Delete
    suspend fun deleteInventaireEnCours(inventaireEnCours: InventaireEnCours)

    @Query("DELETE FROM minventaire_inventaire_en_cours WHERE id = :id")
    suspend fun deleteInventaireEnCoursById(id: Int)

    @Query("DELETE FROM minventaire_inventaire_en_cours")
    suspend fun deleteAllInventairesEnCours()

    // ** DAO pour StockInitial **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockInitial(stockInitial: StockInitial)

    @Query("SELECT * FROM minventaire_stock_ini WHERE code_vitrine = :codeVitrine")
    suspend fun getStockInitialByCodeVitrine(codeVitrine: String): StockInitial?

    @Query("SELECT * FROM minventaire_stock_ini WHERE id = :id")
    suspend fun getStockInitialById(id: Int): StockInitial?

    @Query("SELECT * FROM minventaire_stock_ini")
    suspend fun getAllStockInitiaux(): List<StockInitial>

    @Update
    suspend fun updateStockInitial(stockInitial: StockInitial)

    @Delete
    suspend fun deleteStockInitial(stockInitial: StockInitial)

    @Query("DELETE FROM minventaire_stock_ini WHERE code_vitrine = :codeVitrine")
    suspend fun deleteStockInitialByCodeVitrine(codeVitrine: String)

    @Query("DELETE FROM minventaire_stock_ini WHERE id = :id")
    suspend fun deleteStockInitialById(id: Int)

    @Query("DELETE FROM minventaire_stock_ini")
    suspend fun deleteAllStockInitials()

    // ** DAO pour Saisie **

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaisie(saisie: Saisie)

    @Query("SELECT * FROM minventaire_saisie WHERE code_article = :codeArticle")
    suspend fun getSaisiesByCodeArticle(codeArticle: String): List<Saisie>

    @Query("SELECT * FROM minventaire_saisie WHERE code_vitrine = :codeVitrine")
    suspend fun getSaisiesByCodeVitrine(codeVitrine: String): List<Saisie>

    @Query("SELECT * FROM minventaire_saisie WHERE code_stat5 = :codeStat5")
    suspend fun getSaisiesByCodeStat5(codeStat5: String): List<Saisie>

    @Query("SELECT * FROM minventaire_saisie WHERE code_matiere_id = :codeMatiere")
    suspend fun getSaisiesByCodeMatiere(codeMatiere: String): List<Saisie>

    @Query("SELECT * FROM minventaire_saisie WHERE code_famille_id = :codeFamille")
    suspend fun getSaisiesByCodeFamille(codeFamille: String): List<Saisie>

    @Query("SELECT * FROM minventaire_saisie WHERE id_rfid = :idRfid")
    suspend fun getSaisieByIdRfid(idRfid: String): Saisie?

    @Query("SELECT * FROM minventaire_saisie")
    suspend fun getAllSaisies(): List<Saisie>

    @Delete
    suspend fun deleteSaisie(saisie: Saisie)

    @Query("DELETE FROM minventaire_saisie WHERE id_rfid = :idRfid")
    suspend fun deleteSaisieByIdRfid(idRfid: String)

    @Query("DELETE FROM minventaire_saisie")
    suspend fun deleteAllSaisies()
}
