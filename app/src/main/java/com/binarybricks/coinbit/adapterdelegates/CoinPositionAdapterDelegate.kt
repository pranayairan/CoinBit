package com.binarybricks.coinbit.adapterdelegates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.featurecomponents.CoinPositionCard
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Pranay Airan
 */

class CoinPositionAdapterDelegate(private val androidResourceManager: AndroidResourceManager) : AdapterDelegate<List<ModuleItem>>() {
    private val coinPositionCard by lazy {
        CoinPositionCard(androidResourceManager)
    }

    override fun isForViewType(items: List<ModuleItem>, position: Int): Boolean {
        return items[position] is CoinPositionCard.CoinPositionCardModuleData
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val coinPositionCardView = coinPositionCard.init(LayoutInflater.from(parent.context), parent)
        return CoinPositionCardViewHolder(coinPositionCardView, coinPositionCard)
    }

    override fun onBindViewHolder(items: List<ModuleItem>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val aboutCoinViewHolder = holder as CoinPositionCardViewHolder
        aboutCoinViewHolder.showAboutCoinText((items[position] as CoinPositionCard.CoinPositionCardModuleData))
    }

    class CoinPositionCardViewHolder(override val containerView: View, private val coinPositionCard: CoinPositionCard)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun showAboutCoinText(coinPositionCardModuleData: CoinPositionCard.CoinPositionCardModuleData) {
            coinPositionCard.showNoOfCoinsView(coinPositionCardModuleData)
        }
    }
}