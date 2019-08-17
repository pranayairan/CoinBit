package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.featurecomponents.cointickermodule.CoinTickerModule
import com.binarybricks.coinbit.data.database.CoinBitDatabase
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class CoinTickerAdapterDelegate(
        private val coinName: String,
        private val rxSchedulers: RxSchedulers,
        private val coinBitDatabase: CoinBitDatabase?,
        private val androidResourceManager: AndroidResourceManager
) : AdapterDelegate<List<ModuleItem>>() {

    private val coinTickerModule by lazy {
        CoinTickerModule(coinBitDatabase, rxSchedulers, androidResourceManager, coinName)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinTickerModule.CoinTickerModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinTickerModuleView = coinTickerModule.init(LayoutInflater.from(parent.context), parent)
        return CoinTickerViewHolder(coinTickerModuleView, coinTickerModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        // load data
        val coinTickerViewHolder = holder as CoinTickerViewHolder
        coinTickerViewHolder.loadTickerData()
    }

    class CoinTickerViewHolder(override val containerView: View, private val coinTickerModule: CoinTickerModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun loadTickerData() {
            coinTickerModule.loadTickerData(containerView)
        }
    }

    fun cleanup() {
        coinTickerModule.cleanUp()
    }
}