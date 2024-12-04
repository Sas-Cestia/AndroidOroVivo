package fr.cestia.data.webservices

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.webservices.soapservice.SoapService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        soapService: SoapService
    ): RemoteDataSource {
        return RemoteDataSourceImpl(soapService)
    }
}