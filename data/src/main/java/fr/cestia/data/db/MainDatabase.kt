package fr.cestia.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.cestia.data.dao.MainDao
import fr.cestia.data.model.inventaire.InventaireEnCours
import fr.cestia.data.model.inventaire.Saisie
import fr.cestia.data.model.inventaire.StockInitial
import fr.cestia.data.model.parametres_generaux.ParametresGeneraux
import fr.cestia.data.model.produit.Famille
import fr.cestia.data.model.produit.Matiere

@Database(
    entities = [
        ParametresGeneraux::class,
        Matiere::class,
        Famille::class,
        InventaireEnCours::class,
        StockInitial::class,
        Saisie::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun sinexDao(): MainDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "sinex_orvx"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
