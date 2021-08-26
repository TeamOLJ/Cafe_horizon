package com.teamolj.cafehorizon.coupon

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemCouponBinding
import java.text.SimpleDateFormat

class CouponRecyclerAdapter(val category:Int) : RecyclerView.Adapter<CouponRecyclerAdapter.historyAdapter>() {
    var couponList = mutableListOf<Coupon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponRecyclerAdapter.historyAdapter {
        return historyAdapter(
            RecyclerItemCouponBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: historyAdapter, position: Int) {
        var coupon = couponList.get(position)
        when(category) {
            0-> holder.setCouponList(coupon)
            1-> holder.setCouponHistory(coupon)
        }
    }

    override fun getItemCount(): Int = couponList.size


    inner class historyAdapter(private var binding: RecyclerItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setCouponList(coupon: Coupon) {
            binding.textCouponState.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.colorAccent))
            binding.textCouponState.text = SimpleDateFormat("'D'-D").format(coupon.expiryDate-System.currentTimeMillis())
            binding.textCouponName.text = coupon.couponName
            binding.textCouponDate.text = SimpleDateFormat("사용기한 : yyyy-MM-dd hh:mm").format(coupon.expiryDate)
        }

        fun setCouponHistory(coupon: Coupon) {
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