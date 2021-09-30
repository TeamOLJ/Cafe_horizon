package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentEventsRecyclerBinding

class EventsRecyclerFragment : Fragment() {
    private lateinit var binding: FragmentEventsRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsRecyclerBinding.inflate(inflater, container, false)

        binding.recyclerViewEvents.adapter = (activity as NewsAndEventsActivity).getEventsAdp()
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }
}