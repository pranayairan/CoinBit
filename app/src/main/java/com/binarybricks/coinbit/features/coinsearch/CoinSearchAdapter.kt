package com.binarybricks.coinbit.features.coinsearch

/**
Created by Pranay Airan 1/26/18.
 */

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.math.BigDecimal

class CoinSearchAdapter(var searchList: List<WatchedCoin>) : ListAdapter<WatchedCoin, CoinSearchAdapter.ResultViewHolder>(WatchedCoinDiffCallback()),
        Filterable {

    init {
        submitList(searchList)
    }

    private val cropCircleTransformation by lazy {
        CropCircleTransformation()
    }

    private var mListener: OnSearchItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.coin_search_item, parent, false)

        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ResultViewHolder, position: Int) {
        viewHolder.tvCoinName.text = getItem(position).coin.coinName
        viewHolder.tvCoinSymbol.text = getItem(position).coin.symbol

        Picasso.get().load(BASE_CRYPTOCOMPARE_IMAGE_URL + "${getItem(position).coin.imageUrl}?width=50").error(R.mipmap.ic_launcher_round)
                .transform(cropCircleTransformation)
                .into(viewHolder.ivCoin)

        val purchaseQuantity = getItem(position).purchaseQuantity

        viewHolder.cbWatched.isChecked = purchaseQuantity > BigDecimal.ZERO || getItem(position).watched
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(searchQuery: CharSequence): Filter.FilterResults {

                val filterString = searchQuery.toString().trim().toLowerCase()

                val results = Filter.FilterResults()

                val list = searchList

                val count = list.size
                val filteredList = mutableListOf<WatchedCoin>()

                (0 until count)
                        .filter {
                            // Filter on the name
                            list[it].coin.coinName.contains(filterString, true) ||
                                    list[it].coin.symbol.contains(filterString, true)
                        }
                        .mapTo(filteredList) { list[it] }

                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
                submitList(results.values as MutableList<WatchedCoin>)
            }
        }
    }

    fun setOnSearchItemClickListener(listener: OnSearchItemClickListener) {
        mListener = listener
    }

    interface OnSearchItemClickListener {
        fun onSearchItemClick(view: View, position: Int, watchedCoin: WatchedCoin)
        fun onItemWatchedTicked(view: View, position: Int, watchedCoin: WatchedCoin, watched: Boolean)
        fun showPurchasedItemRemovedMessage()
    }

    fun updateCoinList(updateList: List<WatchedCoin>) {
        searchList = updateList
        submitList(searchList)
    }

    inner class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCoinName: TextView = view.findViewById(R.id.tvCoinPercentChange)
        val tvCoinSymbol: TextView = view.findViewById(R.id.tvCoinName)
        val ivCoin: ImageView = view.findViewById(R.id.ivCoin)
        val cbWatched: SwitchCompat = view.findViewById(R.id.scWatched)
        private val clCoinInfo: View = view.findViewById(R.id.clCoinInfo)

        init {
            // add second text
            clCoinInfo.setOnClickListener {
                mListener?.onSearchItemClick(it, adapterPosition, getItem(adapterPosition))
            }

            cbWatched.setOnClickListener {
                if (getItem(adapterPosition).purchaseQuantity == BigDecimal.ZERO) {
                    mListener?.onItemWatchedTicked(it, adapterPosition, getItem(adapterPosition), cbWatched.isChecked)
                } else {
                    cbWatched.isChecked = !cbWatched.isChecked
                    mListener?.showPurchasedItemRemovedMessage()
                }
            }
        }
    }
}

class WatchedCoinDiffCallback : DiffUtil.ItemCallback<WatchedCoin>() {

    override fun areItemsTheSame(oldItem: WatchedCoin, newItem: WatchedCoin): Boolean {
        return oldItem.coin.id == newItem.coin.id
    }

    override fun areContentsTheSame(oldItem: WatchedCoin, newItem: WatchedCoin): Boolean {
        return oldItem == newItem
    }
}