package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.teamolj.cafehorizon.databinding.ActivityOrderStateBinding
import java.text.SimpleDateFormat

class OrderStateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderStateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.textWaitingOrder.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
        binding.textStateDesc.text = getString(R.string.desc_waiting_order)

        binding.textOrderTime.text = "주문일시: ${
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                intent.getIntExtra("orderTime", 0)
            )
        }"

        /*
        현재 상태에 따라 1. 이미지 바탕색 변경하기 2. 주문상태 텍스트 변경하기
        주문목록 데이터베이스에서 가져온 시간 정보를 텍스트에 붙이기
         */
    }
}