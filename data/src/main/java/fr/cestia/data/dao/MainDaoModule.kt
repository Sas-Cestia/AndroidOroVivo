package fr.cestia.data.dao

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.db.MainDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainDaoModule {
    @Provides
    @Singleton
    fun provideDao(database: MainDatabase): MainDao {
        Log.d("SinexDaoModule", "DAO injecté")
        return database.sinexDao()
    }
}