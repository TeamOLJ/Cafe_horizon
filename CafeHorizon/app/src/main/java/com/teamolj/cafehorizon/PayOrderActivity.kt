package com.teamolj.cafehorizon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.teamolj.cafehorizon.databinding.ActivityPayOrderBinding
import com.teamolj.cafehorizon.smartOrder.AppDatabase
import com.teamolj.cafehorizon.smartOrder.Cart

class PayOrderActivity : AppCompatActivity() {
    companion object{
        val ORDER_NOW = "order now"
        val ORDER_CART = "order cart"
    }
    private lateinit var binding: ActivityPayOrderBinding

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
        var cafeMenuList: MutableList<Cart>

        if (state == ORDER_NOW) {
            cafeMenuList = mutableListOf(intent.getSerializableExtra("cafeMenu") as Cart)

        } else if (state == ORDER_CART) {
            cafeMenuList = AppDatabase.getInstance(this).cartDao().getAllByType().toMutableList()
        }


        binding.groupEatOption.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnForHere.id -> {

                    }
                    binding.btnToGo.id -> {

                    }
                }
            }
        }
    }
}