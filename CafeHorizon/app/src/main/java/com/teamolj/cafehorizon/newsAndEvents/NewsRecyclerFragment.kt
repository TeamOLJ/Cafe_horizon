package com.teamolj.cafehorizon.newsAndEvents

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.FragmentNewsRecyclerBinding

class NewsRecyclerFragment : Fragment() {
    private var _binding: FragmentNewsRecyclerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsRecyclerBinding.inflate(inflater, container, false)
        val view = binding.root

        val data: MutableList<News> = loadNews()

        var adapter = NewsAdapter()
        adapter.newsList = data
        binding.recyclerViewNews.adapter = adapter
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this.context)

        return view
    }

    internal fun loadNews(): MutableList<News> {
        //firebase 연결하기
        val data: MutableList<News> = mutableListOf()

        for (no in 0..10) {
            val title = "${no}번째 새소식"
            val content = "${no}번째 소식의 내용입니다.\n첫번째줄\n두번째줄\n세번째줄\n네번째줄\n다섯번째줄\n여섯번째줄\n일곱번째줄 "
            val date = System.currentTimeMillis().toString()

            var news = News(title, content, date)
            data.add(news)
        }

        return data
    }
}