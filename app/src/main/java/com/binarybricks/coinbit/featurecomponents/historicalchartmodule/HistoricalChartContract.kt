import com.binarybricks.coinbit.features.BaseView
import com.binarybricks.coinbit.network.models.CoinPrice
import com.binarybricks.coinbit.network.models.CryptoCompareHistoricalResponse

/**
Created by Pranay Airan
 */

interface HistoricalChartContract {

    interface View : BaseView {
        fun showOrHideChartLoadingIndicator(showLoading: Boolean = true)
        fun addCoinAndAnimateCoinPrice(coinPrice: CoinPrice?)
        fun onHistoricalDataLoaded(period: String, historicalDataPair: Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>)
    }

    interface Presenter {
        fun loadHistoricalData(period: String, fromCurrency: String, toCurrency: String)
    }
}
