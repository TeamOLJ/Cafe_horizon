package com.teamolj.cafehorizon.newsAndEvents

import java.io.Serializable

data class Events(
    var title: String,
    var contentUrl: String,
    var startDate: Long,
    var endDate: Long
) : Serializable