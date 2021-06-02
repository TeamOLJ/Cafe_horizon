package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayout
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySmartOrderBinding

class SmartOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmartOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_filled_Cart -> {
                    var intent = Intent(this, FilledCartActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_empty_Cart -> {
                    var intent = Intent(this, EmptyCartActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        val tabLayout = binding.tabLayout
        val recyclerView = binding.recyclerviewMenu
    }
}