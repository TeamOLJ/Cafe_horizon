package com.teamolj.cafehorizon.smartOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.databinding.ActivityMenuDetailBinding

class MenuDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMenuDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //https://calvinjmkim.tistory.com/50    나중에 Glide로 이미지 동그랗게 잡아주기
    }
}