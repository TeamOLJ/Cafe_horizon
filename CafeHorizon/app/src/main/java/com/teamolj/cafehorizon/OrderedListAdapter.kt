package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemOrderedListBinding
import com.teamolj.cafehorizon.smartOrder.Cart

class OrderedListAdapter : RecyclerView.Adapter<OrderedListAdapter.listHolder>() {
    var orderedList = mutableListOf<Order>()

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
        val order = orderedList.get(position)
        holder.setList(order)
    }

    override fun getItemCount(): Int = orderedList.size

    inner class listHolder(private var binding: RecyclerItemOrderedListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setList(order: Order) {
            binding.textOrderCafeMenu.text = order.orderTime
            binding.textOrderCafeMenu.text = order.orderMenu.toString()
            binding.textState.text = order.state

            if(!order.getStateBoolean()) {
                binding.textState.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.colorAccent))
            }
        }
    }
}