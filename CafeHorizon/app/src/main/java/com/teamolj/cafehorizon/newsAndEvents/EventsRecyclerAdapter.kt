package com.teamolj.cafehorizon.newsAndEvents


import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.recycler_item_events.view.*

internal data class Events(
    var title: String,
    var imageUri: Uri,
    var period: String
)

class EventAdapter : RecyclerView.Adapter<eventsHolder>() {
    internal var eventsList = mutableListOf<Events>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eventsHolder {
        return eventsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_events, parent, false)
        )
    }


    override fun onBindViewHolder(holder: eventsHolder, position: Int) {
        var events = eventsList.get(position)
        holder.setEvent(events)
    }

    override fun getItemCount(): Int = eventsList.size

}

class eventsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal lateinit var events: Events

    init {
        itemView.setOnClickListener {
            Toast.makeText(itemView.context, "테스트 : " + events.title.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    internal fun setEvent(e: Events) {
        events = e

        itemView.imageEvents.setImageURI(events.imageUri)
        itemView.titleEvents.text = events.title
        itemView.periodEvents.text = events.period
    }
}