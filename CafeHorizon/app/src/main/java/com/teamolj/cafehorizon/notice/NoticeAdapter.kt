package com.teamolj.cafehorizon.notice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemNoticeBinding

class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.noticeHolder>() {
    var noticeList = mutableListOf<Notice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): noticeHolder = noticeHolder(
        RecyclerItemNoticeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )


    override fun onBindViewHolder(holder: noticeHolder, position: Int) {
        val notice = noticeList.get(position)
        holder.setNotice(notice)
    }

    override fun getItemCount(): Int = noticeList.size


    inner class noticeHolder(private var binding: RecyclerItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setNotice(notice: Notice) {
            binding.textNoticeContent.text = notice.content
            binding.textNoticeDate.text = notice.date
        }
    }
}