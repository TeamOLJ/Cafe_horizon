package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import android.widget.Toast
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityMenuDetailBinding

//SmartOrderActivity를 상속받아 동일한 상태의 액션바를 보이도록 함.
class MenuDetailActivity : SmartOrderActivity() {
    private lateinit var binding: ActivityMenuDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.textMenuName.text = intent.getStringExtra("name").toString()
        binding.textMenuOrglPrice.text = intent.getStringExtra("price").toString()
        binding.textMenuDesc.text = resources.getString(R.string.sample_text)

        //https://calvinjmkim.tistory.com/50    나중에 Glide로 이미지 동그랗게 잡아주기

        binding.btnAddCart.setOnClickListener {
            Toast.makeText(this, "장바구니 담기!", Toast.LENGTH_SHORT).show()
            isCartFilled = !(isCartFilled)
            invalidateOptionsMenu()
        }
    }

}