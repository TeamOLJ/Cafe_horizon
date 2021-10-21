package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityOrderedListBinding
import com.teamolj.cafehorizon.smartOrder.MenuInfo
import kotlin.reflect.typeOf

class OrderedListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderedListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrderedListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val topAppBar = binding.toolbar

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val db = Firebase.firestore
        val userUid = Firebase.auth.currentUser!!.uid
        val docRef = db.collection("UserInformation").document(userUid).collection("Orders")

        val orderedList = mutableListOf<Order>()

        docRef.orderBy("orderTime", Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            for (document in documents) {
                // 주문메뉴 리스트 가져오기
                val menuList = mutableListOf<MenuInfo>()

                for (orderMenu in document.data["orderMenuList"] as List<HashMap<String, Any>>) {
                    val menuInfo = MenuInfo()
                    with(menuInfo){
                        name=orderMenu["name"].toString()
                        price = orderMenu["price"].toString().toInt()
                        category = orderMenu["category"].toString().toInt()
                        amount = orderMenu["amount"].toString().toInt()
                        optionShot = orderMenu["optionShot"].toString().toInt()
                        optionSyrup = orderMenu["optionSyrup"].toString().toInt()
                        optionWhipping = orderMenu["optionWhipping"].toString()=="true"
                    }
                    menuList.add(menuInfo)
                }

                val inputOrder = Order(
                    document.data["orderTitle"].toString(),
                    document.getTimestamp("orderTime")!!.toDate(),
                    document.data["orderState"].toString(),
                    document.data["couponPath"].toString(),
                    menuList
                )

                // 주문목록 리스트에 추가
                orderedList.add(inputOrder)
            }
            binding.progressBar.visibility = View.GONE

            var adapter = OrderedListAdapter()
            adapter.orderedList = orderedList
            binding.recyclerViewOrderedList.adapter = adapter
            binding.recyclerViewOrderedList.layoutManager = LinearLayoutManager(this)

        }
            .addOnFailureListener {
                Log.w("firebase", "Error getting documents.", it)
                Toast.makeText(this, getString(R.string.toast_error_occurred), Toast.LENGTH_SHORT)
                    .show()
            }


    }
}