package fr.cestia.data.models.inventaire

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "minventaire_stock_ini")
data class StockInitial (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id:Int = 0,
    @ColumnInfo(name = "code_vitrine") var codeVitrine: String,
    @ColumnInfo(name = "lib_vitrine") var libVitrine: String?,
    @ColumnInfo(name = "quantite") var quantite: Float
) {
    companion object {
        const val CODE_VITRINE_MAX_LENGTH = 3
    }
}