package com.teamolj.cafehorizon.smartOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityEmptyCartBinding

class EmptyCartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEmptyCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMenu.setOnClickListener {
            finish()
        }
    }
}