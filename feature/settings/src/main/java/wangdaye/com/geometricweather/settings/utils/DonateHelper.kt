package wangdaye.com.geometricweather.settings.utils

import android.didikee.donate.AlipayDonate
import android.didikee.donate.WeiXinDonate
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.settings.dialogs.WechatDonateDialog

object DonateHelper {

    @JvmStatic
    fun donateByAlipay(activity: GeoActivity) {
        if (AlipayDonate.hasInstalledAlipayClient(activity)) {
            AlipayDonate.startAlipayClient(activity, "fkx02882gqdh6imokjddj2a")
        } else {
            SnackbarHelper.showSnackbar("Alipay is not installed.")
        }
    }

    @JvmStatic
    fun donateByWechat(activity: GeoActivity) {
        if (WeiXinDonate.hasInstalledWeiXinClient(activity)) {
            WechatDonateDialog.show(activity)
        } else {
            SnackbarHelper.showSnackbar("WeChat is not installed.")
        }
    }
}
