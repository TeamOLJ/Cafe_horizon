package com.teamolj.cafehorizon.notice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.notice_item_recycler.view.*
import java.util.*

data class Notice(var content: String, var date: Long)  //테스트를 위해 date 변수형을 Date->Long 으로 변경

class noticeAdapter : RecyclerView.Adapter<noticeHolder>() {
    var noticeList = mutableListOf<Notice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): noticeHolder {
        return noticeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notice_item_recycler, parent, false)
        )
    }

    override fun onBindViewHolder(holder: noticeHolder, position: Int) {
        val notice = noticeList.get(position)
        holder.setNotice(notice)
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }


}

class noticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setNotice(notice: Notice) {
        itemView.noticeContent.text = notice.content
        itemView.noticeDate.text = notice.date.toString()
    }
}