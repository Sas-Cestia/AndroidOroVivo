package fr.cestia.sinex_orvx.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.sinex_orvx.ConfigurationGenerale
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = ConfigurationGenerale.BASE_URL_WEBSERVICE

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // Log tout, y compris le corps des requêtes/réponses
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Ajoute l' intercepteur de logging
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // Timeout de connexion
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)    // Timeout de lecture
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)   // Timeout d' écriture
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Utilise le client OkHttp avec l' intercepteur
            .addConverterFactory(ScalarsConverterFactory.create()) // Utilisation du converter pour le XML
            .build()
    }
}