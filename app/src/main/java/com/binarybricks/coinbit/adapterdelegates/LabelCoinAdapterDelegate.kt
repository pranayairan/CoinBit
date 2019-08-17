package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.LabelModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class LabelCoinAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {

    private val labelModule by lazy {
        LabelModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is LabelModule.LabelModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val labelModuleView = labelModule.init(LayoutInflater.from(parent.context), parent)
        return LabelViewHolder(labelModuleView, labelModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val labelViewHolder = holder as LabelViewHolder
        labelViewHolder.showLabelText((items[position] as LabelModule.LabelModuleData))
    }

    class LabelViewHolder(override val containerView: View, private val labelModule: LabelModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showLabelText(labelModuleData: LabelModule.LabelModuleData) {
            labelModule.showLabelText(itemView, labelModuleData)
        }
    }
}