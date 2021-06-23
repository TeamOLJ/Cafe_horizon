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

        var adapter = NewsAdapter()
        adapter.newsList = loadNews()
        binding.recyclerViewNews.adapter = adapter
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this.context)

        return view
    }

    //https://stackoverflow.com/questions/51594772/how-to-return-a-list-from-firestore-database-as-a-result-of-a-function-in-kotlin/59124705#59124705
    private fun loadNews(): MutableList<News> {
        val db = Firebase.firestore
        val data: MutableList<News> = mutableListOf()

        Log.d("test", "Before attaching the listener!")


        db.collection("News").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("test", "Inside onComplete function!")
                for (document in task.result!!) {
                    data.add(
                        News(
                            document.data["title"].toString(),
                            document.data["content"].toString(),
                            document.data["date"].toString()
                        )
                    )
                }
            }
        }
            .addOnFailureListener { exception ->
                Log.w("firebase", "Error getting documents.", exception)
            }
        Log.d("test", "After attaching the listener!")


        return data
    }
}