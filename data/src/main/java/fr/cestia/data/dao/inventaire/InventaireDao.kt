package fr.cestia.data.dao.inventaire

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cestia.data.models.inventaire.InventaireEnCours
import fr.cestia.data.models.inventaire.StockInitial

@Dao
interface InventaireDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventaireEnCours(inventaireEnCours: InventaireEnCours)

    @Query("SELECT * FROM minventaire_inventaire_en_cours")
    suspend fun getAllInventairesEnCours(): List<InventaireEnCours>

    @Query("DELETE FROM minventaire_inventaire_en_cours")
    suspend fun deleteAllInventairesEnCours()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockInitial(stockInitial: StockInitial)

    @Query("SELECT * FROM minventaire_stock_ini")
    suspend fun getAllStockInitiaux(): List<StockInitial>

    @Query("DELETE FROM minventaire_stock_ini")
    suspend fun deleteAllStockInitiaux()

}