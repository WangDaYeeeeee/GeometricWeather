package wangdaye.com.geometricweather.main.adapters.main.holder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.main.utils.MainModuleUtils
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

abstract class AbstractMainViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    protected lateinit var context: Context
    protected lateinit var provider: ResourceProvider
    protected var itemAnimationEnabled = false
    private var inScreen = false
    private var itemAnimator: Animator? = null
    private var delayController: AsyncHelper.Controller? = null

    @CallSuper
    open fun onBindView(
        context: Context,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        this.context = context
        this.provider = provider
        this.itemAnimationEnabled = itemAnimationEnabled
        inScreen = false
        delayController = null

        if (listAnimationEnabled) {
            itemView.alpha = 0f
        }
    }

    open fun getTop(): Int = itemView.top

    fun checkEnterScreen(
        host: RecyclerView,
        pendingAnimatorList: MutableList<Animator>,
        listAnimationEnabled: Boolean
    ) {
        if (!itemView.isLaidOut || getTop() >= host.measuredHeight) {
            return
        }
        if (!inScreen) {
            inScreen = true
            if (listAnimationEnabled) {
                executeEnterAnimator(pendingAnimatorList)
            } else {
                onEnterScreen()
            }
        }
    }

    fun executeEnterAnimator(pendingAnimatorList: MutableList<Animator>) {
        itemView.alpha = 0f

        itemAnimator = getEnterAnimator(pendingAnimatorList)
        itemAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                pendingAnimatorList.remove(itemAnimator)
            }
        })

        delayController = AsyncHelper.delayRunOnUI({
            pendingAnimatorList.remove(itemAnimator)
            onEnterScreen()
        }, itemAnimator!!.startDelay)

        pendingAnimatorList.add(itemAnimator!!)
        itemAnimator!!.start()
    }

    protected open fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        return MainModuleUtils.getEnterAnimator(itemView, pendingAnimatorList.size)
    }

    open fun onEnterScreen() {
        // do nothing.
    }

    open fun onRecycleView() {
        delayController?.cancel()
        delayController = null
        itemAnimator?.cancel()
        itemAnimator = null
    }
}
