package com.binarybricks.coinbit.features.coin

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.binarybricks.coinbit.adapterdelegates.*
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager

/**
Created by Pranay Airan 1/18/18.
 *
 * based on http://hannesdorfmann.com/android/adapter-delegates
 */

class CoinAdapter(
        fromCurrency: String,
        toCurrency: String,
        coinName: String,
        var coinDetailList: List<ModuleItem>,
        coinBitDatabase: CoinBitDatabase?,
        rxSchedulers: RxSchedulers,
        androidResourceManager: AndroidResourceManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val HISTORICAL_CHART = 0
        private const val ADD_COIN = 1
        private const val COIN_POSITION = 2
        private const val COIN_INFO = 3
        private const val COIN_NEWS = 4
        private const val COIN_STATS = 5
        private const val ABOUT_COIN = 6
        private const val COIN_TRANSACTION = 7
        private const val FOOTER = 8
        private const val COIN_TICKER = 9
    }

    private val delegates: AdapterDelegatesManager<List<ModuleItem>> = AdapterDelegatesManager()

    private val coinTickerAdapterDelegate by lazy {
        CoinTickerAdapterDelegate(coinName, rxSchedulers, coinBitDatabase, androidResourceManager)
    }

    private val historicalChartAdapterDelegate by lazy {
        HistoricalChartAdapterDelegate(fromCurrency, toCurrency, rxSchedulers, androidResourceManager)
    }

    private val coinNewsAdapterDelegate by lazy {
        CoinNewsAdapterDelegate(fromCurrency, coinName, rxSchedulers, androidResourceManager)
    }

    init {
        delegates.addDelegate(HISTORICAL_CHART, historicalChartAdapterDelegate)
        delegates.addDelegate(ADD_COIN, AddCoinAdapterDelegate())
        delegates.addDelegate(COIN_POSITION, CoinPositionAdapterDelegate(androidResourceManager))
        delegates.addDelegate(COIN_INFO, CoinInfoAdapterDelegate())
        delegates.addDelegate(COIN_NEWS, coinNewsAdapterDelegate)
        delegates.addDelegate(COIN_TICKER, coinTickerAdapterDelegate)
        delegates.addDelegate(COIN_STATS, CoinStatsAdapterDelegate(androidResourceManager))
        delegates.addDelegate(ABOUT_COIN, AboutCoinAdapterDelegate())
        delegates.addDelegate(COIN_TRANSACTION, CoinTransactionAdapterDelegate(androidResourceManager))
        delegates.addDelegate(FOOTER, GenericFooterAdapterDelegate())
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.getItemViewType(coinDetailList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        delegates.onBindViewHolder(coinDetailList, position, viewHolder)
    }

    override fun getItemCount(): Int {
        return coinDetailList.size
    }

    fun cleanup() {
        historicalChartAdapterDelegate.cleanup()
        coinTickerAdapterDelegate.cleanup()
        coinNewsAdapterDelegate.cleanup()
    }
}