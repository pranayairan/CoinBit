package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.binarybricks.coinbit.featurecomponents.DashboardHeaderModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class DashboardHeaderAdapterDelegate(
    private val toCurrency: String,
    private val toolbarTitle: TextView,
    private val androidResourceManager: AndroidResourceManager
) : AdapterDelegate<List<ModuleItem>>() {

    private val dashboardHeaderModule by lazy {
        DashboardHeaderModule(toCurrency, toolbarTitle, androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is DashboardHeaderModule.DashboardHeaderModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val dashboardHeaderModuleView = dashboardHeaderModule.init(LayoutInflater.from(parent.context), parent)
        return DashboardHeaderViewHolder(dashboardHeaderModuleView, dashboardHeaderModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val dashboardHeaderViewHolder = holder as DashboardHeaderViewHolder
        dashboardHeaderViewHolder.loadPortfolio(items[position] as DashboardHeaderModule.DashboardHeaderModuleData)
    }

    class DashboardHeaderViewHolder(override val containerView: View, private val dashboardHeaderModule: DashboardHeaderModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun loadPortfolio(dashboardHeaderModuleData: DashboardHeaderModule.DashboardHeaderModuleData) {
            dashboardHeaderModule.loadPortfolioData(itemView, dashboardHeaderModuleData)
        }
    }
}