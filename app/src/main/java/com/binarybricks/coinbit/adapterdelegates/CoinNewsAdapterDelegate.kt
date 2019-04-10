package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.featurecomponents.cryptonewsmodule.CoinNewsModule
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class CoinNewsAdapterDelegate(
        private val coinSymbol: String,
        private val coinName: String,
        private val rxSchedulers: RxSchedulers,
        private val androidResourceManager: AndroidResourceManager
) : AdapterDelegate<List<ModuleItem>>() {

    private val coinNewsModule by lazy {
        CoinNewsModule(rxSchedulers, coinSymbol, coinName, androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinNewsModule.CoinNewsModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinNewsModuleView = coinNewsModule.init(LayoutInflater.from(parent.context), parent)
        return CoinNewsViewHolder(coinNewsModuleView, coinNewsModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        // load data
        val historicalChartViewHolder = holder as CoinNewsViewHolder
        historicalChartViewHolder.loadCoinNewsData()
    }

    fun cleanup() {
        coinNewsModule.cleanUp()
    }

    class CoinNewsViewHolder(override val containerView: View, private val coinNewsModule: CoinNewsModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun loadCoinNewsData() {
            coinNewsModule.loadNewsData(itemView)
        }
    }
}