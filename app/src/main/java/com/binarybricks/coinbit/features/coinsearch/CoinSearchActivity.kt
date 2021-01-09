package com.binarybricks.coinbit.features.coinsearch

import CoinSearchContract
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.epoxymodels.CoinSearchItemView
import com.binarybricks.coinbit.epoxymodels.coinSearchItemView
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.coindetails.CoinDetailsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_coin_search.*
import java.math.BigDecimal

class CoinSearchActivity : AppCompatActivity(), CoinSearchContract.View {
    private var isCoinInfoChanged = false

    companion object {
        @JvmStatic
        fun buildLaunchIntent(context: Context): Intent {
            return Intent(context, CoinSearchActivity::class.java)
        }
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val coinSearchPresenter: CoinSearchPresenter by lazy {
        CoinSearchPresenter(coinRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_search)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvSearchList.layoutManager = LinearLayoutManager(this)

        coinSearchPresenter.attachView(this)

        lifecycle.addObserver(coinSearchPresenter)

        coinSearchPresenter.loadAllCoins()

        FirebaseCrashlytics.getInstance().log("CoinSearchActivity")
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvSearchList, errorMessage, Snackbar.LENGTH_LONG)
    }

    override fun onCoinsLoaded(coinList: List<WatchedCoin>) {

        showCoinsInTheList(coinList)

        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                val filterString = filterText.toString().trim().toLowerCase()

                showCoinsInTheList(
                    coinList.filter { watchedCoin ->
                        watchedCoin.coin.coinName.contains(filterString, true) ||
                            watchedCoin.coin.symbol.contains(filterString, true)
                    }
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun showCoinsInTheList(coinList: List<WatchedCoin>) {
        rvSearchList.withModels {
            coinList.forEach { watchedCoin ->
                coinSearchItemView {
                    id(watchedCoin.coin.id)
                    watchedCoin(watchedCoin)
                    itemClickListener { _ ->
                        val coinDetailsIntent = CoinDetailsActivity.buildLaunchIntent(this@CoinSearchActivity, watchedCoin)
                        startActivity(coinDetailsIntent)
                    }
                    onWatchedChecked(object : CoinSearchItemView.OnSearchItemClickListener {
                        override fun onItemWatchedClicked(watched: Boolean) {
                            if (watchedCoin.purchaseQuantity == BigDecimal.ZERO) {
                                coinSearchPresenter.updateCoinWatchedStatus(watched, watchedCoin.coin.id, watchedCoin.coin.symbol)
                                isCoinInfoChanged = true
                            } else {
                                Snackbar.make(rvSearchList, getString(R.string.coin_already_purchased), Snackbar.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String) {

        val statusText = if (watched) {
            getString(R.string.coin_added_to_watchlist, coinSymbol)
        } else {
            getString(R.string.coin_removed_to_watchlist, coinSymbol)
        }

        Snackbar.make(rvSearchList, statusText, Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isCoinInfoChanged) {
            setResult(Activity.RESULT_OK)
        }

        finish()
    }
}
