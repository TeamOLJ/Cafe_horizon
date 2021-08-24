package com.teamolj.cafehorizon.stamp

import com.google.firebase.Timestamp
import java.util.*

data class Stamp(
    val stampSize: Int = 0,
    val earnedDate: Timestamp = Timestamp(Date())
)
