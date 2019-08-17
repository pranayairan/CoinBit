package com.binarybricks.coinbit.features.ticker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.binarybricks.coinbit.network.models.CryptoTicker
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.getUrlWithoutParameters
import com.binarybricks.coinbit.utils.openCustomTab
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.ticker_item.view.*
import java.util.*

/**
Created by Pranay Airan 1/18/18.
 *
 * based on http://hannesdorfmann.com/android/adapter-delegates
 */

class CoinTickerAdapter(
    private val tickerData: List<CryptoTicker>,
    val currency: Currency,
    private val androidResourceManager: AndroidResourceManager
) : RecyclerView.Adapter<CoinTickerAdapter.NewsViewHolder>() {

    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private val cropCircleTransformation by lazy {
        CropCircleTransformation()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ticker_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: NewsViewHolder, position: Int) {

        viewHolder.tvFromCoin?.text = tickerData[position].base
        viewHolder.tvToPrice?.text = tickerData[position].target
        viewHolder.tvPrice?.text = formatter.formatAmount(tickerData[position].last, currency, true)
        viewHolder.tvExchange?.text = tickerData[position].marketName
        viewHolder.tvVolume?.text = formatter.formatAmount(tickerData[position].convertedVolumeUSD, currency, true)
        Picasso.get().load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[position].imageUrl).error(R.mipmap.ic_launcher_round)
                .transform(cropCircleTransformation)
                .into(viewHolder.ivExchange)
        viewHolder.clMarket?.setOnClickListener {
            if (tickerData[position].exchangeUrl.isNotBlank()) {
                viewHolder.clMarket?.context?.let { context ->
                    openCustomTab(getUrlWithoutParameters(tickerData[position].exchangeUrl), context)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tickerData.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivExchange: ImageView? = null
        var tvFromCoin: TextView? = null
        var tvToPrice: TextView? = null
        var tvExchange: TextView? = null
        var tvPrice: TextView? = null
        var tvVolume: TextView? = null
        var clMarket: View? = null

        init {
            ivExchange = itemView.ivExchange
            tvFromCoin = itemView.tvFromCoin
            tvToPrice = itemView.tvToPrice
            tvExchange = itemView.tvExchange
            tvPrice = itemView.tvPrice
            tvVolume = itemView.tvVolume
            clMarket = itemView.clMarket
        }
    }
}