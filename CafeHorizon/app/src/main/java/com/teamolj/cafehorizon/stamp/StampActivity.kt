package com.teamolj.cafehorizon.stamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.coupon.CouponAdapter
import com.teamolj.cafehorizon.coupon.CouponHistoryFragment
import com.teamolj.cafehorizon.coupon.CouponListFragment
import com.teamolj.cafehorizon.databinding.ActivityStampBinding

class StampActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStampBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStampBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fragmentList = listOf(StampFragment(), StampHistoryFragment())
        val adapter = StampAdapter(this)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        adapter.fragmentList = fragmentList
        binding.viewPager.adapter = adapter

        val tabTitles = listOf<String>("스탬프","스탬프 히스토리")
        TabLayoutMediator(binding.tabLayout,binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}