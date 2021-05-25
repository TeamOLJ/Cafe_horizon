package com.teamolj.cafehorizon.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamolj.cafehorizon.databinding.ActivityNoticeBinding
import java.text.SimpleDateFormat

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data: MutableList<Notice> = loadNotices()

        var adapter = NoticeAdapter()
        adapter.noticeList = data
        binding.recyclerViewNotice.adapter = adapter
        binding.recyclerViewNotice.layoutManager = LinearLayoutManager(this)
    }


    internal fun loadNotices(): MutableList<Notice> {
        val data:MutableList<Notice> = mutableListOf()

        for (no in 0..100) {
            val title = "이것은 ${no}번째 알림입니다. 14일 이전의 알림만 표시하도록 합니다. height 50dp"
            val date = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            var notice = Notice(title, sdf.format(date))
            data.add(notice)
        }
        
        /*
        데이터베이스에 접근해서 content, date(SimpleDateFormat("yyyy-MM-dd"), 14일 이전) 불러오기
        input: 유저 정보
         */
        
        return data
    }
}