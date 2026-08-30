package wangdaye.com.geometricweather.common.utils.helpers

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.search.SearchActivity

fun IntentHelper.startSearchActivity(activity: Activity, bar: View) {
    activity.startActivity(
        Intent(activity, SearchActivity::class.java),
        ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            Pair.create(bar, activity.getString(R.string.transition_activity_search_bar))
        ).toBundle()
    )
}
