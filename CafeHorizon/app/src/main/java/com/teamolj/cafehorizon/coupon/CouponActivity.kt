package com.teamolj.cafehorizon.coupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.databinding.ActivityCouponBinding

class CouponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponBinding
    private lateinit var viewPager: ViewPager2
    private val ITEMS_NUM: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCouponBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewPager = binding.viewPager
        viewPager.offscreenPageLimit = ITEMS_NUM
        viewPager.adapter = CouponAdapter(this)

        val tabLaout = binding.tabLayout
        TabLayoutMediator(tabLaout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "보유 쿠폰"
                1 -> tab.text = "쿠폰 히스토리 "
            }
        }.attach()
    }

    private inner class CouponAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = ITEMS_NUM

        override fun createFragment(position: Int): Fragment {
            if (position == 0) {
                return CouponRecyclerFragment()
            } else {
                return HistoryRecyclerFragment()
            }
        }
    }
}