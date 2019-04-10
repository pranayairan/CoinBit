package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.CoinStatsticsModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pragya Agrawal
 */

class CoinStatsAdapterDelegate(private val androidResourceManager: AndroidResourceManager) : AdapterDelegate<List<ModuleItem>>() {

    private val coinStatsModule by lazy {
        CoinStatsticsModule(androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinStatsticsModule.CoinStatisticsModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinStatsModuleView = coinStatsModule.init(LayoutInflater.from(parent.context), parent)
        return CoinStatsViewHolder(coinStatsModuleView, coinStatsModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val aboutCoinViewHolder = holder as CoinStatsViewHolder
        aboutCoinViewHolder.showAboutCoinText((items[position] as CoinStatsticsModule.CoinStatisticsModuleData))
    }

    class CoinStatsViewHolder(override val containerView: View, private val coinStatisticsModule: CoinStatsticsModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showAboutCoinText(coinStatisticsModuleData: CoinStatsticsModule.CoinStatisticsModuleData) {
            coinStatisticsModule.showCoinStats(itemView, coinStatisticsModuleData)
        }
    }
}