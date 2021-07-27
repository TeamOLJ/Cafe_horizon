package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.coupon.CouponActivity
import com.teamolj.cafehorizon.databinding.ActivityMainBinding
import com.teamolj.cafehorizon.newsAndEvents.NewsAndEventsActivity
import com.teamolj.cafehorizon.notice.NoticeActivity
import com.teamolj.cafehorizon.smartOrder.SmartOrderActivity
import kotlinx.android.synthetic.main.header_navigation_drawer.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var doubleBackPressed: Boolean = false
    private lateinit var closeToast: Toast

    private var SLIDE_NUM_PAGES = 3
    private val mainImageList = mutableListOf<String>()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notice -> {
                    val intent = Intent(this, NoticeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // navitagionDrawer
        topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
            binding.navigationView.checkedItem?.isChecked = false
        }
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_stamp -> {
                    binding.drawerLayout.close()
//                    val intent = Intent(this, StampActivity::class.java)
//                    startActivity(intent)
                    Toast.makeText(this, "스탬프", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_coupon -> {
                    binding.drawerLayout.close()
                    val intent = Intent(this, CouponActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_smart_order -> {
                    binding.drawerLayout.close()
                    val intent = Intent(this, SmartOrderActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_ordered -> {
                    binding.drawerLayout.close()
                    val intent = Intent(this, OrderedListActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "주문내역", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_news_events -> {
                    binding.drawerLayout.close()
                    val intent = Intent(this, NewsAndEventsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_chats -> {
                    binding.drawerLayout.close()
                    // val intent = Intent(this, ChattingActivity::class.java)
                    // startActivity(intent)
                    Toast.makeText(this, "채팅문의", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_howto -> {
                    binding.drawerLayout.close()
                     val intent = Intent(this, HowToActivity::class.java)
                     startActivity(intent)
                    true
                }
                else -> false
            }
        }

        Glide.with(this)
            .load(R.drawable.img_main_loading_failed)
            .into(binding.imgDefault)

        val headerView = binding.navigationView.getHeaderView(0)

        // SharedPreference에서 가져온 사용자 닉네임 세팅
        binding.textUserNickname.text = "사용자닉네임"
        headerView.textUserNickname.text = "사용자닉네임"

        binding.btnBarcode.setOnClickListener {
            val intent = Intent(this, BarcodeActivity::class.java)
            startActivity(intent)
        }

         binding.btnStamp.setOnClickListener {
//             val intent = Intent(this, StampActivity::class.java)
//             startActivity(intent)
             Toast.makeText(this, "스탬프", Toast.LENGTH_SHORT).show()
         }

         binding.btnCoupon.setOnClickListener {
             val intent = Intent(this, CouponActivity::class.java)
             startActivity(intent)
         }

         binding.btnOrderMain.setOnClickListener {
             val intent = Intent(this, SmartOrderActivity::class.java)
             startActivity(intent)
         }

         binding.btnChatting.setOnClickListener {
        //     val intent = Intent(this, ChattingActivity::class.java)
        //     startActivity(intent)
             Toast.makeText(this, "채팅문의", Toast.LENGTH_SHORT).show()
         }

        headerView.btnMyInfo.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivityForResult(intent, 1111)
        }

        headerView.btnLogOut.setOnClickListener {
            // 로그아웃 처리

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // get images url from Firebase DB
        val docRef = db.collection("Operational").document("MainSlider")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    SLIDE_NUM_PAGES = document.data?.get("numOfPage").toString().toInt()
                    mainImageList.add(document.data?.get("firstImage").toString())
                    mainImageList.add(document.data?.get("secondImage").toString())
                    mainImageList.add(document.data?.get("thirdImage").toString())

                    // Main page image slider
                    val imageSlideAdapter = SlideImageAdapter(this)
                    binding.viewPagerMainImage.adapter = imageSlideAdapter
                    binding.viewPagerMainImage.offscreenPageLimit = SLIDE_NUM_PAGES

                    binding.viewPagerMainImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            binding.slideIndicator.setCurrentSlide(position)
                        }
                    })

                    binding.slideIndicator.setSlideSize(SLIDE_NUM_PAGES)

                } else {
                    binding.slideIndicator.visibility = View.GONE
                    binding.imgDefault.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                binding.slideIndicator.visibility = View.GONE
                binding.imgDefault.visibility = View.VISIBLE
            }
    }

    private inner class SlideImageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = SLIDE_NUM_PAGES
        override fun createFragment(position: Int): Fragment = MainImageFragment(mainImageList.get(position))
    }

    // BackPress twice to close the app
    override fun onBackPressed() {
        if (doubleBackPressed) {
            closeToast.cancel()
            super.onBackPressed()
            return
        }
        this.doubleBackPressed = true
        closeToast = Toast.makeText(this, getString(R.string.toast_backpress_close), Toast.LENGTH_SHORT)
        closeToast.show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackPressed = false }, 2000)
    }
}