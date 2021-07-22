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

    private var defaultSize = 1
    private var indicatorInactiveColor:Int = 0

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
                defaultSize = getInt(R.styleable.SlideIndicatorView_viewSlideIndicatorSize, 0)

                for(idx in 1..defaultSize) {
                    val indicatorDot = View(context)

                    val indicatorDotSize = getDimensionPixelSize(R.styleable.SlideIndicatorView_viewSlideIndicatorDotSize, 0)
                    val indicatorDotMargin = getDimensionPixelSize(R.styleable.SlideIndicatorView_viewSlideIndicatorMargin, 0)

                    indicatorInactiveColor = getColor(R.styleable.SlideIndicatorView_viewSlideIndicatorInactiveColor, resources.getColor(R.color.white))

                    val indicatorParams = LayoutParams(indicatorDotSize, indicatorDotSize)
                    indicatorParams.setMargins(indicatorDotMargin, 0, indicatorDotMargin, 0)
                    indicatorDot.layoutParams = indicatorParams
                    indicatorDot.setBackgroundResource(R.drawable.img_indicator)

                    layoutIndicator.addView(indicatorDot)
                }

            } finally {
                recycle()
            }
        }
    }

    fun setSlideSize(size: Int) {
        for(idx in 0 until size) {
            layoutIndicator.getChildAt(idx).visibility = View.VISIBLE
        }
        for(idx in size until defaultSize) {
            layoutIndicator.getChildAt(idx).visibility = View.GONE
        }
    }

    fun setCurrentSlide(position: Int) {
        for(idx in 0 until defaultSize) {
            layoutIndicator.getChildAt(idx).backgroundTintList = ColorStateList.valueOf(indicatorInactiveColor)
        }
        layoutIndicator.getChildAt(position).backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
    }
}