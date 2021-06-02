package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.databinding.FragmentMenuRecyclerBinding

class MenuRecyclerFragment : Fragment() {
    private var _binding: FragmentMenuRecyclerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMenuRecyclerBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }
}