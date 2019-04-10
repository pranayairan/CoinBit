package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.featurecomponents.historicalchartmodule.HistoricalChartModule
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class HistoricalChartAdapterDelegate(
        private val fromCurrency: String,
        private val toCurrency: String,
        private val rxSchedulers: RxSchedulers,
        private val androidResourceManager: AndroidResourceManager
) : AdapterDelegate<List<ModuleItem>>() {

    private val historicalChartModule by lazy {
        HistoricalChartModule(rxSchedulers, androidResourceManager, fromCurrency, toCurrency)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is HistoricalChartModule.HistoricalChartModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val historicalChartModuleView = historicalChartModule.init(LayoutInflater.from(parent.context), parent)
        return HistoricalChartViewHolder(historicalChartModuleView, historicalChartModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        // load data
        val historicalChartViewHolder = holder as HistoricalChartViewHolder
        val historicalChartModuleData = items[position] as HistoricalChartModule.HistoricalChartModuleData

        historicalChartViewHolder.loadHistoricalChartData()
        historicalChartViewHolder.addCoinAndAnimateCoinPrice(historicalChartModuleData.coinPriceWithCurrentPrice)
    }

    fun cleanup() {
        historicalChartModule.cleanUp()
    }

    class HistoricalChartViewHolder(override val containerView: View, private val historicalChartModule: HistoricalChartModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun loadHistoricalChartData() {
            historicalChartModule.loadData(itemView)
        }

        fun addCoinAndAnimateCoinPrice(coinPrice: CoinPrice?) {
            historicalChartModule.addCoinAndAnimateCoinPrice(coinPrice)
        }
    }
}