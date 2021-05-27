package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.databinding.FragmentExistBinding

class ExistFragment : Fragment() {
    private var _binding: FragmentExistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExistBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }
}