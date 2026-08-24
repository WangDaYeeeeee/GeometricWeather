package wangdaye.com.geometricweather.common.basic

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class GeoViewModel(app: Application) : AndroidViewModel(app) {

    private var mNewInstance: Boolean = true

    fun checkIsNewInstance(): Boolean {
        val result = mNewInstance
        mNewInstance = false
        return result
    }
}
