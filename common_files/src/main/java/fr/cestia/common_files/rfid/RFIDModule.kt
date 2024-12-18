package fr.cestia.common_files.rfid

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RFIDModule {

    @Provides
    @Singleton
    fun provideRFIDManager(@ApplicationContext context: Context): IRFIDManager {
        return RFIDManager(context)
    }
}