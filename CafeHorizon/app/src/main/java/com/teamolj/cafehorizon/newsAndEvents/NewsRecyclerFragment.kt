package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.FragmentNewsRecyclerBinding

class NewsRecyclerFragment : Fragment() {
    private lateinit var binding: FragmentNewsRecyclerBinding

    private val newsList = mutableListOf<News>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsRecyclerBinding.inflate(inflater, container, false)

        val db = Firebase.firestore


        db.collection("News").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("test", "Inside onComplete function!")
                for (document in task.result!!) {
                    newsList.add(
                        News(
                            document.data["title"].toString(),
                            document.data["content"].toString(),
                            document.data["date"].toString()
                        )
                    )
                }

                var adapter = NewsAdapter()
                adapter.newsList = newsList
                binding.recyclerViewNews.adapter = adapter
                binding.recyclerViewNews.layoutManager = LinearLayoutManager(this.context)
            }
        }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.", exception)
            }

        return binding.root
    }
}
