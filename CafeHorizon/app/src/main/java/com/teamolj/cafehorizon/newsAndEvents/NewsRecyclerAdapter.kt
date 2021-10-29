package com.teamolj.cafehorizon.newsAndEvents


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemNewsBinding
import java.text.SimpleDateFormat


class NewsRecyclerAdapter : RecyclerView.Adapter<NewsRecyclerAdapter.newsHolder>() {
    internal var newsList = mutableListOf<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): newsHolder =
        newsHolder(
            RecyclerItemNewsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: newsHolder, position: Int) {
        holder.setNews(newsList[position])
    }

    override fun getItemCount(): Int = newsList.size


    inner class newsHolder(private var binding: RecyclerItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setNews(news: News) {
            val betweenDays = (System.currentTimeMillis() - news.date) / (24 * 60 * 60 * 1000)
            val over3Days = betweenDays > 3

            if (over3Days) {
                binding.imageNewIcon.visibility = View.INVISIBLE
            }

            binding.textNewsTitle.text = news.title
            binding.textNewsDate.text = SimpleDateFormat("yyyy-MM-dd").format(news.date)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, NewsDetailActivity::class.java)
                intent.putExtra("news", news)
                intent.putExtra("over3Days", over3Days)

                binding.root.context.startActivity(intent)
            }
        }
    }
}