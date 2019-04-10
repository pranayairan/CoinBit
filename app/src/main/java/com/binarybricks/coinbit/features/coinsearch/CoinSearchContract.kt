import com.binarybricks.coinbit.data.database.entities.WatchedCoin
import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinSearchContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onCoinsLoaded(coinList: List<WatchedCoin>)
        fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String)
    }

    interface Presenter {
        fun loadAllCoins()
        fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String)
    }
}