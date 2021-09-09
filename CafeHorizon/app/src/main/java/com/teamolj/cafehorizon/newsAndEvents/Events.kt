package com.teamolj.cafehorizon.newsAndEvents

import java.io.Serializable

data class Events(
    var title: String,
    var imageUri: String,
    var period: String
) :Serializable