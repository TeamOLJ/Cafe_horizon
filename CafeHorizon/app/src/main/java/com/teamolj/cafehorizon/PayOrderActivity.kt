package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.coupon.Coupon
import com.teamolj.cafehorizon.databinding.ActivityPayOrderBinding
import com.teamolj.cafehorizon.smartOrder.AppDatabase
import com.teamolj.cafehorizon.smartOrder.Cart
import com.teamolj.cafehorizon.views.PayOrderItemView
import java.text.DecimalFormat

class PayOrderActivity : AppCompatActivity() {
    companion object {
        val ORDER_NOW = "order now"
        val ORDER_CART = "order cart"
        val EAT_FOR_HERE = "for here"
        val EAT_TO_GO = "to go"
        val PAY_CREDIT = "credit card"
        val PAY_EASY_PAY = "easy pay"
        val PAY_BANK_TRANSFER = "bank transfer"
    }

    private lateinit var binding: ActivityPayOrderBinding

    private val OPTION = "Option"
    private val MENU = "Menu"

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }


        val state = intent.getStringExtra("state")
        var cafeMenuList = mutableListOf<Cart>()
        var listTotalPrice = 0
        var eatOption = EAT_FOR_HERE
        var payOption = PAY_CREDIT


        if (state == ORDER_NOW) {
            cafeMenuList = mutableListOf(intent.getSerializableExtra("cafeMenu") as Cart)
        } else if (state == ORDER_CART) {
            cafeMenuList = AppDatabase.getInstance(this).cartDao().getAllByType().toMutableList()
        }

        for (menu in cafeMenuList) {
            listTotalPrice += (menu.eachPrice * menu.cafeMenuAmount)

            val menuView = PayOrderItemView(this).apply {
                setItemType(MENU)
                setCafeMenuInfo(menu.cafeMenuName, menu.cafeMenuAmount, menu.eachPrice)
            }
            binding.layoutCafeMenu.addView(menuView)

            if (menu.optionShot > 0) {
                val shotView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("샷 추가", menu.optionShot)
                }
                binding.layoutCafeMenu.addView(shotView)
            }
            if (menu.optionSyrup > 0) {
                val syrupView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("시럽 추가", menu.optionSyrup)
                }
                binding.layoutCafeMenu.addView(syrupView)
            }
            if (!menu.optionWhipping) {
                val whippingView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("휘핑 X", 0)
                }
                binding.layoutCafeMenu.addView(whippingView)
            }
        }

        binding.textTotalPrice.text = DecimalFormat("총 ###,###원").format(listTotalPrice)


        val userUid = Firebase.auth.currentUser!!.uid
        val couponArray = ArrayList<Coupon>()

        val docRef = db.collection("UserInformation").document(userUid).collection("Coupons")
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val isUsed = document.data["isUsed"] as Boolean
                val expiryDate: Int = document.data["expiryDate"].toString().toInt()

                Log.d(
                    "TAG",
                    "${document.data["couponName"]} ${document.data["discount"]} $expiryDate $isUsed"
                )

                if (!isUsed && System.currentTimeMillis() < expiryDate) {
                    couponArray.add(
                        Coupon(
                            document.data["couponName"].toString(),
                            expiryDate,
                            document.data["discount"].toString().toInt(),
                            isUsed,
                            null
                        )
                    )
                }
            }
        }

        val arrayAdapter =
            ArrayAdapter<Coupon>(this, android.R.layout.simple_spinner_item, couponArray)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCoupon.adapter = arrayAdapter
        //https://app-dev.tistory.com/149


        binding.groupEatOption.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                eatOption = when (checkedId) {
                    binding.btnForHere.id -> EAT_FOR_HERE
                    binding.btnToGo.id -> EAT_TO_GO

                    else -> ""
                }
            }
        }


        binding.groupPayOption.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                payOption = when (checkedId) {
                    binding.btnCreditCard.id -> PAY_CREDIT
                    binding.btnEasyPay.id -> PAY_EASY_PAY
                    binding.btnBankTransfer.id -> PAY_BANK_TRANSFER

                    else -> ""
                }
            }
        }

        binding.btnPayment.setOnClickListener {
            Toast.makeText(this, "$eatOption $payOption", Toast.LENGTH_SHORT).show()
            /*
            취식 옵션 보내기
            페이 옵션대로 결제 인텐드 실행하기
             */
            val intent = Intent(this, OrderStateActivity::class.java).apply {
                putExtra("orderTime", System.currentTimeMillis())
            }
        }
    }
}