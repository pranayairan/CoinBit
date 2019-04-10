package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.DashboardAddNewCoinModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class DashboardAddNewCoinAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {
    private val dashboardAddNewCoinModule by lazy {
        DashboardAddNewCoinModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is DashboardAddNewCoinModule.DashboardAddNewCoinModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val dashboardAddNewCoinModuleView = dashboardAddNewCoinModule.init(LayoutInflater.from(parent.context), parent)
        return DashboardAddNewCoinViewHolder(dashboardAddNewCoinModuleView, dashboardAddNewCoinModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val dashboardAddNewCoinViewHolder = holder as DashboardAddNewCoinViewHolder
        dashboardAddNewCoinViewHolder.addNewCoinListener((items[position] as DashboardAddNewCoinModule.DashboardAddNewCoinModuleData))
    }

    class DashboardAddNewCoinViewHolder(override val containerView: View, private val dashboardAddNewCoinModule: DashboardAddNewCoinModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun addNewCoinListener(dashboardAddNewCoinModuleData: DashboardAddNewCoinModule.DashboardAddNewCoinModuleData) {
            dashboardAddNewCoinModule.addNewCoinListener(containerView, dashboardAddNewCoinModuleData)
        }
    }
}