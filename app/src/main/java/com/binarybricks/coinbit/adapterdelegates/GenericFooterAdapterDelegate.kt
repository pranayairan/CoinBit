package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.GenericFooterModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class GenericFooterAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {

    private val genericFooterModule by lazy {
        GenericFooterModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is GenericFooterModule.FooterModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val genericFooterModuleView = genericFooterModule.init(LayoutInflater.from(parent.context), parent)
        return GenericFooterViewHolder(genericFooterModuleView, genericFooterModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val genericFooterViewHolder = holder as GenericFooterViewHolder
        genericFooterViewHolder.showFooterText((items[position] as GenericFooterModule.FooterModuleData))
    }

    class GenericFooterViewHolder(override val containerView: View, private val genericFooterModule: GenericFooterModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showFooterText(footerModuleData: GenericFooterModule.FooterModuleData) {
            genericFooterModule.showFooterText(itemView, footerModuleData)
        }
    }
}