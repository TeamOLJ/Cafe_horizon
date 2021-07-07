package com.teamolj.cafehorizon

import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityHowToDetailBinding

class HowToDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHowToDetailBinding
    private val CHAPTER_SIZE = 18F
    private val ARTICLE_SIZE = 16F
    private val DESC_SIZE = 14F

    private lateinit var layoutTerms: LinearLayout
    private lateinit var viewPager: ViewPager2
    private val howToImageList = mutableListOf<String>()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutTerms = binding.layoutTerms

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
                setTerms("service")
            }

            "privacyPolicy" -> {
                binding.textToolbar.text = resources.getString(R.string.terms_privacy_policy)
                setTerms("privacyPolicy")
            }

            "marketing" -> {
                binding.textToolbar.text =
                    resources.getString(R.string.terms_agree_to_use_marketing)
                setTerms("marketing")
            }

            "adInfo" -> {
                binding.textToolbar.text =
                    resources.getString(R.string.terms_agree_to_receive_ad_info)
                setTerms("adInfo")
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
                    for (ind in 1..(document.data?.size?:1)){
                        Log.d("TAG", "add image$ind")
                        howToImageList.add(document.data?.get("image$ind").toString())
                    }
                }

                viewPager = binding.viewPagerHowToImage
                viewPager.offscreenPageLimit = howToImageList.size
                viewPager.adapter = HowToImageAdapter(this)

                viewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.slideIndicator.setCurrentSlide(position)
                    }
                })

                binding.slideIndicator.setSlideSize(howToImageList.size)
            }
    }

    private fun setTerms(tag: String) {
        binding.viewPagerHowToImage.visibility = View.GONE
        binding.slideIndicator.visibility = View.GONE
        binding.scrollViewTerms.visibility = View.VISIBLE

        /*
        firebase 테이블명==tag로 접근해
        장-addTermsChapter
        조-addTermsArticle
        내용-addTermsDesc
        컬럼 값에 따라 뷰 추가하기
         */

    }


    private fun addTermsChapter(str: String) {
        val textView = TextView(this).apply {
            text = str
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, CHAPTER_SIZE)
            setPadding(0, 16, 0, 16)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeface = resources.getFont(R.font.bold)
            }
        }

        layoutTerms.addView(textView)
    }


    private fun addTermsArticle(str: String) {
        val textView = TextView(this).apply {
            text = str
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, ARTICLE_SIZE)
            setPadding(8, 8, 0, 8)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeface = resources.getFont(R.font.bold)
            }
        }

        layoutTerms.addView(textView)
    }


    private fun addTermsDesc(str: String) {
        val textView = TextView(this).apply {
            text = str
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, DESC_SIZE)
            setPadding(8, 0, 0, 0)
        }

        layoutTerms.addView(textView)
    }

    private inner class HowToImageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = howToImageList.size
        override fun createFragment(position: Int): Fragment =
            HowToImageFragment(howToImageList.get(position))
    }
}