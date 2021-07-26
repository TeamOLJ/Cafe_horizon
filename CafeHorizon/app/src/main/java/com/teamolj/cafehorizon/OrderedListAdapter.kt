package com.teamolj.cafehorizon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemOrderedListBinding
import com.teamolj.cafehorizon.smartOrder.Cart

class OrderedListAdapter : RecyclerView.Adapter<OrderedListAdapter.listHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderedListAdapter.listHolder {

        return listHolder(
            RecyclerItemOrderedListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderedListAdapter.listHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class listHolder(private var binding: RecyclerItemOrderedListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setList() {

        }
    }
}