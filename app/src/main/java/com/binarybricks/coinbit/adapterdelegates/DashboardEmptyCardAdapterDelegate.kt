package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.DashboardEmptyCoinModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class DashboardEmptyCardAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {
    private val dashboardEmptyCardModule by lazy {
        DashboardEmptyCoinModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is DashboardEmptyCoinModule.DashboardEmptyCoinModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val dashboardEmptyCardModuleView = dashboardEmptyCardModule.init(LayoutInflater.from(parent.context), parent)
        return DashboardEmptyCardViewHolder(dashboardEmptyCardModuleView, dashboardEmptyCardModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val dashboardEmptyCardViewHolder = holder as DashboardEmptyCardViewHolder
        dashboardEmptyCardViewHolder.showTextInEmptySpace((items[position] as DashboardEmptyCoinModule.DashboardEmptyCoinModuleData))
    }

    class DashboardEmptyCardViewHolder(override val containerView: View, private val dashboardEmptyCoinModule: DashboardEmptyCoinModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showTextInEmptySpace(dashboardEmptyCoinModuleData: DashboardEmptyCoinModule.DashboardEmptyCoinModuleData) {
            dashboardEmptyCoinModule.addEmptyCoinModule(itemView, dashboardEmptyCoinModuleData)
        }
    }
}