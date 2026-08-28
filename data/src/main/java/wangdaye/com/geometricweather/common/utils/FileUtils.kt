package wangdaye.com.geometricweather.common.utils

import android.content.Context
import kotlinx.serialization.builtins.ListSerializer
import wangdaye.com.geometricweather.common.basic.models.ChineseCity
import wangdaye.com.geometricweather.common.json.AppJson

object FileUtils {

    @JvmStatic
    fun readCityList(context: Context): List<ChineseCity> {
        return AppJson.decodeFromString(
            ListSerializer(ChineseCity.serializer()),
            readAssetFileToString(context, "city_list.txt")
        )
    }

    private fun readAssetFileToString(context: Context, fileName: String): String {
        return try {
            context.resources.assets.open(fileName).bufferedReader().use { reader ->
                buildString {
                    reader.lineSequence().forEach { append(it) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
