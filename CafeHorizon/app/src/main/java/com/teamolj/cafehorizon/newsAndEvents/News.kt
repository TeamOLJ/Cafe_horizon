package com.teamolj.cafehorizon.newsAndEvents

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class News(
    var title: String,
    var content: String,
    var date: String
) : Serializable {
    override fun toString(): String {
        return "$title | $date | $content"
    }
}

