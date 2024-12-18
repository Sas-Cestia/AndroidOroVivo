package fr.cestia.data.dao.inventaire

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.db.MainDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventaireDaoModule {
    @Provides
    @Singleton
    fun provideInventaireDao(database: MainDatabase): InventaireDao {
        return database.inventaireDao()
    }
}