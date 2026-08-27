package wangdaye.com.geometricweather.common.snackbar

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner

class SnackbarContainer(
    @JvmField val owner: LifecycleOwner?,
    @JvmField val container: ViewGroup,
    @JvmField val cardStyle: Boolean
)
