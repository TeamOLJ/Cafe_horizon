package com.teamolj.cafehorizon.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.view_slide_indicator.view.*

class SlideIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_slide_indicator, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlideIndicatorView,
            0, 0
        ).apply {

            try {
            } finally {
                recycle()
            }
        }
    }

    fun setSlideSize(size: Int) {
        when (size) {
            1 ->  {
                indicator1.visibility = View.VISIBLE
                indicator2.visibility = View.GONE
                indicator3.visibility = View.GONE
            }
            2 -> {
                indicator1.visibility = View.VISIBLE
                indicator2.visibility = View.VISIBLE
                indicator3.visibility = View.GONE
            }
            3 -> {
                indicator1.visibility = View.VISIBLE
                indicator2.visibility = View.VISIBLE
                indicator3.visibility = View.VISIBLE
            }
        }
    }

    fun setCurrentSlide(position: Int) {
        when(position) {
            0 -> {
                indicator1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                indicator2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                indicator3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
            1 -> {
                indicator1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                indicator2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                indicator3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
            2 -> {
                indicator1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                indicator2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                indicator3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
            }
        }
    }
}