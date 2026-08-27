package wangdaye.com.geometricweather.common.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.text.BidiFormatter
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import java.math.BigDecimal
import java.text.DecimalFormat

@SuppressLint("AppCompatCustomView")
class NumberAnimTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private var mNumStart = "0"
    private var mNumEnd: String? = null
    private var mDuration: Long = 2000
    private var mPrefixString = ""
    private var mPostfixString = ""
    private var mIsEnableAnim = true
    private var isInt = false
    private var animator: ValueAnimator? = null

    fun setNumberString(number: String) {
        setNumberString("0", number)
    }

    @SuppressLint("SetTextI18n")
    fun setNumberString(numberStart: String, numberEnd: String) {
        mNumStart = numberStart
        mNumEnd = numberEnd
        if (checkNumString(numberStart, numberEnd)) {
            start()
        } else {
            text = mPrefixString + BidiFormatter.getInstance().unicodeWrap(numberEnd) + mPostfixString
        }
    }

    fun setEnableAnim(enableAnim: Boolean) {
        mIsEnableAnim = enableAnim
    }

    fun setDuration(duration: Long) {
        this.mDuration = duration
    }

    fun setPrefixString(prefixString: String) {
        this.mPrefixString = prefixString
    }

    fun setPostfixString(postfixString: String) {
        this.mPostfixString = postfixString
    }

    private fun checkNumString(numberStart: String, numberEnd: String): Boolean {
        val regexInteger = "-?\\d*"
        isInt = numberEnd.matches(regexInteger.toRegex()) && numberStart.matches(regexInteger.toRegex())
        if (isInt) {
            return true
        }
        val regexDecimal = "-?[1-9]\\d*.\\d*|-?0.\\d*[1-9]\\d*"
        if ("0" == numberStart) {
            if (numberEnd.matches(regexDecimal.toRegex())) {
                return true
            }
        }
        return numberEnd.matches(regexDecimal.toRegex()) && numberStart.matches(regexDecimal.toRegex())
    }

    @SuppressLint("SetTextI18n")
    private fun start() {
        if (!mIsEnableAnim) {
            text = mPrefixString + format(BigDecimal(mNumEnd)) + mPostfixString
            return
        }
        val f = BidiFormatter.getInstance()

        animator = ValueAnimator.ofObject(
            BigDecimalEvaluator(),
            BigDecimal(mNumStart),
            BigDecimal(mNumEnd)
        )
        animator!!.duration = mDuration
        animator!!.interpolator = DecelerateInterpolator(3f)
        animator!!.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as BigDecimal
            text = mPrefixString + f.unicodeWrap(format(value)) + mPostfixString
        }
        animator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                text = mPrefixString + f.unicodeWrap(mNumEnd) + mPostfixString
            }
        })
        animator!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private fun format(bd: BigDecimal): String {
        val pattern = StringBuilder()
        if (isInt) {
            pattern.append("#,###")
        } else {
            var length = 0
            val s1 = mNumStart.split("\\.".toRegex()).toTypedArray()
            val s2 = mNumEnd!!.split("\\.".toRegex()).toTypedArray()
            val s = if (s1.size > s2.size) s1 else s2
            if (s.size > 1) {
                val decimals = s[1]
                length = decimals.length
            }
            pattern.append("#,##0")
            if (length > 0) {
                pattern.append(".")
                for (i in 0 until length) {
                    pattern.append("0")
                }
            }
        }
        val df = DecimalFormat(pattern.toString())
        return df.format(bd)
    }

    private class BigDecimalEvaluator : TypeEvaluator<Any> {
        override fun evaluate(fraction: Float, startValue: Any, endValue: Any): Any {
            val start = startValue as BigDecimal
            val end = endValue as BigDecimal
            val result = end.subtract(start)
            return result.multiply(BigDecimal(fraction.toDouble())).add(start)
        }
    }
}
