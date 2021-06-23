package com.teamolj.cafehorizon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.databinding.FragmentHowToImageBinding

class HowToImageFragment(private val imageUrl : String) : Fragment() {
    private var _binding:FragmentHowToImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHowToImageBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.img_placeholder)
            .thumbnail(0.1f)
            .into(binding.imageView)

        return binding.root
    }
}