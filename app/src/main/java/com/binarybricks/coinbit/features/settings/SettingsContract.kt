import com.binarybricks.coinbit.features.BaseView

/**
Created by Pranay Airan
 */

interface SettingsContract {

    interface View : BaseView {
        fun onCoinListRefreshed()
        fun onExchangeListRefreshed()
    }

    interface Presenter {
        fun refreshCoinList(defaultCurrency: String)
        fun refreshExchangeList()
    }
}