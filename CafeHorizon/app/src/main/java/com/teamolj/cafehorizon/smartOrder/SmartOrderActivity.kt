package com.teamolj.cafehorizon.smartOrder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySmartOrderBinding

open class SmartOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmartOrderBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var MENU_ITEMS: Array<String>

    companion object {
        var isCartFilled = false    //Room에서 데이터 사이즈 얻기
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MENU_ITEMS = resources.getStringArray(R.array.array_menu)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewPager = binding.viewPager
        viewPager.offscreenPageLimit = MENU_ITEMS.size
        val pagerAdapter = SmartOrderAdapter(this)
        viewPager.adapter = pagerAdapter


        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = MENU_ITEMS[position]
        }.attach()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filled_cart -> {
                var intent = Intent(this, FilledCartActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_empty_cart -> {
                var intent = Intent(this, EmptyCartActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("toolbar", "<-- onCreateOptionsMenu : ${isCartFilled}")
        menuInflater.inflate(R.menu.menu_smartorder, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        Log.d("toolbar", "onPrepareOptionsMenu : ${isCartFilled} -->")
        updateToolbarMenu(menu!!)
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onResume() {   //MenuDetailActivitiy에서 돌아올 때 액션바 새로고침
        invalidateOptionsMenu()
        super.onResume()
    }


    fun updateToolbarMenu(menu: Menu) {
        if (isCartFilled) {
            menu.findItem(R.id.action_filled_cart).setVisible(true)
            menu.findItem(R.id.action_empty_cart).setVisible(false)
        } else {
            menu.findItem(R.id.action_filled_cart).setVisible(false)
            menu.findItem(R.id.action_empty_cart).setVisible(true)
        }
    }


    private inner class SmartOrderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = MENU_ITEMS.size

        override fun createFragment(position: Int): Fragment {
            return MenuRecyclerFragment()
        }
    }
}