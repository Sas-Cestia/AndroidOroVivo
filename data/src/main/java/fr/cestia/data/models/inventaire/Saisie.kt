package fr.cestia.data.models.inventaire

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import fr.cestia.data.models.produit.Famille
import fr.cestia.data.models.produit.Matiere

@Entity(
    tableName = "minventaire_saisie",
    foreignKeys = [
        ForeignKey(
            entity = Matiere::class,
            parentColumns = ["code"],
            childColumns = ["code_matiere_id"]
        ),
        ForeignKey(
            entity = Famille::class,
            parentColumns = ["code"],
            childColumns = ["code_famille_id"]
        )
    ],
    indices = [Index("code_matiere_id"), Index("code_famille_id")]
)
data class Saisie (
    @PrimaryKey @ColumnInfo(name = "id_rfid") var idRfid: String,
    @ColumnInfo(name = "code_article") var codeArticle: String,
    @ColumnInfo(name = "quantite") var quantite: Float,
    @ColumnInfo(name = "code_vitrine") var codeVitrine: String,
    @ColumnInfo(name = "code_stat5") var codeStat5: String,
    @ColumnInfo(name = "code_matiere_id") var codeMatiere: String,
    @ColumnInfo(name = "code_famille_id") var codeFamille: String,
) {
    companion object {
        const val CODE_ARTICLE_MAX_LENGTH = 10
        const val CODE_VITRINE_MAX_LENGTH = 10
        const val CODE_STAT5_MAX_LENGTH = 5
    }
}