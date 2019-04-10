import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.features.BaseView

/**
 * Created by Pragya Agrawal
 */

interface CryptoNewsContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews)
    }

    interface Presenter {
        fun getCryptoNews(coinSymbol: String)
    }
}