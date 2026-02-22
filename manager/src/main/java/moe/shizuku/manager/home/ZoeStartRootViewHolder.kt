package moe.shizuku.manager.home

import android.view.LayoutInflater
import android.view.ViewGroup
import moe.shizuku.manager.R
import moe.shizuku.manager.databinding.ZoeHomeActionCardBinding
import moe.shizuku.manager.databinding.ZoeHomeItemContainerBinding
import rikka.recyclerview.BaseViewHolder

class ZoeStartRootViewHolder(
    private val binding: ZoeHomeActionCardBinding,
    root: ViewGroup
) : BaseViewHolder<Boolean>(root) {

    companion object {
        val CREATOR = Creator<Boolean> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = ZoeHomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = ZoeHomeActionCardBinding.inflate(inflater, outer.root, true)
            ZoeStartRootViewHolder(inner, outer.root)
        }
    }

    init {
        binding.icon.setImageResource(R.drawable.ic_root_24dp)
        binding.title.setText(R.string.home_root_title)
    }

    override fun onBind() {
        val restart = data
        val context = itemView.context

        binding.text1.text = context.getString(
            R.string.home_root_description,
            "<b><a href=\"https://github.com/RikkaApps/Sui\">Sui</a></b>"
        )

        if (restart) {
            binding.button1.visibility = ViewGroup.GONE
            binding.button2.visibility = ViewGroup.VISIBLE
            binding.button2.setOnClickListener {
                Starter.start(context, true)
            }
        } else {
            binding.button1.visibility = ViewGroup.VISIBLE
            binding.button2.visibility = ViewGroup.GONE
            binding.button1.setOnClickListener {
                Starter.start(context, true)
            }
        }
    }
}
