package wangdaye.com.geometricweather.common.basic.models.weather

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import java.io.Serializable

class UV(
    val index: Int?,
    val level: String?,
    val description: String?
) : Serializable {

    val isValid: Boolean
        get() = index != null || level != null || description != null

    val isValidIndex: Boolean
        get() = index != null

    @get:JvmName("getUVDescription")
    val uvDescription: String
        @SuppressLint("DefaultLocale")
        get() {
        val builder = StringBuilder()
        if (index != null) {
            builder.append(String.format("%d", index))
        }
        if (level != null) {
            builder.append(
                if (TextUtils.isEmpty(builder.toString())) "" else " "
            ).append(level)
        }
        if (description != null) {
            builder.append(
                if (TextUtils.isEmpty(builder.toString())) "" else "\n"
            ).append(description)
        }
        return builder.toString()
    }

    fun getShortUVDescription(): String {
        val builder = StringBuilder()
        if (index != null) {
            builder.append(index)
        }
        if (level != null) {
            builder.append(
                if (TextUtils.isEmpty(builder.toString())) "" else " "
            ).append(level)
        }
        return builder.toString()
    }

    @ColorInt
    fun getUVColor(context: Context): Int {
        return if (index == null) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (index <= UV_INDEX_LOW) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (index <= UV_INDEX_MIDDLE) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (index <= UV_INDEX_HIGH) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (index <= UV_INDEX_EXCESSIVE) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        }
    }

    companion object {
        const val UV_INDEX_LOW = 2
        const val UV_INDEX_MIDDLE = 5
        const val UV_INDEX_HIGH = 7
        const val UV_INDEX_EXCESSIVE = 10
    }
}
