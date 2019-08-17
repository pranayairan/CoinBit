package com.binarybricks.coinbit.features.coinsearch

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.binarybricks.coinbit.adapterdelegates.CarousalAdapterDelegate
import com.binarybricks.coinbit.adapterdelegates.DiscoveryNewsAdapterDelegate
import com.binarybricks.coinbit.adapterdelegates.LabelCoinAdapterDelegate
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager

/**
Created by Pranay Airan 1/18/18.
 *
 * based on http://hannesdorfmann.com/android/adapter-delegates
 */

class CoinDiscoveryAdapter(
        toCurrency: String,
        androidResourceManager: AndroidResourceManager,
        var coinDiscoverList: MutableList<ModuleItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val DISCOVERY_CAROUSAL = 0
        private const val DISCOVERY_NEWS = 1
        private const val LABEL = 2
    }

    private val delegates: AdapterDelegatesManager<List<ModuleItem>> = AdapterDelegatesManager()

    init {
        delegates.addDelegate(DISCOVERY_CAROUSAL, CarousalAdapterDelegate(toCurrency, androidResourceManager))
        delegates.addDelegate(DISCOVERY_NEWS, DiscoveryNewsAdapterDelegate(androidResourceManager))
        delegates.addDelegate(LABEL, LabelCoinAdapterDelegate())
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.getItemViewType(coinDiscoverList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        delegates.onBindViewHolder(coinDiscoverList, position, viewHolder)
    }

    override fun getItemCount(): Int {
        return coinDiscoverList.size
    }
}