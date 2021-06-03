package com.teamolj.cafehorizon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.databinding.ActivityOrderedStatusBinding

class OrderedStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderedStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderedStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}