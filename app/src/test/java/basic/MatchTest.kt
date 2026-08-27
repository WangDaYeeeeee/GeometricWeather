package basic

import android.text.TextUtils
import org.junit.Test
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.regex.Pattern

class MatchTest {

    @Test
    fun pattern() {
        val text = "Frigid with snow, acuu an additional 1-3 cm; limited outdoor activity. 2-4 cm, 4-5cm"
        val NumberPattern = "\\d+-\\d+(\\s+)?cm"
        val pattern = Pattern.compile(NumberPattern)
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            println(text.substring(start, end))
        }
    }

    @Test
    fun split() {
        val text = "dadasd dsad   dad"
        println(Arrays.toString(text.split("-").toTypedArray()))
    }

    @Test
    fun convertUnit() {
        var str: String? = "Frigid with snow, acuu an additional 1-3 cm; limited outdoor activity. 2-4 cm, 4-5cm"
        if (TextUtils.isEmpty(str)) {
            return
        }
        str = convertUnit(str!!, "cm") { value -> value * 10 }
        println(str)
    }

    @Test
    fun formatFloat() {
        println(formatFloat(7.00646f, 2))
        println(formatFloat(7.00246f, 2))
    }

    fun interface MilliMeterConverter {
        fun toMilliMeters(value: Float): Float
    }

    companion object {
        fun formatFloat(value: Float, decimalNumber: Int): String {
            val factor = Math.pow(10.0, decimalNumber.toDouble()).toFloat()
            return if (Math.round(value) * factor == Math.round(value * factor).toFloat()) {
                Math.round(value).toString()
            } else {
                String.format("%." + decimalNumber + "f", value)
            }
        }

        private fun convertUnit(
            strIn: String,
            targetUnit: String,
            converter: MilliMeterConverter
        ): String {
            var str = strIn
            val numberPattern = "\\d+-\\d+(\\s+)?"
            val matcher = Pattern.compile(numberPattern + targetUnit).matcher(str)
            val targetList = ArrayList<String>()
            val resultList = ArrayList<String>()
            while (matcher.find()) {
                val target = str.substring(matcher.start(), matcher.end())
                targetList.add(target)
                val targetSplitResults = target.replace(" ", "").split(targetUnit).toTypedArray()
                val numberTexts = targetSplitResults[0].split("-").toTypedArray()
                for (i in numberTexts.indices) {
                    var number = numberTexts[i].toFloat()
                    number = converter.toMilliMeters(number)
                    numberTexts[i] = floatToString(number)
                }
                resultList.add(arrayToString(numberTexts, '-') + " " + "mm")
            }
            for (i in targetList.indices) {
                str = str.replace(targetList[i], resultList[i])
            }
            return str
        }

        private fun floatToString(number: Float): String {
            return if (number.toInt() * 1000 == (number * 1000).toInt()) {
                number.toInt().toString()
            } else {
                DecimalFormat("######0.0").format(number.toDouble())
            }
        }

        private fun arrayToString(array: Array<String>, separator: Char): String {
            val builder = StringBuilder()
            for (i in array.indices) {
                builder.append(array[i])
                if (i < array.size - 1) {
                    builder.append(separator)
                }
            }
            return builder.toString()
        }
    }
}
