package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.PayOrderActivity
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCafeMenuDetailBinding
import java.text.DecimalFormat

class CafeMenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityCafeMenuDetailBinding

    companion object{
        var price:Int = 0
    }


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

        //Firebase에서 불러오기
        binding.textCafeMenuName.text = intent.getStringExtra("name").toString()
        binding.textCafeMenuOrgPrice.text = intent.getIntExtra("price", 0).toString()
        binding.textCafeMenuDesc.text = resources.getString(R.string.sample_text)

        binding.textCafeMenuTotalPrice.text = DecimalFormat("총 ###,###원")
            .format(
                binding.customViewAmount.getAmountValue() * intent.getIntExtra("price", 0)
            )

        Glide.with(this).load(R.drawable.coffee_image).circleCrop().into(binding.imageCafeMenu)

        binding.customViewAmount.setOnClickListener {
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

    fun getInfo(): Cart {
        val name = binding.textCafeMenuName.text.toString()
        val type = 0
        val amount = binding.customViewAmount.getAmountValue()
        val cafeMenuTotalPrice =
            (binding.textCafeMenuTotalPrice.text).replace("\\D".toRegex(), "").toInt()
        val optionShot = 0
        val optionSyrup = 0
        val optionWhipping = 0

        when (type) {   //not defined exactly
            0 -> {    //coffee
            }

            1 -> {  //beverage
                optionShot
            }

            //2 = else = values are not changed.
        }

        return Cart(
            name,
            type,
            optionShot,
            optionSyrup,
            optionWhipping,
            cafeMenuTotalPrice / amount,
            amount
        )
    }

}