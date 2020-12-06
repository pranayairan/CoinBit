package com.binarybricks.coinbit.features.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.binarybricks.coinbit.R
import kotlinx.android.synthetic.main.intro_fragment_layout.*

class IntroFragment : Fragment() {
    private var animationRes: Int = 0
    private lateinit var headerTitle: String
    private lateinit var headerSubtitle: String
    private var showbutton: Boolean = false
    private var page: Int = 0

    companion object {
        private const val TITLE = "title"
        private const val SUB_TITLE = "subtitle"
        private const val ANIMATION = "animation"
        private const val SHOW_BUTTON = "showbutton"
        private const val PAGE = "page"

        fun newInstance(animation: Int, headerTitle: String, headerSubtitle: String, page: Int, showbutton: Boolean): IntroFragment {
            val frag = IntroFragment()
            val b = Bundle()
            b.putInt(ANIMATION, animation)
            b.putString(TITLE, headerTitle)
            b.putString(SUB_TITLE, headerSubtitle)
            b.putBoolean(SHOW_BUTTON, showbutton)
            b.putInt(PAGE, page)
            frag.arguments = b
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            animationRes = it.getInt(ANIMATION)
            headerTitle = it.getString(TITLE) ?: ""
            headerSubtitle = it.getString(SUB_TITLE) ?: ""
            showbutton = it.getBoolean(SHOW_BUTTON)
            page = it.getInt(PAGE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout resource file
        val view = activity?.layoutInflater?.inflate(R.layout.intro_fragment_layout, container, false)

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view?.tag = page
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            findViewById<LottieAnimationView>(R.id.animationView).setAnimation(animationRes)
            if (page == 1) {
                findViewById<LottieAnimationView>(R.id.animationView).speed = 1.5f
            }
            findViewById<TextView>(R.id.tvTitle).text = headerTitle
            findViewById<TextView>(R.id.tvSubTitle).text = headerSubtitle
            if (showbutton) {
                findViewById<TextView>(R.id.btnChooseCurrency).visibility = View.VISIBLE
            } else {
                findViewById<TextView>(R.id.btnChooseCurrency).visibility = View.GONE
            }

            findViewById<TextView>(R.id.btnChooseCurrency).setOnClickListener {
                (activity as? LaunchActivity)?.openCurrencyPicker()
            }
        }
    }

    fun showLoadingScreen() {
        contentGroup.visibility = View.GONE
        btnChooseCurrency.visibility = View.GONE
        pbLoading.visibility = View.VISIBLE
        tvLoading.visibility = View.VISIBLE
    }
}
