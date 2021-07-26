package com.teamolj.cafehorizon.views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.ChipGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ViewCafeMenuOptionBinding

class CafeMenuOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewCafeMenuOptionBinding =
        ViewCafeMenuOptionBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CafeMenuOptionView,
            0, 0
        ).apply {
            try {

                val backgroundColor = getColor(
                    R.styleable.CafeMenuOptionView_viewCafeMenuOptionBgColor,
                    resources.getColor(R.color.lightgray)
                )

                val optionTitle =
                    getString(R.styleable.CafeMenuOptionView_viewCafeMenuOptionTitleText)
                val defaultAmount =
                    getInt(
                        R.styleable.CafeMenuOptionView_viewCafeMenuOptionDefaultAmount,
                        0
                    ).toString()

                val itemType = getString(R.styleable.CafeMenuOptionView_viewCafeMenuOptionItemType)

                binding.container.setBackgroundColor(backgroundColor)
                binding.textOptionTitle.text = optionTitle
                binding.textAmount.text = defaultAmount
                setItemType(itemType)


                binding.imageBtnMinus.setOnClickListener {
                    if (getAmountValue() > 0) {
                        binding.textAmount.text = (getAmountValue() - 1).toString()
                    }
                }

                binding.imageBtnPlus.setOnClickListener {
                    binding.textAmount.text = (getAmountValue() + 1).toString()
                }

            } finally {
                recycle()
            }
        }
    }

    fun setItemType(itemType: String?) {
        when (itemType) {
            "view_amount_buttons" -> {
                binding.layoutAmount.visibility = View.VISIBLE
                binding.chipGroupOption.visibility = View.GONE
            }

            "view_chip_group" -> {
                binding.layoutAmount.visibility = View.GONE
                binding.chipGroupOption.visibility = View.VISIBLE
            }

            "view_titleOnly" -> {
                binding.layoutAmount.visibility = View.GONE
                binding.chipGroupOption.visibility = View.GONE
            }
        }
    }

    fun setTitleText(text: String) {
        binding.textOptionTitle.text = text
    }

    fun getAmountValue(): Int {
        return binding.textAmount.text.toString().toInt()
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.textAmount.addTextChangedListener(textWatcher)
    }

    fun setChipGroupListener(listener: ChipGroup.OnCheckedChangeListener) {
        binding.chipGroupOption.setOnCheckedChangeListener(listener)
    }
}