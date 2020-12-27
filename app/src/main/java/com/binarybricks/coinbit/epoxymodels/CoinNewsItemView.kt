package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.network.models.CryptoPanicNews
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.openCustomTab
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinNewsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvFirstArticleTitle: TextView
    private val tvFirstArticleTime: TextView
    private val clFirstArticle: View

    private val tvSecondArticleTitle: TextView
    private val tvSecondArticleTime: TextView
    private val clSecondArticle: View

    private val tvThirdArticleTitle: TextView
    private val tvThirdArticleTime: TextView
    private val clThirdArticle: View

    private val tvMore: View
    private val tvNewsError: View
    private val newsContentGroup: View

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }
    private val formaters: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    init {
        View.inflate(context, R.layout.coin_news_module, this)
        tvFirstArticleTitle = findViewById(R.id.tvFirstArticleTitle)
        tvFirstArticleTime = findViewById(R.id.tvFirstArticleTime)
        clFirstArticle = findViewById(R.id.clFirstArticle)

        tvSecondArticleTitle = findViewById(R.id.tvSecondArticleTitle)
        tvSecondArticleTime = findViewById(R.id.tvSecondArticleTime)
        clSecondArticle = findViewById(R.id.clSecondArticle)

        tvThirdArticleTitle = findViewById(R.id.tvThirdArticleTitle)
        tvThirdArticleTime = findViewById(R.id.tvThirdArticleTime)
        clThirdArticle = findViewById(R.id.clThirdArticle)

        tvMore = findViewById(R.id.tvMore)
        tvNewsError = findViewById(R.id.tvNewsError)
        newsContentGroup = findViewById(R.id.newsContentGroup)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoinNews(coinNewsItemModuleData: CoinNewsItemModuleData) {
        // show the first news
        val newsResult = coinNewsItemModuleData.cryptoPanicNews.results
        if (newsResult != null && newsResult.isNotEmpty()) {
            tvFirstArticleTitle.text = newsResult[0].title
            tvFirstArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[0].created_at, true)
            clFirstArticle.setOnClickListener {
                openCustomTab(newsResult[0].url, context)
            }

            if (newsResult.size > 1) {
                tvSecondArticleTitle.text = newsResult[1].title
                tvSecondArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[1].created_at, true)
                clSecondArticle.setOnClickListener {
                    openCustomTab(newsResult[1].url, context)
                }
            }

            if (newsResult.size > 2) {
                tvThirdArticleTitle.text = newsResult[2].title
                tvThirdArticleTime.text = formaters.parseAndFormatIsoDate(newsResult[2].created_at, true)
                clThirdArticle.setOnClickListener {
                    openCustomTab(newsResult[2].url, context)
                }
            }
        } else {
            tvNewsError.visibility = View.VISIBLE
            newsContentGroup.visibility = View.GONE
        }
    }

    @CallbackProp
    fun setMoreClickListener(listener: OnClickListener?) {
        tvMore.setOnClickListener(listener)
    }

//    tvMore.setOnClickListener {
//        context.startActivity(NewsListActivity.buildLaunchIntent(context, coinNewsItemModuleData.coinName, coinNewsItemModuleData.coinSymbol))
//    }

    data class CoinNewsItemModuleData(val cryptoPanicNews: CryptoPanicNews) : ModuleItem
}
