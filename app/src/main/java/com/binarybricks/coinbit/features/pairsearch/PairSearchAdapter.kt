package com.binarybricks.coinbit.features.pairsearch

/**
Created by Pranay Airan 1/26/18.
 */

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.binarybricks.coinbit.R

class PairSearchAdapter(val searchList: ArrayList<String>, val coinSymbol: String) : RecyclerView.Adapter<PairSearchAdapter.ResultViewHolder>(), Filterable {

    var filterSearchList: ArrayList<String> = searchList

    private var mListener: OnSearchItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.exchange_pair_search_item, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ResultViewHolder, position: Int) {
        viewHolder.tvSearchItemName.text = viewHolder.tvSearchItemName.context.getString(R.string.coinPair, coinSymbol.toUpperCase(), filterSearchList[position])
    }

    override fun getItemCount(): Int {
        return filterSearchList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(searchQuery: CharSequence): Filter.FilterResults {

                val filterString = searchQuery.toString().trim().toLowerCase()

                val results = Filter.FilterResults()

                val list = searchList

                val count = list.size
                val filteredList = ArrayList<String>(count)

                (0 until count)
                        .filter {
                            // Filter on the name
                            list[it].contains(filterString, true)
                        }
                        .mapTo(filteredList) { list[it] }

                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
                filterSearchList = results.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }

    fun setOnSearchItemClickListener(listener: OnSearchItemClickListener) {
        mListener = listener
    }

    interface OnSearchItemClickListener {
        fun onSearchItemClick(view: View, position: Int, text: String)
    }

    inner class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSearchItemName: TextView = view.findViewById(R.id.tvSearchItemName)

        init {
            // add second text
            view.setOnClickListener {
                mListener?.onSearchItemClick(it, layoutPosition, filterSearchList[layoutPosition])
            }
        }
    }
}