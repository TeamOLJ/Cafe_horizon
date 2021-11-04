package com.teamolj.cafehorizon.smartOrder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamolj.cafehorizon.databinding.RecyclerItemFilledCartBinding
import java.text.DecimalFormat

class FilledCartAdapter(val context: Context) :
    RecyclerView.Adapter<FilledCartAdapter.filledCartHolder>() {
    internal var menuList = mutableListOf<MenuInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): filledCartHolder =
        filledCartHolder(
            RecyclerItemFilledCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: filledCartHolder, position: Int) {
        holder.setCartItem(menuList.get(position))
    }

    override fun getItemCount(): Int = menuList.size

    inner class filledCartHolder(private var binding: RecyclerItemFilledCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var eachPrice: Int = 0

        fun setCartItem(cartItem: MenuInfo) {
            eachPrice = cartItem.price
            var menuOption = ""

            if ((cartItem.optionType / 100 == 1) && (cartItem.optionShot > 0)) {
                menuOption += " 샷 ${cartItem.optionShot} "
            }
            if ((cartItem.optionType % 100 / 10 == 1) && cartItem.optionSyrup > 0) {
                menuOption += " 시럽 ${cartItem.optionSyrup} "
            }
            if ((cartItem.optionType % 10 == 1) && !cartItem.optionWhipping) {
                menuOption += " 휘핑 X "
            }

            binding.textCafeMenuName.text = cartItem.name
            binding.textCafeMenuOption.text = menuOption
            Glide.with(binding.root).load(cartItem.imageUrl).circleCrop()
                .into(binding.imageCafeMenu)

            changeAmountAndPrice(cartItem.amount)

            binding.imageBtnMinus.setOnClickListener {
                val amount = binding.textAmount.text.toString().toInt()

                if (amount > 1) {
                    cartItem.amount -= 1
                    AppDatabase.getInstance(binding.root.context).cartDao().update(cartItem)
                    notifyDataSetChanged()
                    FilledCartActivity.changeTotalPrice()
                }
            }

            binding.imageBtnPlus.setOnClickListener {
                cartItem.amount += 1
                AppDatabase.getInstance(binding.root.context).cartDao().update(cartItem)
                notifyDataSetChanged()
                FilledCartActivity.changeTotalPrice()
            }

            binding.btnDelete.setOnClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("선택 삭제")
                    .setMessage("제품이 장바구니에서 삭제됩니다.\n계속하시겠습니까?")
                    .setNegativeButton("취소") { _, _ -> }
                    .setPositiveButton("삭제") { _, _ ->
                        AppDatabase.getInstance(binding.root.context).cartDao().delete(cartItem)
                        menuList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        FilledCartActivity.changeTotalPrice()

                        if (menuList.size == 0) {
                            Toast.makeText(context, "장바구니가 비었습니다.", Toast.LENGTH_SHORT).show()
                            (context as FilledCartActivity).finish()
                        }
                    }
                    .show()
            }
        }

        private fun changeAmountAndPrice(amount: Int) {
            binding.textAmount.text = amount.toString()
            binding.textCafeMenuTotalPrice.text =
                DecimalFormat("###,###원").format(eachPrice * amount)
        }
    }
}