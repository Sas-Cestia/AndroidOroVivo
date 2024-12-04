package fr.cestia.data.repositories.produit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.dao.produit.ProduitDao
import fr.cestia.data.webservices.RemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProduitRepositoryModule {

    @Provides
    @Singleton
    fun provideProduitRepository(
        produitDao: ProduitDao,
        remoteDataSource: RemoteDataSource
    ): ProduitRepository {
        return ProduitRepositoryImpl(produitDao, remoteDataSource)
    }
}