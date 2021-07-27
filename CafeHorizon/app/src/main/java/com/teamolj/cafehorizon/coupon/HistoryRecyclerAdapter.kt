package com.teamolj.cafehorizon.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemCouponBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemHistoryBinding
import java.text.SimpleDateFormat

class HistoryRecyclerAdapter : RecyclerView.Adapter<HistoryRecyclerAdapter.historyAdapter>() {
    var couponList = mutableListOf<Coupon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecyclerAdapter.historyAdapter {
        return historyAdapter(
            RecyclerItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: historyAdapter, position: Int) {
        var coupon = couponList.get(position)
        holder.setCoupon(coupon)
    }

    override fun getItemCount(): Int = couponList.size


    inner class historyAdapter(private var binding: RecyclerItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setCoupon(coupon: Coupon) {
            binding.textCouponName.text = coupon.couponName

            if(coupon.isUsed) {
                binding.textCouponState.text = "사용완료"
                binding.textCouponDate.text =SimpleDateFormat("사용일자 : yyyy-MM-dd hh:mm").format(coupon.usedDate!!)
            } else {
                binding.textCouponState.text = "기간만료"
                binding.textCouponDate.text =SimpleDateFormat("만료일자 : yyyy-MM-dd").format(coupon.expiryDate)
            }
        }
    }
}