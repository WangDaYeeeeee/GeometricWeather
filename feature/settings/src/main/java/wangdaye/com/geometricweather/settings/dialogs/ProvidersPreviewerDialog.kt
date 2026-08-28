package wangdaye.com.geometricweather.settings.dialogs

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AlertDialog
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wangdaye.com.geometricweather.common.basic.ApplicationContextHolder
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.settings.adapters.IconProviderAdapter
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

object ProvidersPreviewerDialog {

    const val ACTION_RESOURCE_PROVIDER_CHANGED =
        "com.wangdaye.geometricweather.RESOURCE_PROVIDER_CHANGED"
    const val KEY_PACKAGE_NAME = "package_name"

    fun interface OnProviderSelectedCallback {
        fun onProviderSelected(packageName: String)
    }

    @JvmStatic
    fun show(activity: Activity, callback: OnProviderSelectedCallback) {
        val view = LayoutInflater
            .from(activity)
            .inflate(R.layout.dialog_providers_previewer, null, false)
        initWidget(
            activity,
            view,
            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.settings_title_icon_provider)
                .setView(view)
                .show(),
            callback
        )
    }

    private fun initWidget(
        activity: Activity,
        view: View,
        dialog: AlertDialog,
        callback: OnProviderSelectedCallback
    ) {
        val progressView = view.findViewById<CircularProgressView>(R.id.dialog_providers_previewer_progress)
        progressView.visibility = View.VISIBLE

        val listView = view.findViewById<RecyclerView>(R.id.dialog_providers_previewer_list)
        listView.layoutManager = LinearLayoutManager(activity)
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val elevation = DisplayUtils.dpToPx(activity, 2f)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!listView.canScrollVertically(-1)) {
                    listView.translationZ = 0f
                } else {
                    listView.translationZ = elevation
                }
            }
        })
        listView.visibility = View.GONE

        AsyncHelper.runOnIO<List<ResourceProvider>>(
            { emitter ->
                emitter.send(ResourcesProviderFactory.getProviderList(ApplicationContextHolder.application), true)
            },
            { resourceProviders, _ ->
                bindAdapter(
                    activity,
                    listView,
                    progressView,
                    resourceProviders!!,
                    dialog,
                    callback
                )
            }
        )
    }

    private fun bindAdapter(
        activity: Activity,
        listView: RecyclerView,
        progressView: CircularProgressView,
        providerList: List<ResourceProvider>,
        dialog: AlertDialog,
        callback: OnProviderSelectedCallback
    ) {
        listView.adapter = IconProviderAdapter(
            activity,
            providerList,
            object : IconProviderAdapter.OnItemClickedListener {
                override fun onItemClicked(provider: ResourceProvider, adapterPosition: Int) {
                    callback.onProviderSelected(provider.packageName)
                    dialog.dismiss()
                }

                override fun onAppStoreItemClicked(query: String) {
                    IntentHelper.startAppStoreSearchActivity(activity, query)
                    dialog.dismiss()
                }

                override fun onGitHubItemClicked(query: String) {
                    IntentHelper.startWebViewActivity(activity, query)
                    dialog.dismiss()
                }
            }
        )

        val show: Animation = AlphaAnimation(0f, 1f)
        show.duration = 300
        show.interpolator = FastOutSlowInInterpolator()
        listView.startAnimation(show)
        listView.visibility = View.VISIBLE

        val out: Animation = AlphaAnimation(1f, 0f)
        show.duration = 300
        show.interpolator = FastOutSlowInInterpolator()
        progressView.startAnimation(out)
        progressView.visibility = View.GONE
    }
}
