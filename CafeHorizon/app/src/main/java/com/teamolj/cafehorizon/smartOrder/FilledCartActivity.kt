package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        FilledCartActivity.binding = this.binding
        changeTotalPrice()

        var adapter = FilledCartAdapter(this)
        adapter.menuList = AppDatabase.getInstance(this).cartDao().getAllByCategory() as MutableList<MenuInfo>
        binding.recyclerViewCart.adapter = adapter
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)

        binding.btnOrder.setOnClickListener {
            val intent = Intent(this, PayOrderActivity::class.java)
            intent.putExtra("from", PayOrderActivity.ORDER_CART)
            startActivity(intent)
        }
    }

    companion object {
        lateinit var binding:ActivityFilledCartBinding

        fun changeTotalPrice() {
            binding.textCartTotalPrice.text = DecimalFormat("합계 ###,###원")
                .format(AppDatabase.getInstance(binding.root.context).cartDao().getTotalAmount())
        }
    }
}