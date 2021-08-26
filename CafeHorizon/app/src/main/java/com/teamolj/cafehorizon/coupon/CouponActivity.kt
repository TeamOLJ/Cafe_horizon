package com.teamolj.cafehorizon.coupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCouponBinding

class CouponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponBinding
    private lateinit var viewPager: ViewPager2
    private val ITEMS_NUM: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCouponBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        viewPager = binding.viewPager
        viewPager.offscreenPageLimit = ITEMS_NUM
        viewPager.adapter = CouponAdapter(this)

         val tabLayout = binding.tabLayout
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.title_coupon_list)
                        1 ->tab.text = getString(R.string.title_coupon_history)
            }
        }.attach()
    }

    private inner class CouponAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = ITEMS_NUM

        override fun createFragment(position: Int): Fragment {
            return CouponRecyclerFragment(position)
        }
    }
}