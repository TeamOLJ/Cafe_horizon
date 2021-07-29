package com.teamolj.cafehorizon.newsAndEvents

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Events(
    var title: String,
    var imageUri: String,
    var period: String
) :Serializable