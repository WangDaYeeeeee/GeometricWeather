package wangdaye.com.geometricweather.common.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.settings.SettingsManager

@InstallIn(SingletonComponent::class)
@Module
class UtilsModule {

    @Provides
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper {
        return DatabaseHelper.getInstance(context)
    }

    @Provides
    fun provideSettingsOptionManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager.getInstance(context)
    }
}
