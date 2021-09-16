package com.teamolj.cafehorizon.coupon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.FragmentCouponRecyclerBinding

class CouponRecyclerFragment(val category: Int) : Fragment() {
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
                val expiryDate = (document.data["expiryDate"] as Timestamp).seconds * 1000

                when (category) {
                    0 -> {  //Coupon List
                        if (!isUsed && System.currentTimeMillis() < expiryDate) {
                            couponList.add(
                                Coupon(
                                    document.id,
                                    document.data["couponName"].toString(),
                                    expiryDate,
                                    document.data["discount"].toString().toInt(),
                                    isUsed
                                )
                            )
                        }
                    }

                    1 -> {  //Coupon History
                        if (isUsed) {
                            couponList.add(
                                Coupon(
                                    document.id,
                                    document.data["couponName"].toString(),
                                    expiryDate,
                                    document.data["discount"].toString().toInt(),
                                    isUsed,
                                    (document.data["usedDate"] as Timestamp).seconds * 1000
                                )
                            )
                        } else if (System.currentTimeMillis() > expiryDate) {
                            couponList.add(
                                Coupon(
                                    document.id,
                                    document.data["couponName"].toString(),
                                    expiryDate,
                                    document.data["discount"].toString().toInt(),
                                    isUsed
                                )
                            )
                        }
                    }
                }
            }

            var adapter = CouponRecyclerAdapter(category)
            adapter.couponList = couponList
            binding.recyclerViewHistory.adapter = adapter
            binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this.context)
        }.addOnFailureListener { exception ->
            Log.w("firebase", "Error getting documents.", exception)
            Toast.makeText(binding.root.context, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}