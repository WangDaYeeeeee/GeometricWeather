package wangdaye.com.geometricweather.main.compose

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.main.MainActivityViewModel

/**
 * City-list swipe actions, matching [wangdaye.com.geometricweather.main.widgets.LocationItemTouchCallback].
 * Swipe toward the end (LTR: right) deletes; swipe toward the start toggles resident / opens providers.
 */
object LocationListActions {

    fun onSwipeTowardStart(
        activity: GeoActivity,
        viewModel: MainActivityViewModel,
        location: Location,
        onSelectProvider: () -> Unit,
    ) {
        if (location.isCurrentPosition) {
            onSelectProvider()
            return
        }
        val updated = Location.copy(
            location,
            location.isCurrentPosition,
            !location.isResidentPosition
        )
        viewModel.updateLocation(updated)
        if (updated.isResidentPosition) {
            SnackbarHelper.showSnackbar(
                activity.getString(R.string.feedback_resident_location),
                activity.getString(R.string.learn_more)
            ) {
                MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.resident_location)
                    .setMessage(R.string.feedback_resident_location_description)
                    .show()
            }
        }
    }

    fun onSwipeTowardEnd(
        activity: GeoActivity,
        viewModel: MainActivityViewModel,
        location: Location,
        position: Int,
    ) {
        val list = viewModel.totalLocationList.value?.locationList.orEmpty()
        if (list.size <= 1) {
            viewModel.updateLocation(location)
            SnackbarHelper.showSnackbar(
                activity.getString(R.string.feedback_location_list_cannot_be_null)
            )
            return
        }
        val deleted = viewModel.deleteLocation(position)
        SnackbarHelper.showSnackbar(
            activity.getString(R.string.feedback_delete_succeed),
            activity.getString(R.string.cancel)
        ) {
            viewModel.addLocation(deleted, position)
        }
    }
}
