package com.binarybricks.coinbit.adapterdelegates

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.CoinTransactionHistoryModule
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class CoinTransactionAdapterDelegate(private val androidResourceManager: AndroidResourceManager) : AdapterDelegate<List<ModuleItem>>() {

    private val coinTransactionHistoryModule by lazy {
        CoinTransactionHistoryModule(androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinTransactionHistoryModule.CoinTransactionHistoryModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinTransactionHistoryModuleView = coinTransactionHistoryModule.init(LayoutInflater.from(parent.context), parent)
        return CoinTransactionViewHolder(coinTransactionHistoryModuleView, coinTransactionHistoryModule)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        val coinTransactionViewHolder = holder as CoinTransactionViewHolder
        coinTransactionViewHolder.showRecentTransactions((items[position] as CoinTransactionHistoryModule.CoinTransactionHistoryModuleData))
    }

    class CoinTransactionViewHolder(override val containerView: View, private val coinTransactionHistoryModule: CoinTransactionHistoryModule)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showRecentTransactions(coinTransactionHistoryModuleData: CoinTransactionHistoryModule.CoinTransactionHistoryModuleData) {
            coinTransactionHistoryModule.showRecentTransactions(itemView, coinTransactionHistoryModuleData)
        }
    }
}