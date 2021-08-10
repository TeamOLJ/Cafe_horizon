package com.teamolj.cafehorizon

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.teamolj.cafehorizon.databinding.ActivityChattingBinding
import com.teamolj.cafehorizon.databinding.ViewChatBubbleInBinding
import java.text.SimpleDateFormat

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAppBar = binding.toolbar
        topAppBar.setNavigationIcon(R.drawable.btn_back)
        topAppBar.setBackgroundColor(255)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        val enable = false

        if (enable) {
            binding.editTextChat.keyListener = null
            binding.editTextChat.setText(R.string.text_chatting_not_enable)
            binding.editTextChat.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.editTextChat.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.btnSendChat.isEnabled = false
        }


        binding.btnSendChat.setOnClickListener {
            val view = ViewChatBubbleInBinding.inflate(layoutInflater)
            view.textChat.text = binding.editTextChat.text.toString()
            view.textSendTime.text = SimpleDateFormat("HH:mm").format(System.currentTimeMillis())
            view.textReadState.text = "안 읽음"

            binding.layoutChats.addView(view as View)
        }
    }
}