package com.teamolj.cafehorizon.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentStampBinding

class StampFragment : Fragment() {
    private lateinit var binding: FragmentStampBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStampBinding.inflate(inflater, container, false)

        return binding.root
    }

}