package com.teamolj.cafehorizon.notice

data class Notice(
    var context: String,
    var time: Long
) {
    constructor():this("", 0) {}
}