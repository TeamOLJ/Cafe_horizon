package com.teamolj.cafehorizon.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.RecyclerItemChatBubbleInBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatBubbleOutBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatPhotoInBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatPhotoOutBinding

class ChatRecyclerAdapter(val user: String) : RecyclerView.Adapter<ChatRecyclerHolder>() {
    var messageList = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerHolder {
        return when (viewType) {
            R.layout.recycler_item_chat_bubble_in -> {
                ChatRecyclerHolder.BubbleInHolder(
                    RecyclerItemChatBubbleInBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            R.layout.recycler_item_chat_photo_in-> {
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
            else -> throw IllegalAccessException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: ChatRecyclerHolder, position: Int) {
        when(holder){
            is ChatRecyclerHolder.BubbleInHolder -> holder.bind(messageList.get(position))
            is ChatRecyclerHolder.PhotoInHolder -> holder.bind(messageList.get(position))
            is ChatRecyclerHolder.BubbleOutHolder -> holder.bind(messageList.get(position))
            is ChatRecyclerHolder.PhotoOutHolder -> holder.bind(messageList.get(position))

        }

    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList.get(position)
        return if (message.user == user) {
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

    fun addNewData(message:Message) {
        messageList.add(message)
        this.notifyDataSetChanged()
    }
}