package moe.shizuku.manager.home

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import moe.shizuku.manager.R
import moe.shizuku.manager.databinding.ZoeHomeItemContainerBinding
import moe.shizuku.manager.databinding.ZoeHomeServerStatusBinding
import moe.shizuku.manager.model.ServiceStatus
import rikka.html.text.HtmlCompat
import rikka.html.text.toHtml
import rikka.recyclerview.BaseViewHolder

class ZoeServerStatusViewHolder(
    private val binding: ZoeHomeServerStatusBinding,
    root: View
) : BaseViewHolder<ServiceStatus>(root) {

    companion object {
        val CREATOR = Creator<ServiceStatus> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = ZoeHomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = ZoeHomeServerStatusBinding.inflate(inflater, outer.root, true)
            ZoeServerStatusViewHolder(inner, outer.root)
        }
    }

    private var pulseAnimator: ValueAnimator? = null

    override fun onBind() {
        val context = itemView.context
        val status = data
        val ok = status.isRunning
        val isRoot = status.uid == 0
        val apiVersion = status.apiVersion
        val patchVersion = status.patchVersion

        val iconRes = if (ok) R.drawable.ic_server_ok_24dp else R.drawable.ic_server_error_24dp
        val iconTint = if (ok) {
            ContextCompat.getColor(context, R.color.zoe_success)
        } else {
            ContextCompat.getColor(context, R.color.zoe_error)
        }

        binding.icon.setImageResource(iconRes)
        binding.statusIconBg.backgroundTintList = ColorStateList.valueOf(iconTint)
        ImageViewCompat.setImageTintList(binding.icon, ColorStateList.valueOf(
            ContextCompat.getColor(context, android.R.color.white)
        ))

        val user = if (isRoot) "root" else "adb"
        val title = if (ok) {
            context.getString(R.string.home_status_service_is_running, context.getString(R.string.app_name))
        } else {
            context.getString(R.string.home_status_service_not_running, context.getString(R.string.app_name))
        }

        val summary = if (ok) {
            if (apiVersion != rikka.shizuku.Shizuku.getLatestServiceVersion() || 
                status.patchVersion != rikka.shizuku.ShizukuApiConstants.SERVER_PATCH_VERSION) {
                context.getString(
                    R.string.home_status_service_version_update, user,
                    "${apiVersion}.${patchVersion}",
                    "${rikka.shizuku.Shizuku.getLatestServiceVersion()}.${rikka.shizuku.ShizukuApiConstants.SERVER_PATCH_VERSION}"
                )
            } else {
                context.getString(R.string.home_status_service_version, user, "${apiVersion}.${patchVersion}")
            }
        } else {
            ""
        }

        binding.text1.text = title.toHtml(HtmlCompat.FROM_HTML_OPTION_TRIM_WHITESPACE)
        binding.text2.text = summary.toHtml(HtmlCompat.FROM_HTML_OPTION_TRIM_WHITESPACE)
        
        binding.text2.visibility = if (TextUtils.isEmpty(binding.text2.text)) {
            View.GONE
        } else {
            View.VISIBLE
        }

        if (ok) {
            startPulseAnimation()
        } else {
            stopPulseAnimation()
        }
    }

    private fun startPulseAnimation() {
        if (pulseAnimator != null) return

        pulseAnimator = ValueAnimator.ofFloat(1f, 1.1f, 1f).apply {
            duration = 2000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = OvershootInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                binding.icon.scaleX = scale
                binding.icon.scaleY = scale
            }
            start()
        }
    }

    private fun stopPulseAnimation() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        binding.icon.scaleX = 1f
        binding.icon.scaleY = 1f
    }

    override fun onUnbind() {
        super.onUnbind()
        stopPulseAnimation()
    }
}
