package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import kotlinx.android.synthetic.main.dashboard_coin_list_header_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 */

class DashboardCoinListHeaderModule : Module() {
    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.dashboard_coin_list_header_module, parent, false)
    }

    fun showHeaderText(inflatedView: View, dashboardCoinListHeaderModuleData: DashboardCoinListHeaderModuleData) {
        inflatedView.tvLabel.text = dashboardCoinListHeaderModuleData.title
    }

    override fun cleanUp() {
        Timber.d("Clean up coinSymbol list module")
    }

    data class DashboardCoinListHeaderModuleData(val title: String) : ModuleItem
}
