package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentEventsRecyclerBinding

class EventsRecyclerFragment : Fragment() {
    private lateinit var binding: FragmentEventsRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsRecyclerBinding.inflate(inflater, container, false)

        val data: MutableList<Events> = loadEvents()

        var adapter = EventAdapter()
        adapter.eventsList = data
        binding.recyclerViewEvents.adapter = adapter
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }

    internal fun loadEvents(): MutableList<Events> {
        //firebase 연결하기
        val data: MutableList<Events> = mutableListOf()

        for (no in 0..5) {
            val title = "${no}번째 이벤트"
            val uri = "android.resource://"+activity?.packageName+"/"+ R.drawable.coffee_image
            val period = "1900-01-01 ~ 2100-01-01"

            var events = Events(title, uri, period)
            data.add(events)
        }

        return data
    }
}