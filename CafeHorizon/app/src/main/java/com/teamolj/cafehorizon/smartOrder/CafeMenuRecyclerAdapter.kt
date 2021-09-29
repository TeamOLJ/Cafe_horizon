package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.databinding.RecyclerItemCafeMenuBinding

class CafeMenuRecyclerAdapter() : RecyclerView.Adapter<CafeMenuRecyclerAdapter.cafeMenuHolder>() {
    internal lateinit var menuList: Array<MutableList<MenuInfo>>
    private var category:Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cafeMenuHolder =
        cafeMenuHolder(
            RecyclerItemCafeMenuBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: cafeMenuHolder, position: Int) {
        holder.setMenu(menuList[category][position])
    }

    override fun getItemCount(): Int {
        return menuList[category].size
    }

    fun setCategory(position:Int) {
        category = position
    }


    inner class cafeMenuHolder(private var binding: RecyclerItemCafeMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var cafeMenu: MenuInfo

        init {
            binding.root.setOnClickListener {
                var intent =
                    Intent(binding.root.context, CafeMenuDetailActivity::class.java).apply {
                        putExtra("info", cafeMenu)
                    }

                binding.root.context.startActivity(intent)
            }
        }

        fun setMenu(cafeMenu: MenuInfo) {
            this.cafeMenu = cafeMenu

            Glide.with(binding.root.context).load(cafeMenu.imageUrl).circleCrop().into(binding.imageCafeMenu)
            binding.textCafeMenuName.text = cafeMenu.name
            binding.textCafeMenuOrgPrice.text = cafeMenu.price.toString()

        }
    }
}