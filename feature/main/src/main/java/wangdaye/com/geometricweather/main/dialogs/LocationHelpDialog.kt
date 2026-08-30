package wangdaye.com.geometricweather.main.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.main.MainActivity

object LocationHelpDialog {

    @JvmStatic
    fun show(activity: Activity) {
        val view = LayoutInflater
            .from(activity)
            .inflate(R.layout.dialog_location_help, null, false)
        initWidget(
            activity,
            view,
            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.feedback_location_help_title)
                .setView(view)
                .show()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget(activity: Activity, view: View, dialog: AlertDialog) {
        view.findViewById<View>(R.id.dialog_location_help_permissionContainer)
            .setOnClickListener { IntentHelper.startApplicationDetailsActivity(activity) }

        view.findViewById<View>(R.id.dialog_location_help_locationContainer)
            .setOnClickListener { IntentHelper.startLocationSettingsActivity(activity) }

        view.findViewById<View>(R.id.dialog_location_help_providerContainer)
            .setOnClickListener { IntentHelper.startSelectProviderActivity(activity) }

        view.findViewById<View>(R.id.dialog_location_help_manageContainer).setOnClickListener {
            if (activity is MainActivity) {
                activity.setManagementFragmentVisibility(true)
            } else {
                IntentHelper.startMainActivityForManagement(activity)
            }
            dialog.dismiss()
        }
        (view.findViewById<View>(R.id.dialog_location_help_manageTitle) as TextView).text =
            activity.getString(R.string.feedback_add_location_manually).replace(
                "$", activity.getString(R.string.current_location)
            )
    }
}
