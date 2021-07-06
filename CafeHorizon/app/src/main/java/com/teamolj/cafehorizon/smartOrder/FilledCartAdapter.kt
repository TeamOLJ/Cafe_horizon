package com.teamolj.cafehorizon.smartOrder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamolj.cafehorizon.databinding.RecyclerItemFilledCartBinding
import java.text.DecimalFormat

class FilledCartAdapter : RecyclerView.Adapter<FilledCartAdapter.filledCartHolder>() {
    internal var cartList = mutableListOf<Cart>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): filledCartHolder =
        filledCartHolder(
            RecyclerItemFilledCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: filledCartHolder, position: Int) {
        holder.setCartItem(cartList.get(position))
    }

    override fun getItemCount(): Int = cartList.size


    inner class filledCartHolder(private var binding: RecyclerItemFilledCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var eachPrice: Int = 0


        fun setCartItem(cartItem: Cart) {
            eachPrice = cartItem.eachPrice

            binding.textCafeMenuName.text = cartItem.cafeMenuName
            binding.textAmount.text = cartItem.cafeMenuAmount.toString()
            changePriceText(cartItem.cafeMenuAmount)


            binding.imageBtnMinus.setOnClickListener {
                val amount = binding.textAmount.text.toString().toInt()

                if (amount > 1) {
                    changePriceText(amount - 1)
                    binding.textAmount.text = (amount - 1).toString()
                }
            }

            binding.imageBtnPlus.setOnClickListener {
                val amount = binding.textAmount.text.toString().toInt()

                changePriceText(amount + 1)
                binding.textAmount.text = (amount + 1).toString()
            }

            binding.btnDelete.setOnClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("선택 삭제")
                    .setMessage("제품이 장바구니에서 삭제됩니다.\n계속하시겠습니까?")
                    .setNegativeButton("취소") { _, _ -> }
                    .setPositiveButton("삭제") { _, _ ->
                        AppDatabase.getInstance(binding.root.context).cartDao().delete(cartItem)
                    }
                    .show()
            }
        }

        fun changePriceText(amount: Int) {
            binding.textCafeMenuTotalPrice.text =
                DecimalFormat("###,###원").format(eachPrice * amount)
        }
    }
}