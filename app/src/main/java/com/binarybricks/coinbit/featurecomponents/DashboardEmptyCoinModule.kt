package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.features.coinsearch.CoinSearchActivity
import kotlinx.android.synthetic.main.dashboard_empty_card_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 *
 * Simple class that shows the empty card on dashboard
 */

class DashboardEmptyCoinModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.dashboard_empty_card_module, parent, false)
    }

    fun addEmptyCoinModule(inflatedView: View, dashboardEmptyCoinModuleData: DashboardEmptyCoinModuleData) {
        inflatedView.tvEmptyMessage.text = dashboardEmptyCoinModuleData.emptySpaceText

        if (dashboardEmptyCoinModuleData.ctaButtonText.isNotEmpty()) {
            inflatedView.btnAddTransaction.text = dashboardEmptyCoinModuleData.ctaButtonText
        }
        inflatedView.btnAddTransaction.setOnClickListener {
            inflatedView.context.startActivity(CoinSearchActivity.buildLaunchIntent(inflatedView.context))
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up empty coinSymbol module")
    }

    data class DashboardEmptyCoinModuleData(val emptySpaceText: String, val ctaButtonText: String = "") : ModuleItem
}