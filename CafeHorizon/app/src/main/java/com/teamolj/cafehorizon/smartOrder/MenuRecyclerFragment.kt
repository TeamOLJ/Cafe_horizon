package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentMenuRecyclerBinding

class MenuRecyclerFragment : Fragment() {
    private var _binding: FragmentMenuRecyclerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMenuRecyclerBinding.inflate(inflater, container, false)
        val view = binding.root

        val data:MutableList<menuInfo> = loadMenu()

        var adapter = MenuRecyclerAdapter()
        adapter.menuList = data
        binding.recyclerViewMenu.adapter = adapter
        binding.recyclerViewMenu.layoutManager = LinearLayoutManager(this.context)

        return view
    }

    internal fun loadMenu():MutableList<menuInfo> {
        val data:MutableList<menuInfo> = mutableListOf()

        for(no in 0..6) {
            data.add(menuInfo("${no}샷 커피", 1000*(no+1)))
        }

        return data
    }
}

data class menuInfo(val name:String, val price:Int)