package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivitySmartOrderBinding

open class SmartOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmartOrderBinding

    lateinit var dbApp: AppDatabase
    lateinit var db: FirebaseFirestore

    lateinit var menuList:Array<MutableList<MenuInfo>>
    lateinit var categories:Array<String>
    lateinit var adapter:CafeMenuRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        dbApp = AppDatabase.getInstance(this)
        db = Firebase.firestore

        db.collection("CafeMenu").get().addOnSuccessListener { result ->
            menuList = Array(result.size()) { mutableListOf() }
            categories = Array(result.size()) { "" }
            adapter = CafeMenuRecyclerAdapter()

            for (i in 0 until result.size()) {
                // 파이어베이스의 카테고리별 메뉴 문서
                val document = result.documents[i]
                // 문서 이름(카테고리명) 저장
                categories[i] = document.id

                for (field in document.data!!) {
                    val hashMap = field.value as HashMap<String, Any>

                    // 카테고리별 메뉴 리스트에 메뉴 정보 저장
                    menuList[i].add(
                        MenuInfo(
                            hashMap["name"].toString(),
                            hashMap["description"].toString(),
                            hashMap["imageUrl"].toString(),
                            hashMap["price"].toString().toInt(),
                            i,
                            hashMap["optionType"].toString().toInt()   //옵션 여부를 1/0으로 구분(샷-시럽-휘핑 순서)
                        )
                    )
                }

            }

            adapter.menuList = menuList
            val viewPager = binding.viewPager
            viewPager.adapter = SmartOrderAdapter(this)

            val tabLayout = binding.tabLayout
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = categories[position]
            }.attach()

            binding.progressBar.visibility = View.GONE

        }.addOnFailureListener { exception ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(binding.root.context,
                getString(R.string.toast_error_occurred),
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filled_cart -> {
                var intent = Intent(this, FilledCartActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_empty_cart -> {
                var intent = Intent(this, EmptyCartActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_smartorder, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        updateToolbarMenu(menu!!)
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onResume() {   //MenuDetailActivitiy에서 돌아올 때 액션바 새로고침
        invalidateOptionsMenu()
        super.onResume()
    }


    private fun updateToolbarMenu(menu: Menu) {
        if (dbApp.cartDao().getCount()==0) {
            menu.findItem(R.id.action_filled_cart).isVisible = false
            menu.findItem(R.id.action_empty_cart).isVisible = true
        } else {
            menu.findItem(R.id.action_filled_cart).isVisible = true
            menu.findItem(R.id.action_empty_cart).isVisible = false
        }
    }

    fun getAdapter(position:Int):CafeMenuRecyclerAdapter {
        adapter.setCategory(position)
        return adapter
    }

    private inner class SmartOrderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = categories.size
        override fun createFragment(position:Int): Fragment = CafeMenuRecyclerFragment(position)
    }
}