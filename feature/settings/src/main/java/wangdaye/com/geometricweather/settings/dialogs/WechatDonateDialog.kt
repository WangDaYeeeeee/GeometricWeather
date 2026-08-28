package wangdaye.com.geometricweather.settings.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.helpers.ImageHelper

object WechatDonateDialog {

    @JvmStatic
    fun show(context: Context) {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_donate_wechat, null, false)
        val image = view.findViewById<AppCompatImageView>(R.id.dialog_donate_wechat_img)
        ImageHelper.load(context, image, R.drawable.donate_wechat)
        MaterialAlertDialogBuilder(context)
            .setView(view)
            .show()
    }
}
