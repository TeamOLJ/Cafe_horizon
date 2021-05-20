package com.teamolj.cafehorizon.newsAndEvents

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        val view = binding.root

        val data: MutableList<Events> = loadEvents()

        var adapter = EventAdapter()
        adapter.eventsList = data
        binding.eventsRecyclerView.adapter = adapter
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this.context)

        return view
    }


    internal fun loadEvents(): MutableList<Events> {
        val data: MutableList<Events> = mutableListOf()

        for (no in 0..5) {
            val title = "${no}번째 이벤트"
            val uri: Uri = Uri.parse("android.resource://com.teamolj.cafehorizon/drawable/ic_launcher_background")
            val period = "1900-01-01 ~ 2100-01-01"

            var events = Events(title, uri, period)
            data.add(events)
        }

        return data
    }

}