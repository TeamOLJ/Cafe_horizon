package com.teamolj.cafehorizon.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityImageDetailBinding

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityImageDetailBinding
    private var toolbarVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val photoUrl = intent.getStringExtra("photoUrl")
        Glide.with(this)
            .load(photoUrl)
            .into(binding.imageDetail)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            toolbarVisible = !toolbarVisible
            if (toolbarVisible) {
                binding.appBarLayout.visibility = View.VISIBLE
            } else {
                binding.appBarLayout.visibility = View.INVISIBLE
            }
        }
        return super.onTouchEvent(event)
    }
}