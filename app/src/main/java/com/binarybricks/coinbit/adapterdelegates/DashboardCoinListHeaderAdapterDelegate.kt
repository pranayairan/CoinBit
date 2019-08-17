package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.DashboardCoinListHeaderModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 * Adapter delegate that takes care of header for list on dashboard
 */

class DashboardCoinListHeaderAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {
    private val dashboardCoinListHeaderModule by lazy {
        DashboardCoinListHeaderModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is DashboardCoinListHeaderModule.DashboardCoinListHeaderModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val dashboardCoinHeaderModuleView = dashboardCoinListHeaderModule.init(LayoutInflater.from(parent.context), parent)
        return DashboardCoinListHeaderViewHolder(dashboardCoinHeaderModuleView, dashboardCoinListHeaderModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val dashboardCoinHeaderViewHolder = holder as DashboardCoinListHeaderViewHolder
        dashboardCoinHeaderViewHolder.showHeaderText((items[position] as DashboardCoinListHeaderModule.DashboardCoinListHeaderModuleData))
    }

    class DashboardCoinListHeaderViewHolder(override val containerView: View, private val dashboardCoinListHeaderModule: DashboardCoinListHeaderModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showHeaderText(dashboardCoinListHeaderModuleData: DashboardCoinListHeaderModule.DashboardCoinListHeaderModuleData) {
            dashboardCoinListHeaderModule.showHeaderText(itemView, dashboardCoinListHeaderModuleData)
        }
    }
}