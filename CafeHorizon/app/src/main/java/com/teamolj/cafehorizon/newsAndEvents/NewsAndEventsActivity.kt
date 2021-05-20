package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.databinding.ActivityNewsAndEventsBinding

class NewsAndEventsActivity : FragmentActivity() {
    private lateinit var binding: ActivityNewsAndEventsBinding
    private lateinit var viewPager: ViewPager2

    var fragmentList = mutableListOf<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsAndEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentList = mutableListOf(NewsFragment(), EventsFragment())

        viewPager = binding.pager
        val pagerAdapter = NewsAndEventsAdapter(this)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "새소식"
                1 -> tab.text = "이벤트"
            }
        }.attach()
    }


    private inner class NewsAndEventsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList.get(position)

    }
}

