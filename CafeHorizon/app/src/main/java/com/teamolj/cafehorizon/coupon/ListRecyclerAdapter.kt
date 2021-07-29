package com.teamolj.cafehorizon.coupon

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentCouponHistoryBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemCouponBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemNewsBinding
import com.teamolj.cafehorizon.newsAndEvents.News
import com.teamolj.cafehorizon.newsAndEvents.NewsAdapter
import com.teamolj.cafehorizon.newsAndEvents.NewsDetailActivity

class ListRecyclerAdapter : RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder>() {
    internal var datas = mutableListOf<Coupons>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RecyclerItemCouponBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var coupons = datas.get(position)
        holder.setCoupons(coupons)
    }

    inner class ViewHolder(private var binding: RecyclerItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var coupons: Coupons

        init {
            binding.root.setOnClickListener {
                var intent = Intent(binding.root.context, CouponActivity::class.java).apply {
                    putExtra("object", coupons)
                }

                binding.root.context.startActivity(intent)
            }
        }

        internal fun setCoupons(n: Coupons) {
            this.coupons = n

            binding.type.text = coupons.type
            binding.status.text = coupons.status
            binding.date.text = coupons.date
        }
    }


}