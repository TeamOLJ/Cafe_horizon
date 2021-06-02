package com.teamolj.cafehorizon.newsAndEvents

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemNewsBinding


class NewsAdapter : RecyclerView.Adapter<NewsAdapter.newsHolder>() {
    internal var newsList = mutableListOf<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): newsHolder =
        newsHolder(
            RecyclerItemNewsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: newsHolder, position: Int) {
        var news = newsList.get(position)
        holder.setNews(news)
    }

    override fun getItemCount(): Int = newsList.size


    inner class newsHolder(private var binding: RecyclerItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var news: News

        init {
            binding.root.setOnClickListener {
                var intent = Intent(binding.root.context, NewsDetailActivity::class.java).apply {
                    putExtra("object", news)
                }

                binding.root.context.startActivity(intent)
            }
        }

        internal fun setNews(n: News) {
            this.news = n

            binding.imageNewIcon.setImageResource(R.drawable.ic_new_item)
//        binding.imageNewIcon.setImageResource(android.R.color.transparent)
            binding.textNewsTitle.text = news.title
            binding.textNewsDate.text = news.date
        }
    }
}