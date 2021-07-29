package com.teamolj.cafehorizon.coupon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.FragmentCouponRecyclerBinding

class CouponRecyclerFragment() : Fragment() {
    private lateinit var binding: FragmentCouponRecyclerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponRecyclerBinding.inflate(inflater, container, false)

        val db = Firebase.firestore
        val userUid = Firebase.auth.currentUser!!.uid
        val docRef = db.collection("UserInformation").document(userUid).collection("Coupons")

        val couponList = mutableListOf<Coupon>()

        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val isUsed = document.data["isUsed"] as Boolean
                val expiryDate: Int = document.data["expiryDate"].toString().toInt()

                Log.d(
                    "TAG",
                    "${document.data["couponName"]} ${document.data["discount"]} $expiryDate $isUsed"
                )

                if (!isUsed && System.currentTimeMillis() < expiryDate) {
                    couponList.add(
                        Coupon(
                            document.data["couponName"].toString(),
                            expiryDate,
                            document.data["discount"].toString().toInt(),
                            isUsed,
                            null
                        )
                    )
                }
            }

            var adapter = CouponRecyclerAdapter()
            adapter.couponList = couponList
            binding.recyclerViewCoupon.adapter = adapter
            binding.recyclerViewCoupon.layoutManager = LinearLayoutManager(this.context)
        }.addOnFailureListener { exception ->
            Log.w("firebase", "Error getting documents.", exception)
        }


        return binding.root
    }
}