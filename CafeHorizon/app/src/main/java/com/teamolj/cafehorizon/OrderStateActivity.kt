package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.databinding.ActivityOrderStateBinding
import com.teamolj.cafehorizon.views.PayOrderItemView
import java.lang.NullPointerException
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class OrderStateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderStateBinding

    private val db = Firebase.firestore
    private lateinit var orderedList: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar

        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        when (intent.getStringExtra("from")) {
            "OrderedListActivity" -> {
                try {
                    orderedList = intent.getBundleExtra("bundle")?.getParcelable<Order>("order")!!
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    Toast.makeText(this, "주문목록을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            "PayOrderActivity" -> {
                orderedList = intent.getBundleExtra("bundle")?.getParcelable<Order>("order")!!
            }

            else -> {
                Toast.makeText(this, "주문목록을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // 주문 상태에 맞게 UI 변경
        when (orderedList.state) {
            getString(R.string.text_order_standby) -> {
                binding.textOrderStandby.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                binding.textStateDesc.text = getString(R.string.desc_order_standby)

            }
            getString(R.string.text_order_receipt) -> {
                binding.textOrderReceipt.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                binding.textStateDesc.text = getString(R.string.desc_order_receipt)

            }
            getString(R.string.text_pickup_wait) -> {
                binding.textPickupWait.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                binding.textStateDesc.text = getString(R.string.desc_pickup_wait)

            }
            getString(R.string.text_pickup_finish) -> {
                binding.textPickupFinish.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
                binding.textStateDesc.text = getString(R.string.desc_pickup_finish)

            }
        }

        binding.textOrderTime.text = "주문일시: ${
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(orderedList.orderTime.time)
        }"

        // 주문목록의 메뉴 출력
        var listTotalPrice = 0;
        for (menu in orderedList.orderMenu) {
            listTotalPrice += (menu.eachPrice * menu.cafeMenuAmount)

            val menuView = PayOrderItemView(this).apply {
                setItemType(PayOrderActivity.MENU)
                setCafeMenuInfo(menu.cafeMenuName, menu.cafeMenuAmount, menu.eachPrice)
            }
            binding.layoutOrderItems.addView(menuView)

            if (menu.optionShot > 0) {
                val shotView = PayOrderItemView(this).apply {
                    setItemType(PayOrderActivity.OPTION)
                    setOptionInfo("샷 추가", menu.optionShot)
                }
                binding.layoutOrderItems.addView(shotView)
            }
            if (menu.optionSyrup > 0) {
                val syrupView = PayOrderItemView(this).apply {
                    setItemType(PayOrderActivity.OPTION)
                    setOptionInfo("시럽 추가", menu.optionSyrup)
                }
                binding.layoutOrderItems.addView(syrupView)
            }
            if (!menu.optionWhipping) {
                val whippingView = PayOrderItemView(this).apply {
                    setItemType(PayOrderActivity.OPTION)
                    setOptionInfo("휘핑 X", 0)
                }
                binding.layoutOrderItems.addView(whippingView)
            }
        }

        // 쿠폰 정보 가져오기
        if (orderedList.couponPath != "") {
            val docRef = db.collection("UserInformation")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("Coupons")
                .document(orderedList.couponPath)

            docRef.get().addOnSuccessListener { Snapshot ->
                if (Snapshot.exists()) {
                    val discountView = PayOrderItemView(this)
                    discountView.setItemType(PayOrderActivity.DISCOUNT)
                    discountView.setDiscountInfo(
                        Snapshot["couponName"].toString(),
                        Snapshot["discount"].toString().toInt()
                    )

                    listTotalPrice -= discountView.getDiscountPrice()
                    binding.layoutOrderItems.addView(discountView)

                    binding.textTotalPrice.text = DecimalFormat("총 ###,###원").format(listTotalPrice)
                }
            }
        } else {
            binding.textTotalPrice.text = DecimalFormat("총 ###,###원").format(listTotalPrice)
        }
    }
}