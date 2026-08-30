package wangdaye.com.geometricweather.main.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import wangdaye.com.geometricweather.settings.ConfigStore
import javax.inject.Inject

class StatementManager @Inject constructor(
    @ApplicationContext context: Context
) {
    var isLocationPermissionDeclared: Boolean
        private set
    var isBackgroundLocationDeclared: Boolean
        private set
    var isPostNotificationRequired: Boolean
        private set

    init {
        val config = ConfigStore.getInstance(context, SP_STATEMENT_RECORD)
        isLocationPermissionDeclared = config.getBoolean(KEY_LOCATION_PERMISSION_DECLARED, false)
        isBackgroundLocationDeclared = config.getBoolean(KEY_BACKGROUND_LOCATION_DECLARED, false)
        isPostNotificationRequired = config.getBoolean(KEY_POST_NOTIFICATION_REQUIRED, false)
    }

    fun setLocationPermissionDeclared(context: Context) {
        isLocationPermissionDeclared = true
        ConfigStore.getInstance(context, SP_STATEMENT_RECORD)
            .edit()
            .putBoolean(KEY_LOCATION_PERMISSION_DECLARED, true)
            .apply()
    }

    fun setBackgroundLocationDeclared(context: Context) {
        isBackgroundLocationDeclared = true
        ConfigStore.getInstance(context, SP_STATEMENT_RECORD)
            .edit()
            .putBoolean(KEY_BACKGROUND_LOCATION_DECLARED, true)
            .apply()
    }

    fun setPostNotificationRequired(context: Context) {
        isPostNotificationRequired = true
        ConfigStore.getInstance(context, SP_STATEMENT_RECORD)
            .edit()
            .putBoolean(KEY_POST_NOTIFICATION_REQUIRED, true)
            .apply()
    }

    companion object {
        private const val SP_STATEMENT_RECORD = "statement_record"
        private const val KEY_LOCATION_PERMISSION_DECLARED = "location_permission_declared"
        private const val KEY_BACKGROUND_LOCATION_DECLARED = "background_location_declared"
        private const val KEY_POST_NOTIFICATION_REQUIRED = "post_notification_required"
    }
}
