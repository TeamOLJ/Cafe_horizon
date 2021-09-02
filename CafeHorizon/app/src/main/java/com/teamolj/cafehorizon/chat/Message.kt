package com.teamolj.cafehorizon.chat

import java.text.SimpleDateFormat

data class Message(
    var user:String,
    var time:Long,
    var contentText:String?,
    var photoUrl:String?,
    var readState:Boolean
){
    constructor() : this("", 0, null, null, false) {}

    fun timeToString():String {
        return SimpleDateFormat("HH:mm").format(time)
    }

    fun getReadStateAsString():String {
        return if (readState) "읽음" else "읽지않음"
    }
}
