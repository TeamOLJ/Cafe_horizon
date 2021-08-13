package com.teamolj.cafehorizon.stamp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentStampHistoryBinding

class StampHistoryFragment : Fragment() {
    private lateinit var binding: FragmentStampHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStampHistoryBinding.inflate(inflater, container, false)

        val data: MutableList<Stamps> = loadCoupons()

        var adapter = StampRecyclerAdapter()
        adapter.datas = data
        binding.recyclerViewSHistory.adapter = adapter
        binding.recyclerViewSHistory.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }

    internal fun loadCoupons() : MutableList<Stamps> {

        val data: MutableList<Stamps> = mutableListOf()

        for (no in 0..10) {
            val type = "+1"
            val date = "2021 / 12 / 12"
            val validity = "유효기간 : 2022/04/05"

            var stamps = Stamps(type, date, validity)
            data.add(stamps)
        }

        return data

    }
}