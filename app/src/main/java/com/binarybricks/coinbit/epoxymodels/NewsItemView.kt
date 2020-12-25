package com.binarybricks.coinbit.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.binarybricks.coinbit.R

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class NewsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val title: TextView
    private val date: TextView
    private val clArticle: View

    init {
        View.inflate(context, R.layout.news_item, this)
        title = findViewById(R.id.tvArticleTitle)
        date = findViewById(R.id.tvArticleTime)
        clArticle = findViewById(R.id.clArticle)
    }

    @TextProp
    fun setTitle(newsTitle: CharSequence) {
        title.text = newsTitle
    }

    @TextProp
    fun setNewsDate(formattedDate: CharSequence) {
        date.text = formattedDate
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        clArticle.setOnClickListener(listener)
    }
}
