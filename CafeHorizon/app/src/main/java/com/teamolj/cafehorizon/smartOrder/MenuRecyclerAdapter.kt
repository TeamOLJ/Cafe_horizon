package com.teamolj.cafehorizon.smartOrder

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemMenuBinding

class MenuRecyclerAdapter : RecyclerView.Adapter<MenuRecyclerAdapter.menuHolder>() {
    internal var menuList: MutableList<menuInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): menuHolder = menuHolder(
        RecyclerItemMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: menuHolder, position: Int) {
        val menu = menuList.get(position)
        holder.setMenu(menu)
    }

    override fun getItemCount(): Int = menuList.size


    inner class menuHolder(private var binding: RecyclerItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var menu: menuInfo

        init {
            binding.root.setOnClickListener {
                var intent = Intent(binding.root.context, MenuDetailActivity::class.java).apply {
                    putExtra("name", menu.name)
                    putExtra("price", menu.price.toString())
                }

                binding.root.context.startActivity(intent)
            }
        }

        fun setMenu(menu: menuInfo) {
            this.menu = menu
            binding.textMenuName.text = menu.name
            binding.textMenuOrglPrice.text = menu.price.toString()

        }
    }
}