import com.binarybricks.coinbit.data.database.entities.CoinTransaction
import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareNews
import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinDashboardContract {

    interface View : BaseView {
        fun onWatchedCoinsAndTransactionsLoaded(watchedCoinList: List<WatchedCoin>, coinTransactionList: List<CoinTransaction>)
        fun onCoinPricesLoaded(coinPriceListMap: HashMap<String, CoinPrice>)
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun loadWatchedCoinsAndTransactions()
        fun loadCoinsPrices(fromCurrencySymbol: String, toCurrencySymbol: String)
        fun getTopCoinsByTotalVolume24hours(toCurrencySymbol: String)
        fun getLatestNewsFromCryptoCompare()
    }
}