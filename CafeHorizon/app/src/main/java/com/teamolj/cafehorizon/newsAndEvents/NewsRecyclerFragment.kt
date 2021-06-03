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
    private var _binding: FragmentNewsRecyclerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsRecyclerBinding.inflate(inflater, container, false)
        val view = binding.root

        val data: MutableList<News> = loadNews()

        var adapter = NewsAdapter()
        adapter.newsList = data
        binding.recyclerViewNews.adapter = adapter
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this.context)

        return view
    }

    fun loadNews(): MutableList<News> { //데이터 가져와서 리스트에 넣고, 그 리스트를 어댑터 리스트에 넣고 업데이트 하는 것까지!
        val db = Firebase.firestore
        val data: MutableList<News> = mutableListOf()

        db.collection("News")
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    data.add(
                        News(
                            document.getString("title")!!,
                            document.getString("content")!!,
                            document.getString("date")!!
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.", exception)
            }

        return data
    }
}