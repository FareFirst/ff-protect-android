package com.amahop.farefirst.ffprotect.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.amahop.farefirst.ffprotect.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_error_card.view.*

class ErrorCardView(context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs),
    View.OnClickListener {
    private var onEnableClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_error_card, this)

        btnEnable.setOnClickListener(this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ErrorCardView)
        tvTitle.text = attributes.getString(R.styleable.ErrorCardView_title)
        tvMessage.text = attributes.getString(R.styleable.ErrorCardView_message)
        val errorLogoRId = attributes.getResourceId(R.styleable.ErrorCardView_errorLogo, -1)

        if (errorLogoRId != -1) {
            val drawable =
                AppCompatResources.getDrawable(context, errorLogoRId)
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }

        attributes.recycle()
    }

    fun onEnableClicked(listener: () -> Unit) {
        onEnableClickListener = listener
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEnable -> onEnableClick()
        }
    }

    private fun onEnableClick() {
        onEnableClickListener?.invoke()
    }
}




