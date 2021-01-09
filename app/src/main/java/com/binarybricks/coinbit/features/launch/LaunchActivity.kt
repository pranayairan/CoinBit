package com.binarybricks.coinbit.features.launch

import LaunchContract
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.binarybricks.coinbit.CoinBitApplication
import com.binarybricks.coinbit.R
import com.binarybricks.coinbit.data.PreferenceManager
import com.binarybricks.coinbit.features.CryptoCompareRepository
import com.binarybricks.coinbit.features.HomeActivity
import com.binarybricks.coinbit.utils.CoinBitExtendedCurrency
import com.binarybricks.coinbit.utils.ui.IntroPageTransformer
import com.google.android.material.snackbar.Snackbar
import com.mynameismidori.currencypicker.CurrencyPicker
import kotlinx.android.synthetic.main.activity_launch.*
import timber.log.Timber

class LaunchActivity : AppCompatActivity(), LaunchContract.View {

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val launchPresenter: LaunchPresenter by lazy {
        LaunchPresenter(coinRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launch)

        launchPresenter.attachView(this)
        lifecycle.addObserver(launchPresenter)

        // determine if this is first time, if yes then show the animations else move away
        if (!PreferenceManager.getPreference(this, PreferenceManager.IS_LAUNCH_FTU_SHOWN, false)) {
            initializeUI()

            launchPresenter.loadAllCoins()
        } else {
            startActivity(HomeActivity.buildLaunchIntent(this))
            finish()
        }
    }

    private fun initializeUI() { // show  list of all currencies and option to choose one, default on phone locale

        // Set an Adapter on the ViewPager
        introPager.adapter = IntroAdapter(supportFragmentManager)

        // Set a PageTransformer
        introPager.setPageTransformer(false, IntroPageTransformer())
    }

    override fun onCoinsLoaded() {
        splashGroup.visibility = View.GONE
        viewpagerGroup.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        Timber.i("Suppressing back press")
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(introPager, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    fun openCurrencyPicker() {

        try {
            val picker = CurrencyPicker.newInstance(getString(R.string.select_currency)) // dialog title

            picker.setCurrenciesList(CoinBitExtendedCurrency.CURRENCIES)

            picker.setListener { name, code, _, _ ->
                Timber.d("Currency code selected $name,$code")
                PreferenceManager.setPreference(this, PreferenceManager.DEFAULT_CURRENCY, code)

                picker.dismiss() // Show currency that is picked.

                // show loading screen
                val currentFragment = (introPager.adapter as IntroAdapter).getCurrentFragment()
                if (currentFragment != null && currentFragment is IntroFragment) {
                    currentFragment.showLoadingScreen()
                }

                introPager.beginFakeDrag()

                // get list of all coins
                launchPresenter.getAllSupportedCoins(code)

                // FTU shown
                PreferenceManager.setPreference(this, PreferenceManager.IS_LAUNCH_FTU_SHOWN, true)
            }

            picker.show(supportFragmentManager, "CURRENCY_PICKER")
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    override fun onAllSupportedCoinsLoaded() {
        startActivity(HomeActivity.buildLaunchIntent(this))
        finish()
    }

    private inner class IntroAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var currentFragment: Fragment? = null

        fun getCurrentFragment(): Fragment? {
            return currentFragment
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.smiley_stack, getString(R.string.intro_coin_title),
                        getString(R.string.intro_coin_message), position, false
                    ) // 5000 curencies
                    currentFragment = newInstance
                    return newInstance
                }

                1 -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.graph, getString(R.string.intro_track_title),
                        getString(R.string.intro_track_message), position, false
                    ) // Track transactions
                    currentFragment = newInstance
                    return newInstance
                }

                else -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.lock, getString(R.string.intro_secure_title),
                        getString(R.string.intro_secure_message), position, true
                    ) // Secure
                    currentFragment = newInstance
                    return newInstance
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
