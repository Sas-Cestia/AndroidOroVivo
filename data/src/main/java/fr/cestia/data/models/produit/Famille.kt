package fr.cestia.data.models.produit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mproduit_famille")
data class Famille (
    @PrimaryKey @ColumnInfo(name = "code") var code: String,
    @ColumnInfo(name = "libelle") var libelle: String
) {
    companion object {
        const val CODE_MAX_LENGTH = 1
    }
}