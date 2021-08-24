package com.teamolj.cafehorizon.stamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemStampBinding
import java.text.SimpleDateFormat
import java.util.*

class StampRecyclerAdapter : RecyclerView.Adapter<StampHolder>() {
    var listData = mutableListOf<Stamp>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampHolder {
        val binding = RecyclerItemStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StampHolder(binding)
    }

    override fun onBindViewHolder(holder: StampHolder, position: Int) {
        val stamp = listData.get(position)
        holder.setStampLog(stamp)
    }

    override fun getItemCount(): Int = listData.size
}

class StampHolder(private val binding: RecyclerItemStampBinding): RecyclerView.ViewHolder(binding.root) {
    fun setStampLog(stamp: Stamp) {
        val dateTime = stamp.earnedDate.toDate()

        binding.txtInCircle.text = String.format("+%d", stamp.stampSize)
        binding.txtMainTitle.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(dateTime)
        binding.txtSubTitle.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime)
    }
}