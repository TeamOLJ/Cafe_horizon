package com.teamolj.cafehorizon.coupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityCouponBinding

class CouponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponBinding
    private val ITEMS_NUM: Int = 2

    lateinit var db: FirebaseFirestore
    lateinit var adapter:CouponRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCouponBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val db = Firebase.firestore
        val userUid = Firebase.auth.currentUser!!.uid
        val docRef = db.collection("UserInformation").document(userUid).collection("Coupons")

        docRef.get().addOnSuccessListener { documents ->
            val couponList: Array<MutableList<Coupon>> = Array(2) { mutableListOf() }
            adapter = CouponRecyclerAdapter()

            for (document in documents) {
                val isUsed = document.data["isUsed"] as Boolean
                val expiryDate = (document.data["expiryDate"] as Timestamp).seconds * 1000

                if (!isUsed && System.currentTimeMillis() < expiryDate) {   //사용 가능 쿠폰
                    couponList[0].add(
                        Coupon(
                            document.id,
                            document.data["couponName"].toString(),
                            expiryDate,
                            document.data["discount"].toString().toInt(),
                            isUsed
                        )
                    )
                } else {
                    //사용되거나 만료된 쿠폰
                    couponList[1].add(
                        Coupon(
                            document.id,
                            document.data["couponName"].toString(),
                            expiryDate,
                            document.data["discount"].toString().toInt(),
                            isUsed,
                            (document.data["usedDate"] as Timestamp).seconds * 1000
                        )
                    )
                }
            }

            adapter.couponList = couponList
            val viewPager = binding.viewPager
            viewPager.offscreenPageLimit = ITEMS_NUM
            viewPager.adapter = CouponAdapter(this)

            TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.title_coupon_list)
                    1 -> tab.text = getString(R.string.title_coupon_history)
                }
            }.attach()

            binding.progressBar.visibility = View.GONE

        }.addOnFailureListener { exception ->

            binding.progressBar.visibility = View.GONE
            Log.w("firebase", "Error getting documents.", exception)
            Toast.makeText(binding.root.context,
                getString(R.string.toast_error_occurred),
                Toast.LENGTH_SHORT).show()
        }


    }

    fun getAdapter(position:Int):CouponRecyclerAdapter {
        adapter.setCategory(position)
        return adapter
    }

    private inner class CouponAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = ITEMS_NUM
        override fun createFragment(position: Int): Fragment = CouponRecyclerFragment(position)
    }
}