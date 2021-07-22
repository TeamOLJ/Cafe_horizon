package com.teamolj.cafehorizon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.teamolj.cafehorizon.databinding.ActivityPayOrderBinding
import com.teamolj.cafehorizon.smartOrder.AppDatabase
import com.teamolj.cafehorizon.smartOrder.Cart
import com.teamolj.cafehorizon.views.PayOrderItemView
import java.text.DecimalFormat

class PayOrderActivity : AppCompatActivity() {
    companion object {
        val ORDER_NOW = "order now"
        val ORDER_CART = "order cart"
    }

    private lateinit var binding: ActivityPayOrderBinding

    private val OPTION = "Option"
    private val MENU = "Menu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }


        val state = intent.getStringExtra("state")
        var cafeMenuList = mutableListOf<Cart>()
        var listTotalPrice:Int = 0

        if (state == ORDER_NOW) {
            cafeMenuList = mutableListOf(intent.getSerializableExtra("cafeMenu") as Cart)
        } else if (state == ORDER_CART) {
            cafeMenuList = AppDatabase.getInstance(this).cartDao().getAllByType().toMutableList()
        }

        for (menu in cafeMenuList) {
            listTotalPrice += (menu.eachPrice * menu.cafeMenuAmount)
            val priceExcludeOption = menu.eachPrice - (menu.optionShot*500) - (menu.optionSyrup*500)

            val menuView = PayOrderItemView(this)
            menuView.setCafeMenuInfo(menu.cafeMenuName, menu.cafeMenuAmount, priceExcludeOption)
            binding.layoutCafeMenu.addView(menuView)

            if (menu.optionShot > 0) {
                val shotView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("샷 추가", menu.optionShot)
                }
                binding.layoutCafeMenu.addView(shotView)
            }
            if (menu.optionSyrup > 0) {
                val syrupView = PayOrderItemView(this).apply{
                    setItemType(OPTION)
                    setOptionInfo("시럽 추가", menu.optionSyrup)
                }
                binding.layoutCafeMenu.addView(syrupView)
            }
            if (!menu.optionWhipping) {
                val whippingView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("휘핑 X", 0)
                }
                binding.layoutCafeMenu.addView(whippingView)
            }
        }

        binding.textTotalPrice.text = DecimalFormat("총 ###,###원").format(listTotalPrice)


        binding.groupEatOption.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnForHere.id -> {

                    }
                    binding.btnToGo.id -> {

                    }
                }
            }
        }
    }
}