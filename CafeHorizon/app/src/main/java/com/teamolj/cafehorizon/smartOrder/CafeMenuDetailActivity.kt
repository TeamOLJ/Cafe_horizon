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
import com.teamolj.cafehorizon.databinding.ViewCafeMenuOptionBinding
import com.teamolj.cafehorizon.views.CafeMenuOptionView
import kotlinx.android.synthetic.main.view_cafe_menu_option.view.*
import java.text.DecimalFormat

class CafeMenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityCafeMenuDetailBinding
    private val cafeMenu = Cart()

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

        //"name" 기준으로 Firebase에서 불러오기
        with(cafeMenu) {
            cafeMenuName = intent.getStringExtra("name").toString()
            menuType = intent.getIntExtra("type", -1)
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

        when (cafeMenu.menuType) {
            0, 1 -> {
                createOptionDescView()
                createOptionShotView()
                createOptionSyrupView()
                createOptionWhippingView()
            }
            2, 3 -> {
                createOptionDescView()
                createOptionShotView()
            }
        }


        binding.btnAddCart.setOnClickListener {
            if(cafeMenu.cafeMenuAmount>0) {
                db.cartDao().insertOrUpdate(cafeMenu)
                invalidateOptionsMenu()
                Toast.makeText(this, "메뉴를 장바구니에 담았습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "수량을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOrderNow.setOnClickListener {
            if(cafeMenu.cafeMenuAmount>0) {
                val intent = Intent(this, PayOrderActivity::class.java)
                intent.putExtra("state", PayOrderActivity.ORDER_NOW)
                intent.putExtra("cafeMenu", cafeMenu)
                startActivity(intent)
            } else {
                Toast.makeText(this, "수량을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun createOptionDescView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(TITLE_ONLY)
            setTitleText("추가 옵션")
        }
        binding.layoutOption.addView(view)
    }

    fun createOptionShotView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(AMOUNT_BUTTONS)
            setTitleText("샷 추가(500원)")
        }

        view.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val diff = p0.toString().toInt() - cafeMenu.optionShot
                cafeMenu.eachPrice += (diff * 500)
                cafeMenu.optionShot += diff
                changeTotalPrice()
            }
        })

        binding.layoutOption.addView(view)
    }

    fun createOptionSyrupView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(AMOUNT_BUTTONS)
            setTitleText("시럽 추가(500원)")
        }

        view.setTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val diff = p0.toString().toInt() - cafeMenu.optionSyrup
                cafeMenu.eachPrice += (diff * 500)
                cafeMenu.optionSyrup += diff
                changeTotalPrice()
            }
        })

        binding.layoutOption.addView(view)
    }

    fun createOptionWhippingView() {
        val view = CafeMenuOptionView(this).apply {
            setItemType(CHIP_GROUP)
            setTitleText("휘핑")
        }

        view.chipGroupOption.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.chipDefault -> {
                    cafeMenu.optionWhipping = true
                }
                R.id.chipRemove -> {
                    cafeMenu.optionWhipping = false
                }
            }
        }

        binding.layoutOption.addView(view)
    }


    fun changeTotalPrice() {
        binding.textCafeMenuTotalPrice.text =
            DecimalFormat("총 ###,###원").format(cafeMenu.cafeMenuAmount * cafeMenu.eachPrice)
    }

}