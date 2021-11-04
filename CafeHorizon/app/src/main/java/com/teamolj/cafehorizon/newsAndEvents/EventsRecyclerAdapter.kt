package com.teamolj.cafehorizon.newsAndEvents

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamolj.cafehorizon.databinding.RecyclerItemEventsBinding
import java.text.SimpleDateFormat

class EventsRecyclerAdapter : RecyclerView.Adapter<EventsRecyclerAdapter.eventsHolder>() {
    internal var eventsList = mutableListOf<Events>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eventsHolder =
        eventsHolder(
            RecyclerItemEventsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: eventsHolder, position: Int) {
        holder.setEvent(eventsList[position])
    }

    override fun getItemCount(): Int = eventsList.size

    inner class eventsHolder(private var binding: RecyclerItemEventsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setEvent(events: Events) {
            Glide.with(binding.root).load(events.contentUrl).into(binding.imageEvents)

            binding.textTitleEvents.text = if (System.currentTimeMillis() < events.endDate) {
                events.title
            } else events.title + " (종료)"

            binding.textPeriodEvents.text =
                "${SimpleDateFormat("yyyy-MM-dd").format(events.startDate)} ~ ${SimpleDateFormat("yyyy-MM-dd").format(events.endDate)}"

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, EventsDetailActivity::class.java)
                intent.putExtra("events", events)

                binding.root.context.startActivity(intent)
            }
        }
    }
}