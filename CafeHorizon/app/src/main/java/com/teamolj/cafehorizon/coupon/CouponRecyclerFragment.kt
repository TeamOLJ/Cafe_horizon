package com.teamolj.cafehorizon.coupon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentCouponRecyclerBinding

class CouponRecyclerFragment(val position: Int) : Fragment() {
    private lateinit var binding: FragmentCouponRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCouponRecyclerBinding.inflate(inflater, container, false)

        binding.recyclerViewHistory.adapter = (activity as CouponActivity).getAdapter(position)
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }
}