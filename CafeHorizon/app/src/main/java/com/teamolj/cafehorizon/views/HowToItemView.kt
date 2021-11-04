package com.teamolj.cafehorizon.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ViewHowToItemBinding

class HowToItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: ViewHowToItemBinding =
        ViewHowToItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.HowToItemView,
            0, 0
        ).apply {
            try {

                val backgroundColor = getColor(R.styleable.HowToItemView_viewHowToItemBgColor, 0)

                val itemText = getString(R.styleable.HowToItemView_viewHowToItemText)
                val titleColor = getColor(R.styleable.HowToItemView_viewHowToItemTextColor, 0xFF000000.toInt())
                val itemTextSize =
                    getDimension(R.styleable.HowToItemView_viewHowToItemTextSize, 16F)

                val itemPaddingLeft =
                    getDimension(R.styleable.HowToItemView_viewHowToItemPaddingLeft, 0F)
                val itemPaddingRight =
                    getDimension(R.styleable.HowToItemView_viewHowToItemPaddingRight, 0F)

                val isShowImage =
                    getBoolean(R.styleable.HowToItemView_viewHowToItemShowImage, false)

                if(backgroundColor == 0) {
                    binding.container.setBackgroundResource(R.drawable.recycler_item_border)
                } else {
                    binding.container.setBackgroundColor(backgroundColor)
                }

                binding.textItem.text = itemText
                binding.textItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize)
                binding.textItem.setTextColor(titleColor)

                binding.container.setPadding(itemPaddingLeft.toInt(), 0, itemPaddingRight.toInt(), 0)

                binding.imageRightArrow.visibility = if(isShowImage) View.VISIBLE else GONE

            } finally {
                recycle()
            }
        }
    }

    fun setText(text:String){
        binding.textItem.text = text
    }
}