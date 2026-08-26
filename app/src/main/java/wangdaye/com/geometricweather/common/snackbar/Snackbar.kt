package wangdaye.com.geometricweather.common.snackbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.WindowInsets
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.behavior.SwipeDismissBehavior
import wangdaye.com.geometricweather.R

class Snackbar private constructor(
    parent: ViewGroup,
    private val mCardStyle: Boolean
) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    annotation class Duration

    open class Callback {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            DISMISS_EVENT_SWIPE,
            DISMISS_EVENT_ACTION,
            DISMISS_EVENT_TIMEOUT,
            DISMISS_EVENT_MANUAL,
            DISMISS_EVENT_CONSECUTIVE
        )
        annotation class DismissEvent

        open fun onDismissed(snackbar: Snackbar, @DismissEvent event: Int) {}

        open fun onShown(snackbar: Snackbar) {}

        companion object {
            const val DISMISS_EVENT_SWIPE = 0
            const val DISMISS_EVENT_ACTION = 1
            const val DISMISS_EVENT_TIMEOUT = 2
            const val DISMISS_EVENT_MANUAL = 3
            const val DISMISS_EVENT_CONSECUTIVE = 4
        }
    }

    private val mContext: Context = parent.context
    private val mParent: ViewGroup = parent
    private val mView: SnackbarLayout = LayoutInflater.from(mContext).inflate(
        if (mCardStyle) R.layout.container_snackbar_layout_card else R.layout.container_snackbar_layout,
        mParent,
        false
    ) as SnackbarLayout

    private var mDuration = 0
    private var mCallback: Callback? = null
    private var mAnimator: Animator? = null

    private val mManagerCallback = object : SnackbarManager.Callback {
        override fun show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, this@Snackbar))
        }

        override fun dismiss(event: Int) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, this@Snackbar))
        }
    }

    fun setAction(@StringRes resId: Int, listener: View.OnClickListener?): Snackbar {
        return setAction(mContext.getText(resId), listener)
    }

    fun setAction(text: CharSequence?, listener: View.OnClickListener?): Snackbar {
        return setAction(text, true, listener)
    }

    fun setAction(
        text: CharSequence?,
        shouldDismissOnClick: Boolean,
        listener: View.OnClickListener?
    ): Snackbar {
        val tv = mView.getActionView()
        if (TextUtils.isEmpty(text) || listener == null) {
            tv.visibility = View.GONE
            tv.setOnClickListener(null)
        } else {
            tv.visibility = View.VISIBLE
            tv.text = text
            tv.setOnClickListener { view ->
                listener.onClick(view)
                if (shouldDismissOnClick) {
                    dispatchDismiss(Callback.DISMISS_EVENT_ACTION)
                }
            }
        }
        return this
    }

    fun setActionTextColor(colors: ColorStateList?): Snackbar {
        mView.getActionView().setTextColor(colors)
        return this
    }

    fun setActionTextColor(@ColorInt color: Int): Snackbar {
        mView.getActionView().setTextColor(color)
        return this
    }

    fun setText(message: CharSequence): Snackbar {
        mView.getMessageView().text = message
        return this
    }

    fun setText(@StringRes resId: Int): Snackbar {
        return setText(mContext.getText(resId))
    }

    fun setDuration(@Duration duration: Int): Snackbar {
        mDuration = duration
        return this
    }

    @Duration
    fun getDuration(): Int {
        return mDuration
    }

    fun getView(): View {
        return mView
    }

    fun show() {
        SnackbarManager.getInstance().show(mDuration, mManagerCallback)
    }

    fun dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL)
    }

    private fun dispatchDismiss(@Callback.DismissEvent event: Int) {
        SnackbarManager.getInstance().dismiss(mManagerCallback, event)
    }

    fun setCallback(callback: Callback?): Snackbar {
        mCallback = callback
        return this
    }

    fun isShown(): Boolean {
        return SnackbarManager.getInstance().isCurrent(mManagerCallback)
    }

    fun isShownOrQueued(): Boolean {
        return SnackbarManager.getInstance().isCurrentOrNext(mManagerCallback)
    }

    fun showView() {
        if (mView.parent == null) {
            val lp = mView.layoutParams
            if (lp is CoordinatorLayout.LayoutParams) {
                val behavior = Behavior()
                behavior.setStartAlphaSwipeDistance(0.1f)
                behavior.setEndAlphaSwipeDistance(0.6f)
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)
                behavior.setListener(object : SwipeDismissBehavior.OnDismissListener {
                    override fun onDismiss(view: View) {
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE)
                    }

                    override fun onDragStateChanged(state: Int) {
                        when (state) {
                            SwipeDismissBehavior.STATE_DRAGGING,
                            SwipeDismissBehavior.STATE_SETTLING ->
                                SnackbarManager.getInstance().cancelTimeout(mManagerCallback)
                            SwipeDismissBehavior.STATE_IDLE ->
                                SnackbarManager.getInstance().restoreTimeout(mManagerCallback)
                        }
                    }
                })
                lp.behavior = behavior
            }
            mParent.addView(mView)
        }

        mView.setOnAttachStateChangeListener(object : SnackbarLayout.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}

            override fun onViewDetachedFromWindow(v: View) {
                if (isShownOrQueued()) {
                    sHandler.post { onViewHidden(Callback.DISMISS_EVENT_MANUAL) }
                }
            }
        })

        if (ViewCompat.isLaidOut(mView)) {
            animateViewIn()
        } else {
            mView.setOnLayoutChangeListener { _, _, _, _, _ ->
                animateViewIn()
                mView.setOnLayoutChangeListener(null)
            }
        }
    }

    private fun animateViewIn() {
        mAnimator?.cancel()
        mAnimator = Utils.getEnterAnimator(mView, mCardStyle)
        mAnimator!!.duration = ANIMATION_DURATION.toLong()
        mAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mView.animateChildrenIn(
                    ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                    ANIMATION_FADE_DURATION
                )
            }

            override fun onAnimationEnd(animation: Animator) {
                mCallback?.onShown(this@Snackbar)
                SnackbarManager.getInstance().onShown(mManagerCallback)
            }
        })
        mAnimator!!.start()
    }

    private fun animateViewOut(event: Int) {
        mAnimator?.cancel()
        mAnimator = ObjectAnimator.ofFloat(
            mView, "translationY", mView.translationY, mView.height.toFloat()
        ).setDuration(ANIMATION_DURATION.toLong())
        mAnimator!!.interpolator = Utils.FAST_OUT_SLOW_IN_INTERPOLATOR
        mAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mView.animateChildrenOut(0, ANIMATION_FADE_DURATION)
            }

            override fun onAnimationEnd(animation: Animator) {
                onViewHidden(event)
            }
        })
        mAnimator!!.start()
    }

    fun hideView(event: Int) {
        if (mView.visibility != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event)
        } else {
            animateViewOut(event)
        }
    }

    private fun onViewHidden(event: Int) {
        SnackbarManager.getInstance().onDismissed(mManagerCallback)
        mCallback?.onDismissed(this, event)
        val parent = mView.parent
        if (parent is ViewGroup) {
            parent.removeView(mView)
        }
    }

    private fun isBeingDragged(): Boolean {
        val lp = mView.layoutParams
        if (lp is CoordinatorLayout.LayoutParams) {
            val behavior = lp.behavior
            if (behavior is SwipeDismissBehavior<*>) {
                return behavior.dragState != SwipeDismissBehavior.STATE_IDLE
            }
        }
        return false
    }

    open class SnackbarLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : ViewGroup(context, attrs) {

        private val mWindowInsets = Rect()
        private lateinit var mMessageView: TextView
        private lateinit var mActionView: Button
        private val mMaxWidth: Int
        private var mOnLayoutChangeListener: OnLayoutChangeListener? = null
        private var mOnAttachStateChangeListener: OnAttachStateChangeListener? = null

        fun interface OnLayoutChangeListener {
            fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int)
        }

        interface OnAttachStateChangeListener {
            fun onViewAttachedToWindow(v: View)
            fun onViewDetachedFromWindow(v: View)
        }

        init {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout)
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1)
            a.recycle()
            isClickable = true
            LayoutInflater.from(context).inflate(getLayoutId(), this)
            ViewCompat.setAccessibilityLiveRegion(
                this,
                ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE
            )
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
            val compat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val systemInsets = compat.getInsets(WindowInsetsCompat.Type.systemBars())
            mWindowInsets.set(
                systemInsets.left, systemInsets.top,
                systemInsets.right, systemInsets.bottom
            )
            Utils.consumeInsets(this, mWindowInsets)
            return insets
        }

        override fun fitSystemWindows(insets: Rect): Boolean {
            mWindowInsets.set(insets)
            Utils.consumeInsets(this, mWindowInsets)
            return false
        }

        @LayoutRes
        open fun getLayoutId(): Int {
            return R.layout.container_snackbar_layout_inner
        }

        override fun onFinishInflate() {
            super.onFinishInflate()
            mMessageView = findViewById(R.id.snackbar_text)
            mActionView = findViewById(R.id.snackbar_action)
        }

        fun getMessageView(): TextView {
            return mMessageView
        }

        fun getActionView(): Button {
            return mActionView
        }

        override fun generateLayoutParams(p: LayoutParams): LayoutParams {
            return MarginLayoutParams(p)
        }

        override fun generateDefaultLayoutParams(): LayoutParams {
            return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
            return MarginLayoutParams(context, attrs)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthSpec = widthMeasureSpec
            val child = getChildAt(0)
            val widthUsed = mWindowInsets.left + mWindowInsets.right
            val heightUsed = mWindowInsets.bottom

            measureChildWithMargins(child, widthSpec, widthUsed, heightMeasureSpec, heightUsed)
            var lp = child.layoutParams as MarginLayoutParams

            var width = child.measuredWidth + widthUsed + lp.leftMargin + lp.rightMargin +
                paddingLeft + paddingRight
            var height = child.measuredHeight + heightUsed + lp.topMargin + lp.bottomMargin +
                paddingTop + paddingBottom

            if (mMaxWidth > 0 && width > mMaxWidth) {
                widthSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY)
                measureChildWithMargins(child, widthSpec, widthUsed, heightMeasureSpec, heightUsed)
                lp = child.layoutParams as MarginLayoutParams
                width = child.measuredWidth + widthUsed + lp.leftMargin + lp.rightMargin +
                    paddingLeft + paddingRight
                height = child.measuredHeight + heightUsed + lp.topMargin + lp.bottomMargin +
                    paddingTop + paddingBottom
            }
            setMeasuredDimension(width, height)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val child = getChildAt(0)
            val x = (measuredWidth - child.measuredWidth) / 2
            child.layout(x, 0, x + child.measuredWidth, child.measuredHeight)
            mOnLayoutChangeListener?.onLayoutChange(this, l, t, r, b)
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            mOnAttachStateChangeListener?.onViewAttachedToWindow(this)
            ViewCompat.requestApplyInsets(this)
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            mOnAttachStateChangeListener?.onViewDetachedFromWindow(this)
        }

        fun animateChildrenIn(delay: Int, duration: Int) {
            mMessageView.alpha = 0f
            ViewCompat.animate(mMessageView)
                .alpha(1f)
                .setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
                .start()
            if (mActionView.visibility == VISIBLE) {
                mActionView.alpha = 0f
                ViewCompat.animate(mActionView)
                    .alpha(1f)
                    .setDuration(duration.toLong())
                    .setStartDelay(delay.toLong())
                    .start()
            }
        }

        fun animateChildrenOut(delay: Int, duration: Int) {
            mMessageView.alpha = 1f
            ViewCompat.animate(mMessageView)
                .alpha(0f)
                .setDuration(duration.toLong())
                .setStartDelay(delay.toLong())
                .start()
            if (mActionView.visibility == VISIBLE) {
                mActionView.alpha = 1f
                ViewCompat.animate(mActionView)
                    .alpha(0f)
                    .setDuration(duration.toLong())
                    .setStartDelay(delay.toLong())
                    .start()
            }
        }

        fun setOnLayoutChangeListener(onLayoutChangeListener: OnLayoutChangeListener?) {
            mOnLayoutChangeListener = onLayoutChangeListener
        }

        fun setOnAttachStateChangeListener(listener: OnAttachStateChangeListener?) {
            mOnAttachStateChangeListener = listener
        }
    }

    class CardSnackbarLayout : SnackbarLayout {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        override fun getLayoutId(): Int {
            return R.layout.container_snackbar_layout_inner_card
        }
    }

    inner class Behavior : SwipeDismissBehavior<SnackbarLayout>() {
        override fun canSwipeDismissView(child: View): Boolean {
            return child is SnackbarLayout
        }

        override fun onLayoutChild(
            parent: CoordinatorLayout,
            child: SnackbarLayout,
            layoutDirection: Int
        ): Boolean {
            return super.onLayoutChild(parent, child, layoutDirection)
        }

        override fun onInterceptTouchEvent(
            parent: CoordinatorLayout,
            child: SnackbarLayout,
            event: MotionEvent
        ): Boolean {
            if (parent.isPointInChildBounds(child, event.x.toInt(), event.y.toInt())) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN ->
                        SnackbarManager.getInstance().cancelTimeout(mManagerCallback)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                        SnackbarManager.getInstance().restoreTimeout(mManagerCallback)
                }
            }
            return super.onInterceptTouchEvent(parent, child, event)
        }
    }

    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0

        private const val ANIMATION_DURATION = 450
        private const val ANIMATION_FADE_DURATION = 200
        private const val MSG_SHOW = 0
        private const val MSG_DISMISS = 1

        private val sHandler = Handler(Looper.getMainLooper()) { message ->
            when (message.what) {
                MSG_SHOW -> {
                    (message.obj as Snackbar).showView()
                    true
                }
                MSG_DISMISS -> {
                    (message.obj as Snackbar).hideView(message.arg1)
                    true
                }
                else -> false
            }
        }

        @JvmStatic
        fun make(
            view: View,
            text: CharSequence,
            @Duration duration: Int,
            cardStyle: Boolean
        ): Snackbar {
            val snackbar = Snackbar(findSuitableParent(view), cardStyle)
            snackbar.setText(text)
            snackbar.setDuration(duration)
            return snackbar
        }

        @JvmStatic
        fun make(
            view: View,
            @StringRes resId: Int,
            @Duration duration: Int,
            cardStyle: Boolean
        ): Snackbar {
            return make(view, view.resources.getText(resId), duration, cardStyle)
        }

        private fun findSuitableParent(view: View): ViewGroup {
            var current: View? = view
            do {
                if (current is CoordinatorLayout) {
                    return current
                } else if (current is FrameLayout) {
                    if (current.id == android.R.id.content) {
                        return current
                    }
                }
                val parent = current?.parent
                current = if (parent is View) parent else null
            } while (current != null)
            throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
        }
    }
}
