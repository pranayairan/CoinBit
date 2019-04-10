package com.binarybricks.coinbit.featurecomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.database.entities.Coin
import com.binarybricks.coinbit.utils.openCustomTab
import kotlinx.android.synthetic.main.coin_about_module.view.*
import timber.log.Timber

/**
 * Created by Pranay Airan
 *
 * Simple class that wraps all logic related to showing about us section
 */

class AboutCoinModule : Module() {

    override fun init(layoutInflater: LayoutInflater, parent: ViewGroup?): View {
        return layoutInflater.inflate(R.layout.coin_about_module, parent, false)
    }

    fun showAboutCoinText(inflatedView: View, aboutCoinModuleData: AboutCoinModuleData) {

        inflatedView.tvAboutCoin.text = getCleanedUpDescription(aboutCoinModuleData.coin.description) ?: inflatedView.context.getString(R.string.info_unavailable)

        aboutCoinModuleData.coin.website?.let { url ->
            if (url.isNotEmpty()) {
                inflatedView.tvWebsiteValue.text = getCleanUrl(url)
                inflatedView.tvWebsiteValue.setOnClickListener {
                    openCustomTab(url, inflatedView.context)
                }
            } else {
                inflatedView.tvWebsiteValue.text = inflatedView.context.getString(R.string.na)
            }
        }

        aboutCoinModuleData.coin.twitter?.let { twt ->
            if (twt.isNotEmpty()) {
                inflatedView.tvTwitterValue.text = inflatedView.context.getString(R.string.twitterValue, twt)
                inflatedView.tvTwitterValue.setOnClickListener {
                    openCustomTab(inflatedView.context.getString(R.string.twitterUrl, twt)
                            ?: "", inflatedView.context)
                }
            } else {
                inflatedView.tvTwitterValue.text = inflatedView.context.getString(R.string.na)
            }
        }

        aboutCoinModuleData.coin.reddit?.let { reddit ->
            if (reddit.isNotEmpty()) {
                inflatedView.tvRedditValue.text = getCleanUrl(reddit)
                inflatedView.tvRedditValue.setOnClickListener {
                    openCustomTab(reddit, inflatedView.context)
                }
            } else {
                inflatedView.tvRedditValue.text = inflatedView.context.getString(R.string.na)
            }
        }

        aboutCoinModuleData.coin.github?.let { git ->
            if (git.isNotEmpty()) {
                inflatedView.tvGithubValue.text = getCleanUrl(git)
                inflatedView.tvGithubValue.setOnClickListener {
                    openCustomTab(git, inflatedView.context)
                }
            } else {
                inflatedView.tvGithubValue.text = inflatedView.context.getString(R.string.na)
            }
        }

        inflatedView.tvAboutCoin.setOnClickListener {
            inflatedView.tvAboutCoin.maxLines = Int.MAX_VALUE
        }
    }

    override fun cleanUp() {
        Timber.d("Clean up about coinSymbol module")
    }

    data class AboutCoinModuleData(val coin: Coin) : ModuleItem

    private fun getCleanedUpDescription(description: String?): String? {
        if (!description.isNullOrBlank()) {
            return description.replace(Regex("<.*?>"), "")
        }
        return null
    }

    private fun getCleanUrl(url: String): String {
        return url.replace("http://", "").replace("https://", "")
    }
}