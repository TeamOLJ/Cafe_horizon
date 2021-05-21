package com.teamolj.cafehorizon.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.recycler_item_notice.view.*

internal data class Notice(
    var content: String,
    var date: Long
)  //테스트를 위해 date 변수형을 Date->Long 으로 변경

class NoticeAdapter : RecyclerView.Adapter<noticeHolder>() {
    internal var noticeList = mutableListOf<Notice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): noticeHolder {
        return noticeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_notice, parent, false)
        )
    }

    override fun onBindViewHolder(holder: noticeHolder, position: Int) {
        val notice = noticeList.get(position)
        holder.setNotice(notice)
    }

    override fun getItemCount(): Int = noticeList.size

}

class noticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal fun setNotice(notice: Notice) {
        itemView.textNoticeContent.text = notice.content
        itemView.textNoticeDate.text = notice.date.toString()
    }
}