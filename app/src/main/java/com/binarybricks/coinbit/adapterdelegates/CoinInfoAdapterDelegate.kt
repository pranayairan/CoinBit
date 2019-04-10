package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.CoinInfoModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pragya Agrawal
 */

class CoinInfoAdapterDelegate : AdapterDelegate<List<ModuleItem>>() {
    private val coinInfoModule by lazy {
        CoinInfoModule()
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinInfoModule.CoinInfoModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinInfoModuleView = coinInfoModule.init(LayoutInflater.from(parent.context), parent)
        return CoinInfoViewHolder(coinInfoModuleView, coinInfoModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val coinInfoViewHolder = holder as CoinInfoViewHolder
        coinInfoViewHolder.showCoinInfo((items[position] as CoinInfoModule.CoinInfoModuleData))
    }

    class CoinInfoViewHolder(override val containerView: View, private val coinInfoModule: CoinInfoModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showCoinInfo(coinInfoModuleData: CoinInfoModule.CoinInfoModuleData) {
            coinInfoModule.showCoinInfo(itemView, coinInfoModuleData)
        }
    }
}