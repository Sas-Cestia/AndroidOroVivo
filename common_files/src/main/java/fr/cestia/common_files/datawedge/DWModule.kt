package fr.cestia.common_files.datawedge

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DWModule {

    @Provides
    @Singleton
    fun provideDWInterface(): DWInterface {
        return DWInterfaceImpl()
    }
}
