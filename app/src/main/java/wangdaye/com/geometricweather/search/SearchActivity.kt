package wangdaye.com.geometricweather.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.search.compose.SearchScreen
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

@AndroidEntryPoint
class SearchActivity : GeoActivity() {

    private lateinit var viewModel: SearchActivityViewModel

    companion object {
        const val KEY_LOCATION = "location"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SearchActivityViewModel::class.java]
        val existingFormattedIds = DatabaseHelper.getInstance(this)
            .readLocationList()
            .map { it.formattedId }
            .toSet()

        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                SearchScreen(
                    viewModel = viewModel,
                    existingFormattedIds = existingFormattedIds,
                    onClose = { location -> finishSelf(location) },
                )
            }
        }
    }

    private fun finishSelf(location: Location?) {
        setResult(RESULT_OK, Intent().putExtra(KEY_LOCATION, location))
        ActivityCompat.finishAfterTransition(this)
    }
}
