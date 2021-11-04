package com.teamolj.cafehorizon.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayDeque

class ChatRecyclerAdapter(private val user: String) : RecyclerView.Adapter<ChatRecyclerHolder>() {
    private var messageList = ArrayDeque<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerHolder {
        return when (viewType) {
            R.layout.recycler_item_chat_bubble_in -> {
                ChatRecyclerHolder.BubbleInHolder(
                    RecyclerItemChatBubbleInBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.recycler_item_chat_photo_in -> {
                ChatRecyclerHolder.PhotoInHolder(
                    RecyclerItemChatPhotoInBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.recycler_item_chat_bubble_out -> {
                ChatRecyclerHolder.BubbleOutHolder(
                    RecyclerItemChatBubbleOutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.recycler_item_chat_photo_out -> {
                ChatRecyclerHolder.PhotoOutHolder(
                    RecyclerItemChatPhotoOutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.recycler_item_chat_date_divider -> {
                ChatRecyclerHolder.DateDividerHolder(
                    RecyclerItemChatDateDividerBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> throw IllegalAccessException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: ChatRecyclerHolder, position: Int) {
        when (holder) {
            is ChatRecyclerHolder.BubbleInHolder -> holder.bind(messageList[position])
            is ChatRecyclerHolder.PhotoInHolder -> holder.bind(messageList[position])
            is ChatRecyclerHolder.BubbleOutHolder -> holder.bind(messageList[position])
            is ChatRecyclerHolder.PhotoOutHolder -> holder.bind(messageList[position])
            is ChatRecyclerHolder.DateDividerHolder -> holder.bind(messageList[position].time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.user == "DIVIDER") {
            R.layout.recycler_item_chat_date_divider
        } else if (message.user == user) {
            if (message.contentText != null) {
                R.layout.recycler_item_chat_bubble_in
            } else {
                R.layout.recycler_item_chat_photo_in
            }
        } else {
            if (message.contentText != null) {
                R.layout.recycler_item_chat_bubble_out
            } else {
                R.layout.recycler_item_chat_photo_out
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    fun addAfterMessage(message: Message) {
        if (messageList.isEmpty()) {
            addDividerView(message.time, null)
        } else if (!compareMessageDate(messageList.last().time, message.time)) {
            addDividerView(message.time, null)
        }

        messageList.add(message)
        this.notifyItemInserted(messageList.size - 1)
    }

    fun addBeforeMessage(message: Message) {
        for (i in 0 until messageList.size) {
            if (message.time < messageList[i].time) {
                messageList.add(i, message)
                this.notifyItemInserted(i)

                if (!compareMessageDate(message.time, messageList[i].time)) {    //다음 메시지와 날짜가 다름
                    addDividerView(messageList[i].time, i + 1)
                }
                if (i == 0) {    //날짜는 같으나 첫 번째 메시지이므로 날짜 구분선 추가
                    addDividerView(message.time, 0)
                }
                break
            }
        }
    }

    private fun compareMessageDate(time1: Long, time2: Long): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.format(time1).equals(sdf.format(time2))
    }

    fun addDividerView(time: Long, position: Int?) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)

        if (position == null) {
            messageList.add(Message(cal.timeInMillis))
            this.notifyItemInserted(messageList.size - 1)
        } else {
            messageList.add(position, Message(cal.timeInMillis))
            this.notifyItemInserted(position)
        }
    }

    fun getOldestMessageTime(): Long {
        return if (messageList[0].user == "DIVIDER") {
            messageList[1].time
        } else {
            messageList[0].time
        }
    }
}