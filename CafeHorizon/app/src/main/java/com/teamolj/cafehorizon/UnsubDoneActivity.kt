package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.databinding.ActivityUnsubDoneBinding

class UnsubDoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUnsubDoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnsubDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setBackgroundColor(255)

        binding.btnClose.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}