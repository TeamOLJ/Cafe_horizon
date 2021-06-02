package com.teamolj.cafehorizon.smartOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityFilledCartBinding

class FilledCartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFilledCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilledCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}