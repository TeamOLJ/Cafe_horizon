package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCafeMenuDetailBinding
import java.text.DecimalFormat

class CafeMenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityCafeMenuDetailBinding

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

        //https://calvinjmkim.tistory.com/50    나중에 Glide로 이미지 동그랗게 잡아주기

        binding.btnAddCart.setOnClickListener {
            val testCart = getInfo()
            Log.d("test", testCart.toString())
//            db.cartDao().insertOrUpdate(testCart)
            invalidateOptionsMenu()
            Toast.makeText(this, "메뉴를 장바구니에 담았습니다!", Toast.LENGTH_SHORT).show()
        }
        binding.btnOrderNow.setOnClickListener {
            //주문하기 페이지에 현재 메뉴의 Cart 생성해서 보내기
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