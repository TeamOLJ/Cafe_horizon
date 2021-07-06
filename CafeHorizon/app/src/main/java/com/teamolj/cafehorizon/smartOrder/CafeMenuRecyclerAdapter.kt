package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemCafeMenuBinding

class CafeMenuRecyclerAdapter : RecyclerView.Adapter<CafeMenuRecyclerAdapter.cafeMenuHolder>() {
    internal var cafeMenuList: MutableList<cafeMenuInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cafeMenuHolder =
        cafeMenuHolder(
            RecyclerItemCafeMenuBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: cafeMenuHolder, position: Int) {
        val menu = cafeMenuList.get(position)
        holder.setMenu(menu)
    }

    override fun getItemCount(): Int = cafeMenuList.size


    inner class cafeMenuHolder(private var binding: RecyclerItemCafeMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var cafeMenu: cafeMenuInfo

        init {
            binding.root.setOnClickListener {
                var intent =
                    Intent(binding.root.context, CafeMenuDetailActivity::class.java).apply {
                        putExtra("name", cafeMenu.name)
                        putExtra("price", cafeMenu.price)
                    }

                binding.root.context.startActivity(intent)
            }
        }

        fun setMenu(cafeMenu: cafeMenuInfo) {
            this.cafeMenu = cafeMenu

            Glide.with(binding.root.context).load(R.drawable.coffee_image).circleCrop().into(binding.imageCafeMenu)
            binding.textCafeMenuName.text = cafeMenu.name
            binding.textCafeMenuOrgPrice.text = cafeMenu.price.toString()

        }
    }
}