package com.amahop.farefirst.ffprotect.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import com.amahop.farefirst.ffprotect.R
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.view_info_card.view.*

class ListItemCardView(context: Context, attrs: AttributeSet) :
    MaterialCardView(context, attrs) {
    init {
        inflate(context, R.layout.view_list_item_card, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ListItemCardView)
        tvTitle.text = attributes.getString(R.styleable.ListItemCardView_title)

        val iconRId = attributes.getResourceId(R.styleable.ListItemCardView_icon, -1)

        if (iconRId != -1) {
            val drawable =
                AppCompatResources.getDrawable(context, iconRId)
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
        attributes.recycle()
        isClickable = true
        isFocusable = true
    }
}