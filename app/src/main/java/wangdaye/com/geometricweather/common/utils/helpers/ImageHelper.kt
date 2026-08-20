package wangdaye.com.geometricweather.common.utils.helpers

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.imageLoader
import coil.request.ImageRequest

object ImageHelper {

    @JvmStatic
    fun load(context: Context, target: ImageView, @DrawableRes resId: Int) {
        val request = ImageRequest.Builder(context)
            .data(resId)
            .target(target)
            .build()
        context.imageLoader.enqueue(request)
    }
}
