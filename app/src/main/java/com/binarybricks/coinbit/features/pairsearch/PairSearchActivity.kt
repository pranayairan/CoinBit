package com.binarybricks.coinbit.features.pairsearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.binarybricks.coinbit.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_exchange_pair_search.*

class PairSearchActivity : AppCompatActivity() {

    private var pairSearchAdapter: PairSearchAdapter? = null

    companion object {
        private const val SEARCH_LIST = "search_list"
        private const val SEARCH_RESULT = "search_result"
        private const val COIN_SYMBOL = "coin_symbol"

        @JvmStatic
        fun buildLaunchIntent(context: Context, searchList: ArrayList<String>, coinSymbol: String): Intent {
            val intent = Intent(context, PairSearchActivity::class.java)
            intent.putStringArrayListExtra(SEARCH_LIST, searchList)
            intent.putExtra(COIN_SYMBOL, coinSymbol)
            return intent
        }

        fun getResultFromIntent(data: Intent): String {
            return data.getStringExtra(SEARCH_RESULT) ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_pair_search)

        val searchList = intent.getStringArrayListExtra(SEARCH_LIST)

        checkNotNull(searchList)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = getString(R.string.change_trading_pair)

        rvSearchList.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_thin_horizontal)?.let { dividerItemDecoration.setDrawable(it) }

        rvSearchList.addItemDecoration(dividerItemDecoration)
        pairSearchAdapter = PairSearchAdapter(searchList, intent.getStringExtra(COIN_SYMBOL) ?: "")

        rvSearchList.adapter = pairSearchAdapter

        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                pairSearchAdapter?.filter?.filter(filterText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        pairSearchAdapter?.setOnSearchItemClickListener(object : PairSearchAdapter.OnSearchItemClickListener {
            override fun onSearchItemClick(view: View, position: Int, text: String) {
                intent.putExtra(SEARCH_RESULT, text)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        FirebaseCrashlytics.getInstance().log("PairSearchActivity")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                // tell the calling activity/fragment that we're done deleting this transaction
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
