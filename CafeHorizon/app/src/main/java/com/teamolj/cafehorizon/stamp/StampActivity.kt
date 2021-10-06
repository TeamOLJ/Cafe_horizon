package com.teamolj.cafehorizon.stamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityStampBinding

class StampActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStampBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var dbFetched = false
    private var stampCount = -1
    private var stampInform = ""
    private var rvAdapter = StampRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        db.collection("UserInformation").document(auth.currentUser!!.uid).collection("Stamps")
            .get()
            .addOnSuccessListener { result ->
                // 스탬프 개수 확인
                stampCount = result.size()

                // 내용물 어댑터에 저장
                for (document in result) {
                    rvAdapter.listData.add(document.toObject<Stamp>())
                }
                rvAdapter.listData.sortByDescending { stamp -> stamp.earnedDate }

                db.collection("Operational").document("StampInformation")
                    .get()
                    .addOnSuccessListener { document ->
                        dbFetched = true
                        binding.progressBar.visibility = View.GONE

                        stampInform = if (document.exists())
                            document.data?.get("informText").toString().replace("\\\\n", "\n")
                        else
                            getString(R.string.toast_error_occurred)

                        val stampPageAdapter = StampPageAdapter(this)
                        stampPageAdapter.addFragment(StampFragment())
                        stampPageAdapter.addFragment(StampHistoryFragment())
                        binding.containerStamp.offscreenPageLimit = 2
                        binding.containerStamp.adapter = stampPageAdapter

                        TabLayoutMediator(binding.tabLayoutStamp, binding.containerStamp) { tab, position ->
                            when (position) {
                                0 -> tab.text = getString(R.string.menu_stamp)
                                1 -> tab.text = getString(R.string.tab_stamp_history)
                            }
                        }.attach()
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT).show()
            }
    }

    fun getStampCount() : Int {
        return stampCount
    }

    fun getStampInformation() : String {
        return stampInform
    }

    fun getRecyclerAdapter() : StampRecyclerAdapter {
        return rvAdapter
    }

    private inner class StampPageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        var fragments : ArrayList<Fragment> = ArrayList()

        override fun getItemCount(): Int {
            return fragments.size
        }
        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
            notifyItemInserted(fragments.size-1)
        }
    }
}