package com.binarybricks.coinbit.features.coin

import CoinContract
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.featurecomponents.*
import com.binarybricks.coinbit.featurecomponents.cointickermodule.CoinTickerModule
import com.binarybricks.coinbit.featurecomponents.cryptonewsmodule.CoinNewsModule
import com.binarybricks.coinbit.featurecomponents.historicalchartmodule.HistoricalChartModule
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.coindetails.CoinDetailsActivity
import com.binarybricks.coinbit.features.coindetails.CoinDetailsPagerActivity
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.schedulers.RxSchedulers
import com.binarybricks.coinbit.utils.defaultExchange
import com.binarybricks.coinbit.utils.dpToPx
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl
import com.binarybricks.coinbit.utils.ui.OnVerticalScrollListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_coin_details.*
import java.math.BigDecimal

class CoinFragment : Fragment(), CoinContract.View {

    private val coinDetailList: MutableList<ModuleItem> = mutableListOf()
    private var coinAdapter: CoinAdapter? = null
    private var coinPrice: CoinPrice? = null
    private var watchedMenuItem: MenuItem? = null
    private var isCoinWatched = false
    private var isCoinedPurchased = false
    private var watchedCoin: WatchedCoin? = null

    private val rxSchedulers: RxSchedulers by lazy {
        RxSchedulers.instance
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(rxSchedulers, CoinBitApplication.database)
    }

    private val coinPresenter: CoinPresenter by lazy {
        CoinPresenter(rxSchedulers, coinRepo)
    }

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(requireContext())
    }
    val toCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(context?.applicationContext)
    }

    companion object {
        private const val WATCHED_COIN = "WATCHED_COIN"

        @JvmStatic
        fun getArgumentBundle(watchedCoin: WatchedCoin): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(WATCHED_COIN, watchedCoin)
            return bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_coin_details, container, false)

        watchedCoin = arguments?.getParcelable(WATCHED_COIN)

        setHasOptionsMenu(true)

        FirebaseCrashlytics.getInstance().log("CoinFragment")

        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watchedCoin?.let {

            coinPresenter.attachView(this)

            lifecycle.addObserver(coinPresenter)

            rvCoinDetails.layoutManager = LinearLayoutManager(context)

            val toolBarDefaultElevation = dpToPx(context, 12) // default elevation of toolbar

            rvCoinDetails.addOnScrollListener(object : OnVerticalScrollListener() {
                override fun onScrolled(offset: Int) {
                    super.onScrolled(offset)
                    (activity as? CoinDetailsPagerActivity)?.supportActionBar?.elevation = Math.min(toolBarDefaultElevation.toFloat(), offset.toFloat())
                    (activity as? CoinDetailsActivity)?.supportActionBar?.elevation = Math.min(toolBarDefaultElevation.toFloat(), offset.toFloat())
                }
            })

            // load data
            coinPresenter.loadCurrentCoinPrice(it, toCurrency)

            if (it.purchaseQuantity > BigDecimal.ZERO) {
                isCoinedPurchased = true
                isCoinWatched = true
            } else if (it.watched) {
                isCoinWatched = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.coin_details_menu, menu)

        watchedMenuItem = menu.findItem(R.id.action_watch)

        changeCoinMenu(isCoinWatched, isCoinedPurchased)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_watch -> {
                isCoinWatched = !isCoinWatched
                changeCoinMenu(isCoinWatched, isCoinedPurchased)
                changeCoinWatchedStatus(isCoinWatched)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeCoinWatchedStatus(isCoinWatched: Boolean) {
        watchedCoin?.let {
            coinPresenter.updateCoinWatchedStatus(isCoinWatched, it.coin.id, it.coin.symbol)

            (activity as? CoinDetailsPagerActivity)?.isCoinInfoChanged = true
        }
    }

    private fun changeCoinMenu(isCoinWatched: Boolean, isCoinPurchased: Boolean) {
        if (!isCoinPurchased) {
            if (isCoinWatched) {
                watchedMenuItem?.icon = context?.getDrawable(R.drawable.ic_watching)
                watchedMenuItem?.title = context?.getString(R.string.remove_to_watchlist)
            } else {
                watchedMenuItem?.icon = context?.getDrawable(R.drawable.ic_watch)
                watchedMenuItem?.title = context?.getString(R.string.add_to_watchlist)
            }
        } else {
            watchedMenuItem?.isVisible = false
        }
    }

    override fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String) {

        val statusText = if (watched) {
            getString(R.string.coin_added_to_watchlist, coinSymbol)
        } else {
            getString(R.string.coin_removed_to_watchlist, coinSymbol)
        }

        Snackbar.make(rvCoinDetails, statusText, Snackbar.LENGTH_LONG).show()
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvCoinDetails, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onCoinPriceLoaded(coinPrice: CoinPrice?, watchedCoin: WatchedCoin) {

        this.coinPrice = coinPrice

        coinDetailList.add(HistoricalChartModule.HistoricalChartModuleData(coinPrice))

        // coinDetailList.add(AddCoinModule.AddCoinModuleData(watchedCoin.coin))

        if (coinPrice != null) {
            coinDetailList.add(CoinStatsticsModule.CoinStatisticsModuleData(coinPrice))

            coinDetailList.add(
                CoinInfoModule.CoinInfoModuleData(
                    coinPrice.market
                        ?: defaultExchange,
                    watchedCoin.coin.algorithm, watchedCoin.coin.proofType
                )
            )
        }

        coinDetailList.add(CoinTickerModule.CoinTickerModuleData())

        coinDetailList.add(CoinNewsModule.CoinNewsModuleData())

        coinDetailList.add(AboutCoinModule.AboutCoinModuleData(watchedCoin.coin))

        coinAdapter = CoinAdapter(
            watchedCoin.coin.symbol, toCurrency, watchedCoin.coin.coinName,
            coinDetailList, CoinBitApplication.database, rxSchedulers, androidResourceManager
        )

        rvCoinDetails?.adapter = coinAdapter
        coinAdapter?.notifyDataSetChanged()

        coinPresenter.loadRecentTransaction(watchedCoin.coin.symbol)

        coinDetailList.add(GenericFooterModule.FooterModuleData())
    }

    override fun onRecentTransactionLoaded(coinTransactionList: List<CoinTransaction>) {
        if (!coinTransactionList.isEmpty()) {
            coinPrice?.let {
                // add position module
                coinDetailList.add(2, CoinPositionCard.CoinPositionCardModuleData(it, coinTransactionList))
                coinAdapter?.coinDetailList = coinDetailList
                coinAdapter?.notifyItemChanged(2)
            }

            // add transaction module
            coinDetailList.add(4, CoinTransactionHistoryModule.CoinTransactionHistoryModuleData(coinTransactionList))
            coinAdapter?.coinDetailList = coinDetailList
            coinAdapter?.notifyItemChanged(4)
        }
    }

    override fun onDestroy() {
        coinAdapter?.cleanup()
        super.onDestroy()
    }
}
