package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.PayOrderActivity
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCafeMenuDetailBinding
import java.text.DecimalFormat

class CafeMenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityCafeMenuDetailBinding
    private val cafeMenu = Cart()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        //"name" 기준으로 Firebase에서 불러오기
        with(cafeMenu) {
            cafeMenuName = intent.getStringExtra("name").toString()
            menuType = 0
            eachPrice = intent.getIntExtra("price", 0)
        }

        binding.textCafeMenuName.text = cafeMenu.cafeMenuName
        binding.textCafeMenuDesc.text = resources.getString(R.string.sample_text)
        binding.textCafeMenuOrgPrice.text = cafeMenu.eachPrice.toString()
        changeTotalPrice()


        Glide.with(this).load(R.drawable.coffee_image).circleCrop().into(binding.imageCafeMenu)

        binding.customViewAmount.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                cafeMenu.cafeMenuAmount = p0.toString().toInt()
                changeTotalPrice()
            }
        })

        when(cafeMenu.menuType) {
            0-> {
                //샷, 시럽, 휘핑 TextWatcher 추가, cafeMenu.eachPrice 에 금액 추가
            }
        }


        binding.btnAddCart.setOnClickListener {
            val testCart = getInfo()
            db.cartDao().insertOrUpdate(testCart)
            invalidateOptionsMenu()
            Toast.makeText(this, "메뉴를 장바구니에 담았습니다!", Toast.LENGTH_SHORT).show()
        }

        binding.btnOrderNow.setOnClickListener {
            val intent = Intent(this, PayOrderActivity::class.java)
            intent.putExtra("state", PayOrderActivity.ORDER_NOW)
            intent.putExtra("cafeMenu", getInfo())
            startActivity(intent)
        }

    }


    fun changeTotalPrice() {
        binding.textCafeMenuTotalPrice.text = DecimalFormat("총 ###,###원").format(cafeMenu.cafeMenuAmount * cafeMenu.eachPrice)
    }


    fun getInfo(): Cart {
        with(cafeMenu) {
            cafeMenuAmount = binding.customViewAmount.getAmountValue()

            when (menuType) {
                0 -> {  //coffee
                    optionShot
                    optionSyrup
                    optionWhipping
                }

                1 -> {  ///beverage
                    optionShot

                }

                else -> {
                }
            }
        }

        return cafeMenu
    }

}