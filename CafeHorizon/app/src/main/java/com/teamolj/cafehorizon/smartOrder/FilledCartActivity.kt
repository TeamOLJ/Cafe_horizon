package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.PayOrderActivity
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityFilledCartBinding
import java.text.DecimalFormat

class FilledCartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFilledCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilledCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.textCartTotalPrice.text = DecimalFormat("합계 ###,###원")
            .format(AppDatabase.getInstance(this).cartDao().getTotalAmount())

        var adapter = FilledCartAdapter()
        adapter.cartList = AppDatabase.getInstance(this).cartDao().getAllByType() as MutableList<Cart>
        binding.recyclerViewCart.adapter = adapter
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)

        binding.btnOrder.setOnClickListener {
            val intent = Intent(this, PayOrderActivity::class.java)
            intent.putExtra("state", PayOrderActivity.ORDER_CART)
//            intent.putExtra("cafeMenu", )
            startActivity(intent)
        }
    }
}