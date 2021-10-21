package com.teamolj.cafehorizon.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.R
import com.teamolj.cafehorizon.databinding.*
import java.text.SimpleDateFormat

sealed class ChatRecyclerHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class BubbleInHolder(private val binding: RecyclerItemChatBubbleInBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            binding.textChat.text = message.contentText
            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()

            binding.textChat.setOnLongClickListener {
                val clipboard = binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip:ClipData = ClipData.newPlainText("chat message", binding.textChat.text.toString())
                Toast.makeText(binding.root.context, "텍스트가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                clipboard.setPrimaryClip(clip)
                true; }
        }
    }

    class BubbleOutHolder(private val binding: RecyclerItemChatBubbleOutBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            binding.textChat.text = message.contentText
            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()

            binding.textChat.setOnLongClickListener {
                val clipboard = binding.root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip:ClipData = ClipData.newPlainText("chat message", binding.textChat.text.toString())
                Toast.makeText(binding.root.context, "텍스트가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                clipboard.setPrimaryClip(clip)
                true; }
        }
    }

    class PhotoInHolder(private val binding: RecyclerItemChatPhotoInBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(message: Message) {
            Glide.with(binding.root)
                .load(message.photoUrl)
                .thumbnail(0.5f)
                .into(binding.photoChat)

            binding.photoChat.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoDetailActivity::class.java)
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
                .thumbnail(0.5f)
                .into(binding.photoChat)

            binding.photoChat.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoDetailActivity::class.java)
                intent.putExtra("photoUrl", message.photoUrl)
                binding.root.context.startActivity(intent)
            }

            binding.textSendTime.text = message.timeToString()
            binding.textReadState.text = message.readStateToString()
        }
    }

    class DateDividerHolder(private val binding: RecyclerItemChatDateDividerBinding) :
        ChatRecyclerHolder(binding) {
        fun bind(date: Long) {
            binding.textChatDate.text = SimpleDateFormat("yyyy년 MM월 dd일 EEEE").format(date)
        }
    }
}
