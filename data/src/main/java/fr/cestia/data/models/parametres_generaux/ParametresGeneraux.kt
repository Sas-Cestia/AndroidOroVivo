package fr.cestia.data.models.parametres_generaux

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mparam_gen")
data class ParametresGeneraux(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "code_langage") var codeLangage: String,
) {
    companion object {
        const val CODE_LANGAGE_MAX_LENGTH = 2
    }
}
