package com.teamolj.cafehorizon

import android.os.Parcel
import android.os.Parcelable
import com.teamolj.cafehorizon.smartOrder.Cart
import java.util.*

data class Order(
    var orderTitle: String = "",
    var orderTime: Date = Date(),
    var state: String = "",
    var orderMenu: MutableList<Cart> = mutableListOf()):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDate()!!,
        parcel.readString()!!,
        mutableListOf<Cart>().apply {
            parcel.readList(this, Cart::class.java.classLoader)
        }
    ) {
    }

    fun isPickedUp(): Boolean {
        return when (state) {
            "픽업완료" -> true
            else -> false
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderTitle)
        parcel.writeSerializable(orderTime)
        parcel.writeString(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }

        fun Parcel.readDate(): Date? {
            val long = readLong()
            return if (long != -1L) Date(long) else null
        }
    }
}
