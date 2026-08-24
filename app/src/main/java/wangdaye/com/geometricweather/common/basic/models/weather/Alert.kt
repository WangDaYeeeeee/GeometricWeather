package wangdaye.com.geometricweather.common.basic.models.weather

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import java.io.Serializable
import java.util.Date

class Alert(
    val alertId: Long,
    val date: Date,
    val time: Long,
    val description: String,
    val content: String,
    val type: String,
    val priority: Int,
    @ColorInt val color: Int
) : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
        alertId = parcel.readLong(),
        date = parcel.readLong().let { tmpDate -> if (tmpDate == -1L) Date(0) else Date(tmpDate) },
        time = parcel.readLong(),
        description = parcel.readString() ?: "",
        content = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        priority = parcel.readInt(),
        color = parcel.readInt()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(alertId)
        dest.writeLong(date.time)
        dest.writeLong(time)
        dest.writeString(description)
        dest.writeString(content)
        dest.writeString(type)
        dest.writeInt(priority)
        dest.writeInt(color)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Alert> = object : Parcelable.Creator<Alert> {
            override fun createFromParcel(source: Parcel): Alert = Alert(source)
            override fun newArray(size: Int): Array<Alert?> = arrayOfNulls(size)
        }

        @JvmStatic
        fun deduplication(alertList: MutableList<Alert>) {
            val typeSet = HashSet<String>()
            for (i in alertList.indices.reversed()) {
                val alert = alertList[i]
                if (typeSet.contains(alert.type)) {
                    alertList.removeAt(i)
                } else {
                    typeSet.add(alert.type)
                }
            }
        }

        @JvmStatic
        fun descByTime(alertList: MutableList<Alert>) {
            alertList.sortWith { o1, o2 -> (o2.time - o1.time).toInt() }
        }
    }
}
