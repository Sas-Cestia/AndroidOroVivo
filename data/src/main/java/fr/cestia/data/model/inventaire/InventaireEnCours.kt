package fr.cestia.data.model.inventaire

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "minventaire_inventaire_en_cours")
data class InventaireEnCours (
    @PrimaryKey @ColumnInfo(name = "id") val id:Int = 0,
    @ColumnInfo(name = "code_magasin") var codeMagasin: String,
    @ColumnInfo(name = "date_ouverture") var dateOuverture: String
) {
    companion object {
        const val CODE_MAGASIN_MAX_LENGTH = 3
    }
}