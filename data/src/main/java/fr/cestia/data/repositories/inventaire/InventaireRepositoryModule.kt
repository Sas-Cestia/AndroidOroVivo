package fr.cestia.data.repositories.inventaire

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.dao.inventaire.InventaireDao
import fr.cestia.data.webservices.RemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventaireRepositoryModule {

    @Provides
    @Singleton
    fun provideInventaireRepository(
        inventaireDao: InventaireDao,
        remoteDataSource: RemoteDataSource
    ): InventaireRepository {
        return InventaireRepositoryImpl(inventaireDao, remoteDataSource)
    }
}