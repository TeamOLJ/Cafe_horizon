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
    constructor(time:Long):this("DIVIDER", time, null, null, false) {}

    fun timeToString():String {
        return SimpleDateFormat("a hh:mm").format(time)
    }

    fun readStateToString():String {
        return if (readState) "읽음" else ""
    }

    override fun toString(): String {
        return "${user} : ${contentText} - ${SimpleDateFormat("HH:mm:ss").format(time)}"
    }
}
