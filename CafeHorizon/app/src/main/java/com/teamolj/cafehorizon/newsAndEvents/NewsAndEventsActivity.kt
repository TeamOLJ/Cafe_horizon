package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityNewsAndEventsBinding

class NewsAndEventsActivity : FragmentActivity() {
    private lateinit var binding: ActivityNewsAndEventsBinding
    private val ITEMS_NUM: Int = 2

    private lateinit var eventsAdapter: EventsRecyclerAdapter
    private lateinit var newsAdapter: NewsRecyclerAdapter

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

        val db = Firebase.firestore

        db.collection("News").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                val newsList = mutableListOf<News>()

                for (document in documents) {
                    newsList.add(
                        News(
                            document.data["title"].toString(),
                            document.data["content"].toString(),
                            (document.data["date"] as Timestamp).seconds * 1000
                        )
                    )
                }

                newsAdapter = NewsRecyclerAdapter()
                newsAdapter.newsList = newsList

                db.collection("Events").orderBy("startDate", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener { documents ->
                        val eventsList = mutableListOf<Events>()

                        for (document in documents) {
                            eventsList.add(
                                Events(
                                    document.data["title"].toString(),
                                    document.data["contentUrl"].toString(),
                                    (document.data["startDate"] as Timestamp).seconds * 1000,
                                    (document.data["endDate"] as Timestamp).seconds * 1000
                                )
                            )
                        }

                        eventsAdapter = EventsRecyclerAdapter()
                        eventsAdapter.eventsList = eventsList

                        val viewPager = binding.viewPager
                        viewPager.offscreenPageLimit = ITEMS_NUM
                        val pagerAdapter = NewsAndEventsAdapter(this)
                        viewPager.adapter = pagerAdapter

                        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
                            when (position) {
                                0 -> tab.text = getString(R.string.title_news)
                                1 -> tab.text = getString(R.string.title_events)
                            }
                        }.attach()

                        binding.progressBar.visibility = View.GONE

                    }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Log.w("firebase", "Error getting documents.", it)
                    Toast.makeText(binding.root.context,
                        getString(R.string.toast_error_occurred),
                        Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Log.w("firebase", "Error getting documents.", it)
            Toast.makeText(binding.root.context,
                getString(R.string.toast_error_occurred),
                Toast.LENGTH_SHORT).show()
        }
    }

    fun getEventsAdp(): EventsRecyclerAdapter = eventsAdapter

    fun getNewsAdp(): NewsRecyclerAdapter = newsAdapter

    private inner class NewsAndEventsAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = ITEMS_NUM

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) NewsRecyclerFragment() else EventsRecyclerFragment()
        }
    }
}