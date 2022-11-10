package com.life4.core.manager

import com.life4.core.manager.language.MyLanguageManager
import com.life4.core.manager.language.MyLanguageManagerImp
import com.life4.core.manager.resource.MyResourceManager
import com.life4.core.manager.resource.MyResourceManagerImp
import com.life4.core.manager.storage.MyStorageManager
import com.life4.core.manager.storage.MyStorageManagerImp
import com.life4.core.manager.theme.MyThemeManager
import com.life4.core.manager.theme.MyThemeManagerImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class ManagerModule {
    @Binds
    abstract fun provideLanguage(myLanguageHandlerImp: MyLanguageManagerImp): MyLanguageManager

    @Binds
    abstract fun provideResource(resourceInitializerImp: MyResourceManagerImp): MyResourceManager

    @Binds
    abstract fun provideStorage(prefStorageManagerImp: MyStorageManagerImp): MyStorageManager

    @Binds
    abstract fun provideTheme(myThemeManagerImp: MyThemeManagerImp): MyThemeManager
}
