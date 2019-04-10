package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.AboutCoinModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class AboutCoinAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {

    private val aboutCoinModule by lazy {
        AboutCoinModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is AboutCoinModule.AboutCoinModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val aboutCardModuleView = aboutCoinModule.init(LayoutInflater.from(parent.context), parent)
        return AboutCoinViewHolder(aboutCardModuleView, aboutCoinModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val aboutCoinViewHolder = holder as AboutCoinViewHolder
        aboutCoinViewHolder.showAboutCoinText((items[position] as AboutCoinModule.AboutCoinModuleData))
    }

    class AboutCoinViewHolder(override val containerView: View, private val aboutCoinModule: AboutCoinModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showAboutCoinText(aboutCoinModuleData: AboutCoinModule.AboutCoinModuleData) {
            aboutCoinModule.showAboutCoinText(itemView, aboutCoinModuleData)
        }
    }
}