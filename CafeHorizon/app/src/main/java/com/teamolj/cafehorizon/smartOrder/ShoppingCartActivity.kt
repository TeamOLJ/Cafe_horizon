package com.teamolj.cafehorizon.smartOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.teamolj.cafehorizon.databinding.ActivityShoppingCartBinding

class ShoppingCartActivity : FragmentActivity() {
    private lateinit var binding: ActivityShoppingCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}