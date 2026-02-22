package moe.shizuku.manager.home

import android.animation.AnimatorInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimatorRes
import androidx.recyclerview.widget.RecyclerView
import moe.shizuku.manager.R

abstract class AnimatedViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var data: T? = null
        private set

    open fun bind(data: T) {
        this.data = data
    }

    open fun onAttached() {}
    open fun onDetached() {}

    fun runEnterAnimation(@AnimatorRes animatorRes: Int = R.animator.zoe_card_enter) {
        itemView.alpha = 0f
        itemView.postDelayed({
            AnimatorInflater.loadAnimator(itemView.context, animatorRes).apply {
                setTarget(itemView)
                start()
            }
        }, (bindingAdapterPosition * 50L).coerceAtMost(300))
    }

    companion object {
        inline fun <reified T, VH : AnimatedViewHolder<T>> createCreator(
            crossinline inflater: (LayoutInflater, ViewGroup?) -> VH,
            @AnimatorRes enterAnimation: Int = R.animator.zoe_card_enter
        ): Creator<T> {
            return Creator { layoutInflater, parent ->
                val holder = inflater(layoutInflater, parent)
                object : AnimatedViewHolder<T>(holder.itemView) {
                    override fun bind(data: T) {
                        holder.bind(data)
                        this.data = data
                    }

                    override fun onAttached() {
                        super.onAttached()
                        runEnterAnimation(enterAnimation)
                    }
                }
            }
        }
    }
}
