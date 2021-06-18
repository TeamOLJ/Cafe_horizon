package com.teamolj.cafehorizon.newsAndEvents


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.databinding.RecyclerItemEventsBinding


class EventAdapter : RecyclerView.Adapter<EventAdapter.eventsHolder>() {
    internal var eventsList = mutableListOf<Events>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eventsHolder =
        eventsHolder(
            RecyclerItemEventsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun onBindViewHolder(holder: eventsHolder, position: Int) {
        var events = eventsList.get(position)
        holder.setEvent(events)
    }

    override fun getItemCount(): Int = eventsList.size


    inner class eventsHolder(private var binding: RecyclerItemEventsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var events: Events

        init {
            binding.root.setOnClickListener {
                var intent = Intent(binding.root.context, EventsDetailActivity::class.java).apply {
                    putExtra("events", events)
                }

                binding.root.context.startActivity(intent)
            }
        }

        fun setEvent(e: Events) {
            events = e

            binding.imageEvents.setImageURI(Uri.parse(events.imageUri))
            binding.textTitleEvents.text = events.title
            binding.textPeriodEvents.text = events.period
        }
    }
}