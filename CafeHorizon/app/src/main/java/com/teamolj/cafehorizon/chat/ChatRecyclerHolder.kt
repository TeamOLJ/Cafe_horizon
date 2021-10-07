package com.teamolj.cafehorizon.chat

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.databinding.RecyclerItemChatBubbleInBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatBubbleOutBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatPhotoInBinding
import com.teamolj.cafehorizon.databinding.RecyclerItemChatPhotoOutBinding

sealed class ChatRecyclerHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class BubbleInHolder(private val binding: RecyclerItemChatBubbleInBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
                binding.textChat.text = message.contentText
                binding.textSendTime.text = message.timeToString()
                binding.textReadState.text = message.readStateToString()
        }
    }

    class BubbleOutHolder(private val binding: RecyclerItemChatBubbleOutBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            binding.textChat.text = message.contentText
            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()
        }
    }

    class PhotoInHolder(private val binding: RecyclerItemChatPhotoInBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            Glide.with(binding.root)
                .load(message.photoUrl)
                .into(binding.imageChat)

            binding.imageChat.setOnClickListener {
                val intent = Intent(binding.root.context, ImageDetailActivity::class.java)
                intent.putExtra("photoUrl", message.photoUrl)
                binding.root.context.startActivity(intent)
            }

            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()
        }
    }

    class PhotoOutHolder(private val binding: RecyclerItemChatPhotoOutBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            Glide.with(binding.root)
                .load(message.photoUrl)
                .into(binding.imageChat)

            binding.imageChat.setOnClickListener {
                val intent = Intent(binding.root.context, ImageDetailActivity::class.java)
                intent.putExtra("photoUrl", message.photoUrl)
                binding.root.context.startActivity(intent)
            }

            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()
        }
    }
}
