package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.DiscoveryNewsModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class DiscoveryNewsAdapterDelegate(private val androidResourceManager: AndroidResourceManager) : AdapterDelegate<List<ModuleItem>>() {

    private val discoveryNewsModule by lazy {
        DiscoveryNewsModule(androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is DiscoveryNewsModule.DiscoveryNewsModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val discoveryNewsModuleView = discoveryNewsModule.init(LayoutInflater.from(parent.context), parent)
        return DiscoveryNewsHolder(discoveryNewsModuleView, discoveryNewsModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val discoveryNewsHolder = holder as DiscoveryNewsHolder
        discoveryNewsHolder.showNewsOnDiscoverFeed((items[position] as DiscoveryNewsModule.DiscoveryNewsModuleData))
    }

    class DiscoveryNewsHolder(override val containerView: View, private val discoveryNewsModule: DiscoveryNewsModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showNewsOnDiscoverFeed(discoveryNewsModuleData: DiscoveryNewsModule.DiscoveryNewsModuleData) {
            discoveryNewsModule.showNewsOnDiscoverFeed(containerView, discoveryNewsModuleData)
        }
    }
}