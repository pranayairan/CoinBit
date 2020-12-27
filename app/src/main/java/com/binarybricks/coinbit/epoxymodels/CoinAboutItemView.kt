package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.Coin
import com.binarybricks.coinbit.featurecomponents.ModuleItem
import com.binarybricks.coinbit.utils.Formaters
import com.binarybricks.coinbit.utils.openCustomTab
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManager
import com.binarybricks.coinbit.utils.resourcemanager.AndroidResourceManagerImpl

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinAboutItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvAboutCoin: TextView
    private val tvWebsiteValue: TextView
    private val tvTwitterValue: TextView
    private val tvRedditValue: TextView
    private val tvGithubValue: TextView

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    init {
        View.inflate(context, R.layout.coin_about_module, this)
        tvAboutCoin = findViewById(R.id.tvAboutCoin)
        tvWebsiteValue = findViewById(R.id.tvWebsiteValue)
        tvTwitterValue = findViewById(R.id.tvTwitterValue)
        tvRedditValue = findViewById(R.id.tvRedditValue)
        tvGithubValue = findViewById(R.id.tvGithubValue)
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoin(aboutCoinModuleData: AboutCoinModuleData) {
        val coin = aboutCoinModuleData.coin
        tvAboutCoin.text = getCleanedUpDescription(coin.description)
            ?: context.getString(R.string.info_unavailable)

        coin.website?.let { url ->
            if (url.isNotEmpty()) {
                tvWebsiteValue.text = getCleanUrl(url)
                tvWebsiteValue.setOnClickListener {
                    openCustomTab(url, context)
                }
            } else {
                tvWebsiteValue.text = context.getString(R.string.na)
            }
        }

        coin.twitter?.let { twt ->
            if (twt.isNotEmpty()) {
                tvTwitterValue.text = context.getString(R.string.twitterValue, twt)
                tvTwitterValue.setOnClickListener {
                    openCustomTab(
                        context.getString(R.string.twitterUrl, twt)
                            ?: "",
                        context
                    )
                }
            } else {
                tvTwitterValue.text = context.getString(R.string.na)
            }
        }

        coin.reddit?.let { reddit ->
            if (reddit.isNotEmpty()) {
                tvRedditValue.text = getCleanUrl(reddit)
                tvRedditValue.setOnClickListener {
                    openCustomTab(reddit, context)
                }
            } else {
                tvRedditValue.text = context.getString(R.string.na)
            }
        }

        coin.github?.let { git ->
            if (git.isNotEmpty()) {
                tvGithubValue.text = getCleanUrl(git)
                tvGithubValue.setOnClickListener {
                    openCustomTab(git, context)
                }
            } else {
                tvGithubValue.text = context.getString(R.string.na)
            }
        }

        tvAboutCoin.setOnClickListener {
            tvAboutCoin.maxLines = Int.MAX_VALUE
        }
    }

    private fun getCleanedUpDescription(description: String?): String? {
        if (!description.isNullOrBlank()) {
            return description.replace(Regex("<.*?>"), "")
        }
        return null
    }

    private fun getCleanUrl(url: String): String {
        return url.replace("http://", "").replace("https://", "")
    }

    data class AboutCoinModuleData(val coin: Coin) : ModuleItem
}
