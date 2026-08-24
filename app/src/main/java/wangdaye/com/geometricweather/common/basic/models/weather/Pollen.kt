package wangdaye.com.geometricweather.common.basic.models.weather

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import java.io.Serializable

class Pollen(
    val grassIndex: Int?,
    val grassLevel: Int?,
    val grassDescription: String?,
    val moldIndex: Int?,
    val moldLevel: Int?,
    val moldDescription: String?,
    val ragweedIndex: Int?,
    val ragweedLevel: Int?,
    val ragweedDescription: String?,
    val treeIndex: Int?,
    val treeLevel: Int?,
    val treeDescription: String?
) : Serializable {

    val isValid: Boolean
        get() = (grassIndex != null && grassIndex > 0 && grassLevel != null)
            || (moldIndex != null && moldIndex > 0 && moldLevel != null)
            || (ragweedIndex != null && ragweedIndex > 0 && ragweedLevel != null)
            || (treeIndex != null && treeIndex > 0 && treeLevel != null)

    companion object {
        @JvmStatic
        @ColorInt
        fun getPollenColor(context: Context, level: Int?): Int {
            return if (level == null) {
                ContextCompat.getColor(context, R.color.colorLevel_1)
            } else if (level <= 1) {
                ContextCompat.getColor(context, R.color.colorLevel_1)
            } else if (level <= 2) {
                ContextCompat.getColor(context, R.color.colorLevel_2)
            } else if (level <= 3) {
                ContextCompat.getColor(context, R.color.colorLevel_3)
            } else if (level <= 4) {
                ContextCompat.getColor(context, R.color.colorLevel_4)
            } else if (level <= 5) {
                ContextCompat.getColor(context, R.color.colorLevel_5)
            } else {
                ContextCompat.getColor(context, R.color.colorLevel_6)
            }
        }
    }
}
