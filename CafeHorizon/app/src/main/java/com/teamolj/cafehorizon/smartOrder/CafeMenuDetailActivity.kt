package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.PayOrderActivity
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCafeMenuDetailBinding
import com.teamolj.cafehorizon.views.CafeMenuOptionView
import java.text.DecimalFormat

class CafeMenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityCafeMenuDetailBinding
    private lateinit var menuInfo: MenuInfo

    private val TITLE_ONLY = "view_titleOnly"
    private val AMOUNT_BUTTONS = "view_amount_buttons"
    private val CHIP_GROUP = "view_chip_group"

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

        menuInfo = intent.getParcelableExtra<MenuInfo>("info")!!

        binding.textCafeMenuName.text = menuInfo.name
        binding.textCafeMenuDesc.text = menuInfo.description
        binding.textCafeMenuOrgPrice.text = menuInfo.price.toString()
        Glide.with(this).load(menuInfo.imageUrl).circleCrop().into(binding.imageCafeMenu)

        changeTotalPrice()

        binding.customViewAmount.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                menuInfo.amount = p0.toString().toInt()
                changeTotalPrice()
            }
        })

        if (menuInfo.optionType > 0) createOptionDescView()
        if (menuInfo.optionType / 100 == 1) createOptionShotView()
        if (menuInfo.optionType % 100 / 10 == 1) createOptionSyrupView()
        if (menuInfo.optionType % 10 == 1) createOptionWhippingView()

        binding.btnAddCart.setOnClickListener {
            if (menuInfo.amount > 0) {
                dbApp.cartDao().insertOrUpdate(menuInfo)
                invalidateOptionsMenu()
                Toast.makeText(this, "메뉴를 장바구니에 담았습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "수량을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOrderNow.setOnClickListener {
            if (menuInfo.amount > 0) {
                val intent = Intent(this, PayOrderActivity::class.java)
                intent.putExtra("from", PayOrderActivity.ORDER_NOW)
                intent.putExtra("menuInfo", menuInfo)
                startActivity(intent)
            } else {
                Toast.makeText(this, "수량을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //추가옵션 타이틀 뷰 생성
    private fun createOptionDescView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(TITLE_ONLY)
            setTitleText("추가 옵션")
        }
        binding.layoutOption.addView(view)
    }

    //샷 옵션 뷰 생성
    private fun createOptionShotView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(AMOUNT_BUTTONS)
            setTitleText("샷 추가(500원)")
        }

        view.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val diff = p0.toString().toInt() - menuInfo.optionShot
                menuInfo.price += (diff * 500)
                menuInfo.optionShot += diff
                changeTotalPrice()
            }
        })

        binding.layoutOption.addView(view)
    }

    //시럽 옵션 뷰 생성
    private fun createOptionSyrupView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(AMOUNT_BUTTONS)
            setTitleText("시럽 추가(500원)")
        }

        view.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val diff = p0.toString().toInt() - menuInfo.optionSyrup
                menuInfo.price += (diff * 500)
                menuInfo.optionSyrup += diff
                changeTotalPrice()
            }
        })

        binding.layoutOption.addView(view)
    }

    //휘핑 옵션 뷰 생성
    private fun createOptionWhippingView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(CHIP_GROUP)
            setTitleText("휘핑")
            setChipGroupListener { _, checkedId ->
                when (checkedId) {
                    R.id.chipDefault -> {
                        menuInfo.optionWhipping = true
                    }
                    R.id.chipRemove -> {
                        menuInfo.optionWhipping = false
                    }
                }
            }
        }

        binding.layoutOption.addView(view)
    }

    fun changeTotalPrice() {
        binding.textCafeMenuTotalPrice.text =
            DecimalFormat("###,###원").format(menuInfo.amount * menuInfo.price)
    }

}