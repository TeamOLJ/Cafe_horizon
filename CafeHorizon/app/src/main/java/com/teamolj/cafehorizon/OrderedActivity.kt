package com.teamolj.cafehorizon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.databinding.ActivityOrderedBinding

class OrderedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}