package com.teamolj.cafehorizon.coupon

data class Coupon(
    var couponPath: String,
    var couponName: String,
    var expiryDate: Long,
    var discount: Int,
    var isUsed: Boolean,
    var usedDate: Long? = null
) {
    override fun toString(): String {
        if (discount == 0) {
            return "${couponName}"
        } else {
            return "${couponName} (${discount}원)"
        }
    }
}
