package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemCafeMenuBinding
import java.lang.StringBuilder

class CafeMenuRecyclerAdapter : RecyclerView.Adapter<CafeMenuRecyclerAdapter.cafeMenuHolder>() {
    internal lateinit var menuList: Array<MutableList<MenuInfo>>
    private var category: Int = 0

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

    fun setCategory(position: Int) {
        category = position
    }


    inner class cafeMenuHolder(private var binding: RecyclerItemCafeMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setMenu(menuInfo: MenuInfo) {
            Glide.with(binding.root.context)
                .load(menuInfo.imageUrl)
                .circleCrop()
                .thumbnail(0.1f)
                .into(binding.imageCafeMenu)
            binding.textCafeMenuName.text = menuInfo.name
            binding.textCafeMenuOrgPrice.text = menuInfo.price.toString()

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, CafeMenuDetailActivity::class.java)
                intent.putExtra("info", menuInfo)

                binding.root.context.startActivity(intent)
            }
        }
    }
}