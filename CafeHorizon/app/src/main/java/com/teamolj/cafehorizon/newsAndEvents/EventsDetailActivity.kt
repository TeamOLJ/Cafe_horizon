package com.teamolj.cafehorizon.newsAndEvents

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamolj.cafehorizon.databinding.ActivityEventsDetailBinding

class EventsDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEventsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val events: Events = intent.getSerializableExtra("events") as Events

        binding.imageEvents.setImageURI(Uri.parse(events.imageUri))
    }
}