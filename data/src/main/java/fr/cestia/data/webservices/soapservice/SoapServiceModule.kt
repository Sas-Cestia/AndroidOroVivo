package fr.cestia.data.webservices.soapservice

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SoapServiceModule {

    @Provides
    @Singleton
    fun provideSoapServiceModule(retrofit: Retrofit): SoapService {
        return retrofit.create(SoapService::class.java)
    }
}