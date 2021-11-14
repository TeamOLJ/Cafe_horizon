package com.teamolj.cafehorizon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityHowToDetailBinding

class HowToDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHowToDetailBinding

    private lateinit var viewPager: ViewPager2
    private val howToImageList = mutableListOf<String>()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        when (intent.getStringExtra("category")) {
            "stamp" -> {
                binding.textToolbar.text = resources.getString(R.string.how_to_stamp)
                setHowToImage("HowToStamp")
            }

            "coupon" -> {
                binding.textToolbar.text = resources.getString(R.string.how_to_coupon)
                setHowToImage("HowToCoupon")
            }

            "smartOrder" -> {
                binding.textToolbar.text = resources.getString(R.string.how_to_smart_order)
                setHowToImage("HowToSmartOrder")
            }

            "service" -> {
                binding.textToolbar.text = resources.getString(R.string.terms_service)
                setTerms("TermsService")
            }

            "privacyPolicy" -> {
                binding.textToolbar.text = resources.getString(R.string.terms_privacy_policy)
                setTerms("TermsPrivacyPolicy")
            }

            "marketing" -> {
                binding.textToolbar.text = resources.getString(R.string.terms_agree_to_use_marketing)
                setTerms("TermsMarketing")
            }

            "adInfo" -> {
                binding.textToolbar.text =
                    resources.getString(R.string.terms_agree_to_receive_ad_info)
                setTerms("TermsAdInfo")
            }
        }
    }

    private fun setHowToImage(tag: String) {
        binding.viewPagerHowToImage.visibility = View.VISIBLE
        binding.slideIndicator.visibility = View.VISIBLE
        binding.scrollViewTerms.visibility = View.GONE

        val docRef = db.collection("Operational").document(tag)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    for (ind in 1..(document.data?.size ?: 1)) {
                        howToImageList.add(document.data?.get("image$ind").toString())
                    }
                }

                viewPager = binding.viewPagerHowToImage
                viewPager.offscreenPageLimit = howToImageList.size
                viewPager.adapter = HowToImageAdapter(this)

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.slideIndicator.setCurrentSlide(position)
                    }
                })

                val child = viewPager.getChildAt(0)
                (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

                binding.slideIndicator.setSlideSize(howToImageList.size)
            }
    }

    private fun setTerms(tag: String) {
        binding.viewPagerHowToImage.visibility = View.GONE
        binding.slideIndicator.visibility = View.GONE
        binding.scrollViewTerms.visibility = View.VISIBLE

        val docRef = db.collection("Operational").document(tag)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.textTerms.text = document.data?.get("terms").toString().replace("\\\\n", "\n")
                }
            }
    }

    private inner class HowToImageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = howToImageList.size
        override fun createFragment(position: Int): Fragment =
            HowToImageFragment(howToImageList[position])
    }
}