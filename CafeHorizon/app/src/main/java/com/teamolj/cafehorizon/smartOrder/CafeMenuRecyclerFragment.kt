package com.teamolj.cafehorizon.smartOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentCafeMenuRecyclerBinding

class CafeMenuRecyclerFragment(val position:Int) : Fragment() {
    private lateinit var binding: FragmentCafeMenuRecyclerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCafeMenuRecyclerBinding.inflate(inflater, container, false)

        val data:MutableList<cafeMenuInfo> = loadMenu()

        var adapter = CafeMenuRecyclerAdapter(position)
        adapter.cafeMenuList = data
        binding.recyclerViewCafeMenu.adapter = adapter
        binding.recyclerViewCafeMenu.layoutManager = LinearLayoutManager(this.context)

        return binding.root
    }

    private fun loadMenu():MutableList<cafeMenuInfo> {
        val data:MutableList<cafeMenuInfo> = mutableListOf()

        for(no in 0..6) {
            data.add(cafeMenuInfo("${no}샷 커피", 1000*(no+1)))
        }

        return data
    }
}

data class cafeMenuInfo(val name:String, val price:Int)