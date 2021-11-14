package com.teamolj.cafehorizon.newsAndEvents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.ActivityNewsDetailBinding
import java.text.SimpleDateFormat

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val news: News = intent.getSerializableExtra("news") as News

        if (intent.getBooleanExtra("over3Days", true)) {
            binding.imageNewIcon.visibility = View.GONE

            val param = binding.textNewsTitle.layoutParams as ViewGroup.MarginLayoutParams
            param.leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20F, resources.displayMetrics).toInt()
            binding.textNewsTitle.layoutParams = param
        }

        binding.textNewsTitle.text = news.title
        binding.textNewsDate.text = SimpleDateFormat("yyyy-MM-dd").format(news.date)
        binding.textNewsContent.text = news.content
    }
}