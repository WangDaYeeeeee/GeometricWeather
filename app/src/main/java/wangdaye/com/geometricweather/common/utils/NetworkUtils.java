package wangdaye.com.geometricweather.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean isAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
            NetworkInfo vpnInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_VPN);
            return activeNetworkInfo != null || (vpnInfo != null && vpnInfo.isConnected());
        }
        return false;
    }
}