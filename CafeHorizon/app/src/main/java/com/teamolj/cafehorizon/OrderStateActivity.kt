package com.teamolj.cafehorizon

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
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

    private var from:String = ""

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
            // PayOrderActivity로 돌아가는 것 방지
            startActivity(Intent(applicationContext, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        from = intent.getStringExtra("from")!!

        when (from) {
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
        when (orderedList.orderState) {
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
        for (menu in orderedList.orderMenuList) {
            listTotalPrice += (menu.price * menu.amount)

            val menuView = PayOrderItemView(this).apply {
                setItemType(PayOrderActivity.MENU)
                setCafeMenuInfo(menu.name, menu.amount, menu.price)
            }
            binding.layoutOrderItems.addView(menuView)

            if ((menu.optionType / 100 == 1) && (menu.optionShot > 0)) {
                val shotView = PayOrderItemView(this).apply {
                    setItemType(PayOrderActivity.OPTION)
                    setOptionInfo("샷 추가", menu.optionShot)
                }
                binding.layoutOrderItems.addView(shotView)
            }

            if ((menu.optionType % 100 / 10 == 1) && (menu.optionSyrup > 0)) {
                val syrupView = PayOrderItemView(this).apply {
                    setItemType(PayOrderActivity.OPTION)
                    setOptionInfo("시럽 추가", menu.optionSyrup)
                }
                binding.layoutOrderItems.addView(syrupView)
            }

            if ((menu.optionType % 10 == 1) && !menu.optionWhipping) {
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

                    binding.textTotalPrice.text = DecimalFormat("###,###원").format(listTotalPrice)
                }
            }
        } else {
            binding.textTotalPrice.text = DecimalFormat("###,###원").format(listTotalPrice)
        }
    }

    override fun onBackPressed() {
        when(from) {
            "OrderedListActivity" -> {
                super.onBackPressed()
            }

            "PayOrderActivity" -> {
                startActivity(Intent(applicationContext, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }
    }
}