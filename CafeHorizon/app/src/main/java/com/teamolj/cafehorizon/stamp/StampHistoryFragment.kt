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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStampHistoryBinding.inflate(inflater, container, false)

        if ((activity as StampActivity).checkIsListEmpty()) {
            binding.txtEmptyList.visibility = View.VISIBLE
            binding.recyclerViewSHistory.visibility = View.GONE
        }
        else {
            binding.txtEmptyList.visibility = View.GONE
            binding.recyclerViewSHistory.visibility = View.VISIBLE

            binding.recyclerViewSHistory.adapter = (activity as StampActivity).getRecyclerAdapter()
            binding.recyclerViewSHistory.layoutManager = LinearLayoutManager(this.context)
        }

        return binding.root
    }
}