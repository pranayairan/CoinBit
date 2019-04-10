package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.CoinBitCache
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.binarybricks.coinbit.utils.openCustomTab
import kotlinx.android.synthetic.main.dashboard_news_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 *
 * Simple class that wraps all logic related to showing news on dashboard.
 */

class DashboardNewsModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.dashboard_news_module, parent, false)
    }

    fun showNewsOnDashboard(inflatedView: View, dashboardNewsModuleData: DashboardNewsModuleData) {
        var i = 0

        if (!dashboardNewsModuleData.coinNews.isNullOrEmpty()) {
            inflatedView.pbLoading.visibility = View.GONE
            inflatedView.tvNewsTitle.text = dashboardNewsModuleData.coinNews[i].title
            inflatedView.clNewsArticleContainer.setOnClickListener {
                dashboardNewsModuleData.coinNews[i].url?.let {
                    openCustomTab(it, inflatedView.context)
                    CoinBitCache.updateCryptoCompareNews(dashboardNewsModuleData.coinNews[i])
                    if (i < dashboardNewsModuleData.coinNews.size) {
                        i++
                        inflatedView.tvNewsTitle.text = dashboardNewsModuleData.coinNews[i].title
                    }
                }
            }
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up about DashboardNews module")
    }

    data class DashboardNewsModuleData(val coinNews: List<CryptoCompareNews>?) : ModuleItem
}