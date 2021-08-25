package com.teamolj.cafehorizon

import com.teamolj.cafehorizon.smartOrder.Cart

data class Order(
    var orderTime:String,
    var orderMenu:List<Cart>,
    var state:String) {

    fun getStateBoolean():Boolean{
        return when(state) {
            "픽업완료" -> true
            else -> false
        }
    }
}
