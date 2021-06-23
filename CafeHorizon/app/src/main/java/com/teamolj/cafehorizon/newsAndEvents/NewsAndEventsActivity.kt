package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
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
    private val ITEMS_NUM: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsAndEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        viewPager = binding.viewPager
        viewPager.offscreenPageLimit = ITEMS_NUM
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
        override fun getItemCount(): Int = ITEMS_NUM

        override fun createFragment(position: Int): Fragment {
            if (position == 0) {
                return NewsRecyclerFragment()
            } else {
                return EventsRecyclerFragment()
            }
        }
    }
}