package fr.cestia.common_files.barcode

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DWModule {

    @Provides
    @Singleton
    fun provideDWManager(@ApplicationContext context: Context): DWManager {
        return DWManager(context)
    }
}