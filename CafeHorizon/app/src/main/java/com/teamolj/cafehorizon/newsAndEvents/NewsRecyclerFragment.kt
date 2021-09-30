package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentNewsRecyclerBinding

class NewsRecyclerFragment : Fragment() {
    private lateinit var binding: FragmentNewsRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsRecyclerBinding.inflate(inflater, container, false)

        binding.recyclerViewNews.adapter = (activity as NewsAndEventsActivity).getNewsAdp()
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }
}
