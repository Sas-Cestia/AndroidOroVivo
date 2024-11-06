package fr.cestia.data.dao

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.cestia.data.db.MainDatabase

@Module
@InstallIn(SingletonComponent::class)
object MainDaoModule {

    @Provides
    fun provideDao(database: MainDatabase): MainDao {
        Log.d("SinexDaoModule", "DAO injecté")
        return database.sinexDao()
    }
}