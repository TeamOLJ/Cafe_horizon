package com.teamolj.cafehorizon.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ViewPayOrderItemBinding
import java.text.DecimalFormat

class PayOrderItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding:ViewPayOrderItemBinding =
        ViewPayOrderItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PayOrderItemView,
            0, 0
        ).apply{
            try {
                val type = getString(R.styleable.PayOrderItemView_viewPayOrderItemType) ?: "Menu"

                setItemType(type)

            } finally{
                recycle()
            }
        }
    }

    fun setItemType(type:String) {
        when(type) {
            "Menu" -> {
                val paddingEight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics).toInt()
                binding.container.setPadding(0, paddingEight, 0, paddingEight)

                binding.textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                binding.textAmount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                binding.textPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
            }

            "Option" -> {
                val paddingFour = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, resources.displayMetrics).toInt()
                binding.container.setPadding(0, paddingFour, 0, paddingFour)

                binding.textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
                binding.textAmount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
                binding.textPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)


                val leftPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, resources.displayMetrics).toInt()
                binding.textName.setPadding(leftPadding, 0, 0, 0)
            }

            "Discount" -> {
                val paddingEight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics).toInt()
                binding.container.setPadding(0, paddingEight, 0, paddingEight)

                binding.textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                binding.textAmount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                binding.textPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
            }
        }
    }

    fun setOptionInfo(name:String, amount:Int) {
        binding.textName.text = "-$name"
        if(amount>0){
            binding.textAmount.text = amount.toString()
        } else {
            binding.textAmount.text = ""
        }
        binding.textPrice.text = ""
    }

    fun setCafeMenuInfo(name:String, amount:Int, eachPrice:Int) {
        binding.textName.text = name
        binding.textAmount.text = amount.toString()
        binding.textPrice.text = DecimalFormat("###,###").format(amount*eachPrice)
    }

    fun setDiscountInfo(name:String, price:Int) {
        binding.textName.text = name
        binding.textAmount.text = ""
        binding.textPrice.text = DecimalFormat("###,###").format(price)
    }

    fun getDiscountPrice():Int {
        return binding.textPrice.text.toString().toInt()
    }
}