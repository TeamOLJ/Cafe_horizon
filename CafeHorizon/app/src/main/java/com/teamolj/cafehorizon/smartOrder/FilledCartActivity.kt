package com.teamolj.cafehorizon.smartOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityFilledCartBinding

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

        var adapter = FilledCartAdapter()
        adapter.cartList = loadCart()
        binding.recyclerViewCart.adapter = adapter
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
    }

    fun loadCart():MutableList<String> {
        val data = mutableListOf<String>()
        
        data.add("첫 번째 메뉴")
        data.add("두 번째 메뉴")
        data.add("세 번째 메뉴")
        
        return data
    }
}