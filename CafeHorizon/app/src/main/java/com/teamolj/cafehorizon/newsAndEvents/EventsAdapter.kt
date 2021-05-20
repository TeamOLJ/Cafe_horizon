package com.teamolj.cafehorizon.newsAndEvents


import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.teamolj.cafehorizon.R
import kotlinx.android.synthetic.main.events_item_recycler.view.*

internal data class Events(
    var title: String,
    var imageUri: Uri,
    var period: String
)

class EventAdapter : RecyclerView.Adapter<eventsHolder>() {
    internal var eventsList = mutableListOf<Events>()
    private lateinit var viewGroup:ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eventsHolder {
        viewGroup = parent
        return eventsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.events_item_recycler, parent, false)
        )
    }


    override fun onBindViewHolder(holder: eventsHolder, position: Int) {
        var events = eventsList.get(position)
        holder.setEvent(events, viewGroup)
    }

    override fun getItemCount(): Int = eventsList.size

}

class eventsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal lateinit var events: Events
    private lateinit var viewGroup:ViewGroup

    init {
        itemView.setOnClickListener {
            Toast.makeText(itemView.context, "테스트 : " + events.title.toString(), Toast.LENGTH_SHORT).show()

            val fragmentActivity = viewGroup.context as FragmentActivity
            fragmentActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.eventsContainer, EventsDetailFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    internal fun setEvent(e: Events, vg:ViewGroup) {
        events = e
        viewGroup = vg

        itemView.eventImage.setImageURI(events.imageUri)
        itemView.eventTitle.text = events.title
        itemView.eventPeriod.text = events.period
    }
}