package wangdaye.com.geometricweather.common.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Matches Gson `setDateFormat("yyyy-MM-dd'T'HH:mm:ss")`: parse the local datetime
 * prefix and ignore a trailing offset such as `+08:00`.
 */
object GsonCompatibleDateSerializer : KSerializer<Date> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("GsonCompatibleDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(formatter().format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        val text = decoder.decodeString()
        return try {
            formatter().parse(text) ?: throw ParseException(text, 0)
        } catch (e: ParseException) {
            throw e
        }
    }

    private fun formatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    }
}
