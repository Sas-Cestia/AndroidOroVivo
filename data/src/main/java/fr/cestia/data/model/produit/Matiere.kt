package fr.cestia.data.model.produit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mproduit_matiere")
data class Matiere (
    @PrimaryKey @ColumnInfo(name = "code") var code: String,
    @ColumnInfo(name = "libelle") var libelle: String
) {
    companion object {
        const val CODE_MAX_LENGTH = 1
    }
}