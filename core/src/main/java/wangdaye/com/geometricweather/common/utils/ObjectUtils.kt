package wangdaye.com.geometricweather.common.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object ObjectUtils {

    @JvmStatic
    @Throws(IOException::class, ClassNotFoundException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> deepCopy(src: List<T>): List<T> {
        val byteOut = ByteArrayOutputStream()
        val out = ObjectOutputStream(byteOut)
        out.writeObject(src)

        val byteIn = ByteArrayInputStream(byteOut.toByteArray())
        val input = ObjectInputStream(byteIn)
        return input.readObject() as List<T>
    }

    @JvmStatic
    fun safeValueOf(integer: Int?): Int {
        return integer ?: 0
    }
}
