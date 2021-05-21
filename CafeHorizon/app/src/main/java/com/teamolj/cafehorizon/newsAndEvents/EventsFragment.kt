package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.teamolj.cafehorizon.R
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

        childFragmentManager.beginTransaction().replace(binding.containerEvent.id, EventsRecyclerFragment()).commit()

        return view
    }
}