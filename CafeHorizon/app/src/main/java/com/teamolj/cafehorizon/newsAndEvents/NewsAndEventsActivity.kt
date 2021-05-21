package com.teamolj.cafehorizon.newsAndEvents

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityNewsAndEventsBinding

class NewsAndEventsActivity : FragmentActivity() {
    private lateinit var binding: ActivityNewsAndEventsBinding
    private lateinit var viewPager: ViewPager2
    private val NUM_ITEMS: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsAndEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        viewPager.offscreenPageLimit = NUM_ITEMS
        val pagerAdapter = NewsAndEventsAdapter(this)
        viewPager.adapter = pagerAdapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.title_news)
                1 -> tab.text = getString(R.string.title_events)
            }
        }.attach()
    }


    private inner class NewsAndEventsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_ITEMS

        override fun createFragment(position: Int): Fragment {
            if (position == 0) {
                return NewsFragment()
            } else {
                return EventsFragment()
            }
        }
    }
}