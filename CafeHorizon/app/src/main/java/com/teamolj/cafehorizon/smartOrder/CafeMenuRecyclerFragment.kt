package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentCafeMenuRecyclerBinding

class CafeMenuRecyclerFragment(val position: Int) : Fragment() {
    private lateinit var binding: FragmentCafeMenuRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCafeMenuRecyclerBinding.inflate(inflater, container, false)

        binding.recyclerViewCafeMenu.adapter = (activity as SmartOrderActivity).getAdapter(position)
        binding.recyclerViewCafeMenu.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }
}