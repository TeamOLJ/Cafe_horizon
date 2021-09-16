package com.teamolj.cafehorizon

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemOrderedListBinding
import java.text.SimpleDateFormat
import java.util.*

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
            binding.container.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("order", order)

                val intent = Intent(binding.root.context, OrderStateActivity::class.java).apply {
                    putExtra("bundle", bundle)
                    putExtra("from", "OrderedListActivity")
                }

                binding.root.context.startActivity(intent)
            }

            binding.textOrderTime.text = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREAN).format(order.orderTime)
            binding.textOrderTitle.text = order.orderTitle
            binding.textState.text = order.state

            if(!order.isPickedup()) {
                binding.textState.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.colorAccent))
            }
        }
    }
}