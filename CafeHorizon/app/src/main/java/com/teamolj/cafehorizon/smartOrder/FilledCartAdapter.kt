package com.teamolj.cafehorizon.smartOrder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemFilledCartBinding

class FilledCartAdapter : RecyclerView.Adapter<FilledCartAdapter.filledCartHolder>() {
    internal var cartList = mutableListOf<String>()

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

        init {

            binding.btnDelete.setOnClickListener {
                Toast.makeText(binding.root.context, "삭제 버튼 눌림", Toast.LENGTH_SHORT).show()
            }

            binding.imageBtnMinus.setOnClickListener {
                val amount = binding.textAmount.text.toString().toInt()

                if (amount > 1) {
                    binding.textAmount.text = (amount - 1).toString()
                }
            }

            binding.imageBtnPlus.setOnClickListener {
                val amount = binding.textAmount.text.toString().toInt()

                binding.textAmount.text = (amount + 1).toString()
            }
        }

        fun setCartItem(cartItem: String) {
            binding.textCafeMenuName.text = cartItem
        }

    }
}