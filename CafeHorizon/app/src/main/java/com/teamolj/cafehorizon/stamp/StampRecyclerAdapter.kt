package com.teamolj.cafehorizon.stamp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemStampBinding

class StampRecyclerAdapter : RecyclerView.Adapter<StampRecyclerAdapter.ViewHolder>() {
    internal var datas = mutableListOf<Stamps>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RecyclerItemStampBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var stamps = datas.get(position)
        holder.setCoupons(stamps)
    }

    inner class ViewHolder(private var binding: RecyclerItemStampBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var stamps: Stamps

        init {
            binding.root.setOnClickListener {
                var intent = Intent(binding.root.context, StampActivity::class.java).apply {
                    putExtra("object", stamps)
                }

                binding.root.context.startActivity(intent)
            }
        }

        internal fun setCoupons(n: Stamps) {
            this.stamps = n

            binding.type.text = stamps.type
            binding.date.text = stamps.date
            binding.validity.text = stamps.validity
        }
    }


}