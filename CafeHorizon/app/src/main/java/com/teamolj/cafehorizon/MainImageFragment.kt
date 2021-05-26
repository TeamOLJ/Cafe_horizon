package com.teamolj.cafehorizon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.databinding.FragmentMainImageBinding

class MainImageFragment(private val imageUrl : String) : Fragment() {
    private lateinit var binding: FragmentMainImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainImageBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.img_placeholder)
            .thumbnail(0.1f)
            .into(binding.imageView)

        return binding.root
    }
}