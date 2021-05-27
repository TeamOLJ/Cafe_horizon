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
    private lateinit var menuList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        menuList = resources.getStringArray(R.array.array_menu).toList()

        val tabLayout = binding.tabLayout
        val recyclerView = binding.recyclerviewMenu


    }
    

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_smartorder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean = when (item.itemId) {
        R.id.action_shoppingCart -> {
            var intent = Intent(this, ShoppingCartActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}