package fr.cestia.data.dao.produit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.db.MainDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProduitDaoModule {
    @Provides
    @Singleton
    fun provideProduitDao(database: MainDatabase): ProduitDao {
        return database.produitDao()
    }
}