package com.binarybricks.coinbit.featurecomponents.cryptonewsmodule

import CryptoNewsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.Module
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.features.newslist.NewsListActivity
import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.openCustomTab
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import kotlinx.android.synthetic.main.coin_news_module.view.*

/**
 * Created by Pragya Agrawal
 * A compound layout to see coin news
 */
class CoinNewsModule(
    private val rxSchedulers: RxSchedulers,
    private val coinSymbol: String,
    private val coinName: String,
    private val androidResourceManager: AndroidResourceManager
) : Module(), CryptoNewsContract.View {

    private lateinit var inflatedView: View

    private var cryptoPanicNews: CryptoPanicNews? = null

    private val cryptoNewsRepository by lazy {
        CryptoNewsRepository(rxSchedulers)
    }
    private val cryptoNewsPresenter: CryptoNewsPresenter by lazy {
        CryptoNewsPresenter(rxSchedulers, cryptoNewsRepository)
    }

    private val formaters: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {

        val inflatedView = layoutInflater.inflate(R.layout.coin_news_module, parent, false)

        cryptoNewsPresenter.attachView(this)

        return inflatedView
    }

    fun loadNewsData(inflatedView: View) {
        this.inflatedView = inflatedView
        cryptoNewsPresenter.getCryptoNews(coinSymbol)
    }

    // cleanup
    fun cleanYourSelf() {
        cryptoNewsPresenter.detachView()
        cryptoPanicNews = null
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            inflatedView.pbLoadingCurrencyList.visibility = View.GONE
        } else {
            inflatedView.pbLoadingCurrencyList.visibility = View.VISIBLE
        }
    }

    override fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews) {
        // show the first news
        val newsResult = cryptoPanicNews.results
        if (newsResult != null && newsResult.isNotEmpty()) {
            inflatedView.tvFirstArticleTitle.text = newsResult[0].title
            inflatedView.tvFirstArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[0].created_at, true)
            inflatedView.clFirstArticle.setOnClickListener {
                openCustomTab(newsResult[0].url, inflatedView.context)
            }

            if (newsResult.size > 1) {
                inflatedView.tvSecondArticleTitle.text = newsResult[1].title
                inflatedView.tvSecondArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[1].created_at, true)
                inflatedView.clSecondArticle.setOnClickListener {
                    openCustomTab(newsResult[1].url, inflatedView.context)
                }
            }

            if (newsResult.size > 2) {
                inflatedView.tvThirdArticleTitle.text = newsResult[2].title
                inflatedView.tvThirdArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[2].created_at, true)
                inflatedView.clThirdArticle.setOnClickListener {
                    openCustomTab(newsResult[2].url, inflatedView.context)
                }
            }

            inflatedView.tvMore.setOnClickListener {
                inflatedView.context.startActivity(NewsListActivity.buildLaunchIntent(inflatedView.context, coinName, coinSymbol))
            }
        } else {
            inflatedView.tvNewsError.visibility = View.VISIBLE
            inflatedView.newsContentGroup.visibility = View.GONE
        }
    }

    override fun onNetworkError(errorMessage: String) {
        inflatedView.tvNewsError.visibility = View.VISIBLE
        inflatedView.newsContentGroup.visibility = View.GONE
    }

    override fun cleanUp() {
        cryptoPanicNews = null
        cryptoNewsPresenter.detachView()
    }

    class CoinNewsModuleData : ModuleItem
}
