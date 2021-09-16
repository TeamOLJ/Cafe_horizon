package com.teamolj.cafehorizon

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.chat.ChatActivity
import com.teamolj.cafehorizon.coupon.CouponActivity
import com.teamolj.cafehorizon.databinding.ActivityMainBinding
import com.teamolj.cafehorizon.newsAndEvents.NewsAndEventsActivity
import com.teamolj.cafehorizon.notice.NoticeActivity
import com.teamolj.cafehorizon.operation.InternetConnection
import com.teamolj.cafehorizon.smartOrder.SmartOrderActivity
import com.teamolj.cafehorizon.stamp.StampActivity
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
                    val intent = Intent(this, StampActivity::class.java)
                    startActivity(intent)
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
                     val intent = Intent(this, ChatActivity::class.java)
                     startActivity(intent)
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
        binding.textUserNickname.text = App.prefs.getString("userNick", "")
        headerView.textUserNickname.text = App.prefs.getString("userNick", "")

        binding.btnBarcode.setOnClickListener {
            val intent = Intent(this, BarcodeActivity::class.java)
            startActivity(intent)
        }

         binding.btnStamp.setOnClickListener {
             val intent = Intent(this, StampActivity::class.java)
             startActivity(intent)
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
             val intent = Intent(this, ChatActivity::class.java)
             startActivity(intent)
         }

        // 정보 변경 발생 시 닉네임 필드 업데이트
        val myinfoForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.textUserNickname.text = App.prefs.getString("userNick", "")
                headerView.textUserNickname.text = App.prefs.getString("userNick", "")
            }
        }

        headerView.btnMyInfo.setOnClickListener {
            binding.drawerLayout.close()
            myinfoForResult.launch(Intent(this, MyInfoActivity::class.java))
        }

        headerView.btnLogOut.setOnClickListener {
            // 재확인
            val builder = AlertDialog.Builder(this)

            builder.setMessage(getString(R.string.ask_logout))
                .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                    val loginType = App.prefs.getString("LoginType", "")

                    // 로그아웃 처리
                    Firebase.auth.signOut()
                    App.prefs.clear()

                    if (loginType == "Google") {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.google_web_client_id))
                            .requestEmail()
                            .build()
                        GoogleSignIn.getClient(this, gso).signOut()
                    }
                    else if (loginType == "Facebook") {
                        LoginManager.getInstance().logOut()
                    }

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .setCancelable(true)

            builder.create().show()
        }

        // get images url from Firebase DB
        if (!InternetConnection.isInternetConnected(this)) {
            Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show()

            binding.slideIndicator.visibility = View.GONE
            binding.imgDefault.visibility = View.VISIBLE
            binding.viewPagerMainImage.visibility = View.INVISIBLE
        }
        else {
            binding.slideIndicator.visibility = View.VISIBLE
            binding.imgDefault.visibility = View.INVISIBLE
            binding.viewPagerMainImage.visibility = View.VISIBLE

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
                        binding.viewPagerMainImage.visibility = View.INVISIBLE
                    }
                }
                .addOnFailureListener {
                    binding.slideIndicator.visibility = View.GONE
                    binding.imgDefault.visibility = View.VISIBLE
                    binding.viewPagerMainImage.visibility = View.INVISIBLE
                }
        }
    }

    private inner class SlideImageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = SLIDE_NUM_PAGES
        override fun createFragment(position: Int): Fragment = MainImageFragment(mainImageList.get(position))
    }

    // if navigation is open, close navigation drawer
    // else, BackPress twice to close the app
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawers()
        }
        else {
            if (doubleBackPressed) {
                closeToast.cancel()
                super.onBackPressed()
                return
            }
            this.doubleBackPressed = true
            closeToast =
                Toast.makeText(this, getString(R.string.toast_backpress_close), Toast.LENGTH_SHORT)
            closeToast.show()
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackPressed = false }, 2000)
        }
    }
}