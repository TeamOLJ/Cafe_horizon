package com.teamolj.cafehorizon.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.view_list_item.view.*

class ListItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_list_item, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ListItemView,
            0, 0
        ).apply {

            try {
                val backgroundColor =
                    getColor(R.styleable.ListItemView_viewListItemBgColor, 0xFFFFFFFF.toInt())

                val titleColor = getColor(R.styleable.ListItemView_viewListItemTitleTextColor, 0xFF000000.toInt())
                val titleSize = getDimension(R.styleable.ListItemView_viewListItemTitleTextSize, 16F)

                val descColor = getColor(R.styleable.ListItemView_viewListItemDescTextColor, 0xFF000000.toInt())
                val descSize = getDimension(R.styleable.ListItemView_viewListItemDescTextSize, 16F)

                val itemType = getString(R.styleable.ListItemView_viewListItemType)

                backgroundLayout.setBackgroundColor(backgroundColor)

                itemTitle.text = getString(R.styleable.ListItemView_viewListItemTitleText)
                itemTitle.setTextColor(titleColor)
                itemTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)

                itemDescription.text = getString(R.styleable.ListItemView_viewListItemDescText)
                itemDescription.setTextColor(descColor)
                itemDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, descSize)

                when (itemType) {
                    "view_titleOnly" -> {
                        itemDescription.visibility = View.GONE
                        itemSlider.visibility = View.GONE
                        itemSwitch.visibility = View.GONE
                    }
                    "view_title_description" -> {
                        itemDescription.visibility = View.VISIBLE
                        itemSlider.visibility = View.GONE
                        itemSwitch.visibility = View.GONE
                    }
                    "view_title_description_slider" -> {
                        itemDescription.visibility = View.VISIBLE
                        itemSlider.visibility = View.VISIBLE
                        itemSwitch.visibility = View.GONE
                    }
                    "view_title_slider" -> {
                        itemDescription.visibility = View.GONE
                        itemSlider.visibility = View.VISIBLE
                        itemSwitch.visibility = View.GONE
                    }
                    "view_title_switch" -> {
                        itemDescription.visibility = View.GONE
                        itemSlider.visibility = View.GONE
                        itemSwitch.visibility = View.VISIBLE
                    }
                }
            } finally {
                recycle()
            }
        }
    }

    fun setTitleText(text: String) {
        itemTitle.text = text
    }

    fun setDescText(text: String) {
        itemDescription.text = text
    }

    override fun setOnClickListener(l: OnClickListener?) {
        if (itemSlider.visibility == View.VISIBLE) {
            itemSlider.setOnClickListener(l)
        }
        if (itemSwitch.visibility == View.VISIBLE) {
            itemSwitch.isChecked = !itemSwitch.isChecked
        }
    }

    private fun toggleSlider() {
        if (itemSlider.tag == "not_open") {
            itemSlider.tag = "open"
            itemSlider.setImageResource(R.drawable.ic_slideup)
        }
        else {
            itemSlider.tag = "not_open"
            itemSlider.setImageResource(R.drawable.ic_slidedown)
        }
    }
}