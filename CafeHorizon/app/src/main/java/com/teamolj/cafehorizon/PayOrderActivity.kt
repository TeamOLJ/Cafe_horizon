package com.teamolj.cafehorizon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teamolj.cafehorizon.coupon.Coupon
import com.teamolj.cafehorizon.databinding.ActivityPayOrderBinding
import com.teamolj.cafehorizon.smartOrder.AppDatabase
import com.teamolj.cafehorizon.smartOrder.MenuInfo
import com.teamolj.cafehorizon.views.PayOrderItemView
import java.text.DecimalFormat
import java.util.*

class PayOrderActivity : AppCompatActivity() {
    companion object {
        const val ORDER_NOW = "order now"
        const val ORDER_CART = "order cart"

        const val EAT_FOR_HERE = "for here"
        const val EAT_TO_GO = "to go"

        const val PAY_CREDIT = "credit card"
        const val PAY_EASY_PAY = "easy pay"
        const val PAY_BANK_TRANSFER = "bank transfer"

        const val OPTION = "Option"
        const val MENU = "Menu"
        const val DISCOUNT = "Discount"
    }

    private lateinit var binding: ActivityPayOrderBinding

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

        val from = intent.getStringExtra("from")
        var menuList = mutableListOf<MenuInfo>()
        var listTotalPrice = 0
        var couponIndex = 0
        var eatOption = EAT_FOR_HERE
        var payOption = PAY_CREDIT


        // 바로 주문하기 or 장바구니 로부터 메뉴 리스트 가져오기
        if (from == ORDER_NOW) {
            menuList = mutableListOf(intent.getParcelableExtra("menuInfo")!!)
        } else if (from == ORDER_CART) {
            menuList = AppDatabase.getInstance(this).cartDao().getAllByCategory().toMutableList()
        }

        // 결제할 메뉴 리스트 표시하기
        for (menu in menuList) {
            listTotalPrice += (menu.price * menu.amount)

            val menuView = PayOrderItemView(this).apply {
                setItemType(MENU)
                setCafeMenuInfo(menu.name, menu.amount, menu.price)
            }
            binding.layoutMenuItems.addView(menuView)


            if ((menu.optionType / 100 == 1) && (menu.optionShot > 0)) {
                val shotView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("샷 추가", menu.optionShot)
                }
                binding.layoutMenuItems.addView(shotView)
            }

            if ((menu.optionType % 100 / 10 == 1) && (menu.optionSyrup > 0)) {
                val syrupView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("시럽 추가", menu.optionSyrup)
                }
                binding.layoutMenuItems.addView(syrupView)
            }

            if ((menu.optionType % 10 == 1) && !menu.optionWhipping) {
                val whippingView = PayOrderItemView(this).apply {
                    setItemType(OPTION)
                    setOptionInfo("휘핑 X", 0)
                }
                binding.layoutMenuItems.addView(whippingView)
            }
        }

        binding.textTotalPrice.text = DecimalFormat("총 ###,###원").format(listTotalPrice)


        // 쿠폰 스피너 설정하기
        val userUid = Firebase.auth.currentUser!!.uid
        val couponArray = arrayListOf<Coupon>(Coupon("", "쿠폰을 선택해주세요.", 0, 0, false))

        val discountView = PayOrderItemView(this)
        discountView.setItemType(DISCOUNT)

        val docRef = db.collection("UserInformation").document(userUid).collection("Coupons")
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val isUsed = document.data["isUsed"] as Boolean
                val expiryDate = (document.data["expiryDate"] as Timestamp).seconds * 1000

                if (!isUsed && System.currentTimeMillis() < expiryDate) {
                    couponArray.add(
                        Coupon(
                            document.id,
                            document.data["couponName"].toString(),
                            expiryDate,
                            document.data["discount"].toString().toInt(),
                            isUsed
                        )
                    )
                }
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, couponArray)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerCoupon.adapter = arrayAdapter

        }

        // 스피너 아이템 선택 리스너
        binding.spinnerCoupon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                try {
                    if (p2 == 0) {
                        couponIndex = 0
                        binding.layoutMenuItems.removeView(discountView)
                        binding.textTotalPrice.text =
                            DecimalFormat("총 ###,###원").format(listTotalPrice)
                    } else {
                        if (listTotalPrice >= couponArray[p2].discount) {
                            couponIndex = p2
                            discountView.setDiscountInfo(
                                couponArray[p2].couponName,
                                couponArray[p2].discount
                            )
                            binding.layoutMenuItems.addView(discountView)
                            binding.textTotalPrice.text =
                                DecimalFormat("총 ###,###원").format(listTotalPrice - discountView.getDiscountPrice())
                        } else {
                            couponIndex = 0
                            Toast.makeText(
                                applicationContext,
                                "결제 금액이 할인 금액보다 적습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.spinnerCoupon.setSelection(0);
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }


        binding.groupEatOption.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                eatOption = when (checkedId) {
                    binding.btnForHere.id -> EAT_FOR_HERE
                    binding.btnToGo.id -> EAT_TO_GO

                    else -> EAT_FOR_HERE
                }
            }
        }


        binding.groupPayOption.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                payOption = when (checkedId) {
                    binding.btnCreditCard.id -> PAY_CREDIT
                    binding.btnEasyPay.id -> PAY_EASY_PAY
                    binding.btnBankTransfer.id -> PAY_BANK_TRANSFER

                    else -> PAY_CREDIT
                }
            }
        }

        binding.btnPayment.setOnClickListener {
            /*
            결제 완료한 셈 치고 수행합니다.
            실제로는 payOption을 먼저 수행해야 합니다.
            카페에 eatOption도 전송해야 합니다.
             */
            val title = if (menuList.size > 1) {
                menuList[0].name
            } else {
                menuList[0].name + " 외"
            }

            val order = Order(
                title,
                Date(System.currentTimeMillis()),
                getString(R.string.text_order_standby),
                couponArray[couponIndex].couponPath,
                menuList
            )

            val bundle = Bundle()
            bundle.putParcelable("order", order)

            val intent = Intent(this, OrderStateActivity::class.java)
            intent.putExtra("bundle", bundle)
            intent.putExtra("from", "PayOrderActivity")
            startActivity(intent)

        }
    }
}