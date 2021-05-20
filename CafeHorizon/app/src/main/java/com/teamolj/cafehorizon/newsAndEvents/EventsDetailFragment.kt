package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentEventsDetailBinding


class EventsDetailFragment : Fragment() {
    private var _binding: FragmentEventsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}