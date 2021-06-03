package com.teamolj.cafehorizon.coupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCouponBinding

class CouponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentList = listOf(CouponListFragment(),CouponHistoryFragment())
        val adapter = CouponAdapter(this)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        val tabTitles = listOf<String>("보유 쿠폰","쿠폰 히스토리")
        TabLayoutMediator(binding.tabLayout,binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}