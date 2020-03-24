package com.amahop.farefirst.ffprotect.ui.dashboard.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.amahop.farefirst.ffprotect.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_info_card.view.*

class InfoCardView(context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs),
    View.OnClickListener {
    private var onKnowMoreClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_info_card, this)

        btnKnowMore.setOnClickListener(this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.InfoCardView)
        tvTitle.text = attributes.getString(R.styleable.InfoCardView_title)
        tvMessage.text = attributes.getString(R.styleable.InfoCardView_message)
        attributes.recycle()

        val drawable =
            AppCompatResources.getDrawable(context, R.drawable.ic_help_outline_accent_24dp)
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    fun onKnowMoreClicked(listener: () -> Unit) {
        onKnowMoreClickListener = listener
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnKnowMore -> onKnowMoreClick()
        }
    }

    private fun onKnowMoreClick() {
        onKnowMoreClickListener?.invoke()
    }
}




