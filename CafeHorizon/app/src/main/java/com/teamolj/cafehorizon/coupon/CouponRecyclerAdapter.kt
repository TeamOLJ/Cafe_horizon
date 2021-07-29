package com.teamolj.cafehorizon.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemCouponBinding
import java.text.SimpleDateFormat

class CouponRecyclerAdapter : RecyclerView.Adapter<CouponRecyclerAdapter.couponHolder>() {
    var couponList = mutableListOf<Coupon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): couponHolder {
        return couponHolder(
            RecyclerItemCouponBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: couponHolder, position: Int) {
        var coupon = couponList.get(position)
        holder.setCoupon(coupon)
    }

    override fun getItemCount(): Int = couponList.size


    inner class couponHolder(private var binding: RecyclerItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setCoupon(coupon: Coupon) {
            binding.textCouponName.text = coupon.couponName
            binding.textCouponExpiryDate.text =
                SimpleDateFormat("사용기한 : yyyy-MM-dd hh:mm").format(coupon.expiryDate)
        }
    }
}