package com.teamolj.cafehorizon.newsAndEvents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.recycler_item_news.view.*


internal data class News(
    var title: String,
    var content: String,
    var date: Long,
    var isNew: Boolean
)   //테스트를 위해 date 변수형을 Date->Long 으로 변경

class NewsAdapter : RecyclerView.Adapter<newsHolder>() {
    internal var newsList = mutableListOf<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): newsHolder =
        newsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_news, parent, false)
        )

    override fun onBindViewHolder(holder: newsHolder, position: Int) {
        var news = newsList.get(position)
//        holder.saveContent(news.content)
        holder.setNews(news)
    }

    override fun getItemCount(): Int = newsList.size
}

class newsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var content: String? = null

    init {
        itemView.setOnClickListener {
            Toast.makeText(itemView?.context, content, Toast.LENGTH_SHORT).show()
        }
    }

    internal fun setNews(news: News) {
        this.content = news.content

        if (news.isNew) {
            itemView.imageNewIcon.setImageResource(android.R.color.black)
        } else {
            itemView.imageNewIcon.setImageResource(android.R.color.transparent)
        }

        itemView.textNewsTitle.text = news.title
        itemView.textNewsDate.text = news.date.toString()
    }

//    internal fun saveContent(content: String) {
//        this.content = content
//    }
}