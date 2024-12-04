package fr.cestia.data.dao.produit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere

@Dao
interface ProduitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMatieres(matieres: List<Matiere>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFamilles(familles: List<Famille>)

    @Query("SELECT * FROM mproduit_famille")
    suspend fun getAllFamilles(): List<Famille>

    @Query("SELECT * FROM mproduit_matiere")
    suspend fun getAllMatieres(): List<Matiere>

}