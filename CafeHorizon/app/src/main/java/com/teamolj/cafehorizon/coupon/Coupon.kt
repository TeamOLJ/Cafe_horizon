package com.teamolj.cafehorizon.coupon

data class Coupon(
    var couponName:String,
    var expiryDate: Int,
    var discount:Int,
    var isUsed:Boolean,
    var usedDate:Int?=null
) {
}
