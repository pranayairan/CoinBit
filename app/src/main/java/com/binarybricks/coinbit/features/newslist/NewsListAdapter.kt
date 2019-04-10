package com.binarybricks.coinbit.features.newslist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.openCustomTab
import kotlinx.android.synthetic.main.news_item.view.*

/**
Created by Pranay Airan 1/18/18.
 *
 * based on http://hannesdorfmann.com/android/adapter-delegates
 */

class NewsListAdapter(
    private val cryptoPanicNews: CryptoPanicNews,
    private val androidResourceManager: AndroidResourceManager
) : RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: NewsViewHolder, position: Int) {
        val newsResult = cryptoPanicNews.results?.get(position)
        newsResult?.let { result ->
            viewHolder.title?.text = result.title
            viewHolder.date?.text = formatter.parseAndFormatIsoDate(result.created_at, true)
            viewHolder.clArticle?.setOnClickListener {
                openCustomTab(result.url, it.context)
            }
        }
    }

    override fun getItemCount(): Int {
        return cryptoPanicNews.results?.size ?: 0
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = null
        var date: TextView? = null
        var clArticle: View? = null

        init {
            title = itemView.tvArticleTitle
            date = itemView.tvArticleTime
            clArticle = itemView.clArticle
        }
    }
}