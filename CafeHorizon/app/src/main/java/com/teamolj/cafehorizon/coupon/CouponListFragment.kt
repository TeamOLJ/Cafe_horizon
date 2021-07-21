package com.teamolj.cafehorizon.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentCouponListBinding



class CouponListFragment : Fragment() {
        private var _binding: FragmentCouponListBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentCouponListBinding.inflate(inflater, container, false)
            val view = binding.root

            val data: MutableList<Coupons> = loadCoupons()

            var adapter = ListRecyclerAdapter()
            adapter.datas = data
            binding.recyclerViewCList.adapter = adapter
            binding.recyclerViewCList.layoutManager = LinearLayoutManager(this.context)

            return view
        }

        internal fun loadCoupons() : MutableList<Coupons> {

            val data: MutableList<Coupons> = mutableListOf()

            for (no in 0..10) {
                val type = "스탬프 무료 적립 쿠폰"
                val date = System.currentTimeMillis().toString()
                val status = "현재 쿠폰이라 삭제예정"

                var coupons = Coupons(type, date, status)
                data.add(coupons)
            }

            return data

        }
    }