package com.teamolj.cafehorizon.newsAndEvents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val news: News = intent.getSerializableExtra("news") as News

        binding.textNewsTitle.text = news.title
        binding.textNewsDate.text = news.date
        binding.textNewsContent.text = news.content
    }
}