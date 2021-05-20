package com.teamolj.cafehorizon.views;

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.view_main_button.view.*

class MainButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_main_button, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MainButtonView,
            0, 0
        ).apply {

            try {
                val backgroundColor = getColor(R.styleable.MainButtonView_viewMainBtnBgColor, 0xFFFFFFFF.toInt())

                val titleColor = getColor(R.styleable.MainButtonView_viewMainBtnTextColor, 0xFF000000.toInt())
                val titleSize = getDimension(R.styleable.MainButtonView_viewMainBtnTextSize, 16F)

                backgroundLayout.setBackgroundColor(backgroundColor)

                viewBtnText.text = getString(R.styleable.MainButtonView_viewMainBtnText)
                viewBtnText.setTextColor(titleColor)
                viewBtnText.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)

                viewBtnImage.setImageDrawable(getDrawable(R.styleable.MainButtonView_viewMainBtnImage))

            } finally {
                recycle()
            }
        }
    }
}
