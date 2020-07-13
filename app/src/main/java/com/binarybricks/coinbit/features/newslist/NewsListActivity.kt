package com.binarybricks.coinbit.features.newslist

import CryptoNewsContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.View
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.cryptonewsmodule.CryptoNewsPresenter
import com.binarybricks.coinbit.featurecomponents.cryptonewsmodule.CryptoNewsRepository
import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.binarybricks.coinbit.utils.openCustomTab
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_news_list.*

/**
 * Created by Pragya Agrawal
 * Activity showing all news items
 */
class NewsListActivity : AppCompatActivity(), CryptoNewsContract.View {

    companion object {
        private const val COIN_FULL_NAME = "COIN_FULL_NAME"
        private const val COIN_SYMBOL = "COIN_SYMBOL"

        @JvmStatic
        fun buildLaunchIntent(context: Context, coinName: String, coinSymbol: String): Intent {
            val intent = Intent(context, NewsListActivity::class.java)
            intent.putExtra(COIN_FULL_NAME, coinName)
            intent.putExtra(COIN_SYMBOL, coinSymbol)
            return intent
        }
    }

    private val rxSchedulers: RxSchedulers by lazy {
        RxSchedulers.instance
    }

    private val cryptoNewsRepository by lazy {
        CryptoNewsRepository(rxSchedulers)
    }
    private val cryptoNewsPresenter: CryptoNewsPresenter by lazy {
        CryptoNewsPresenter(rxSchedulers, cryptoNewsRepository)
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val coinFullName = intent.getStringExtra(COIN_FULL_NAME).trim()
        val coinSymbol = intent.getStringExtra(COIN_SYMBOL).trim()

        supportActionBar?.title = getString(R.string.newsActivityTitle, coinFullName)

        rvNewsList.layoutManager = LinearLayoutManager(this)

        cryptoNewsPresenter.attachView(this)

        lifecycle.addObserver(cryptoNewsPresenter)

        cryptoNewsPresenter.getCryptoNews(coinSymbol)

        FirebaseCrashlytics.getInstance().log("NewsListActivity")
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews) {
        val newsListAdapter = NewsListAdapter(cryptoPanicNews, androidResourceManager)
        rvNewsList.adapter = newsListAdapter

        tvFooter.setOnClickListener {
            openCustomTab(getString(R.string.crypto_panic_url), this)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvNewsList, errorMessage, Snackbar.LENGTH_LONG)
    }
}
