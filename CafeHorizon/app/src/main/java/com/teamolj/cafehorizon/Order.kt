package com.teamolj.cafehorizon

import android.os.Parcel
import android.os.Parcelable
import com.teamolj.cafehorizon.smartOrder.MenuInfo
import java.util.*

data class Order(
    var orderTitle: String = "",
    var orderTime: Date = Date(),
    var state: String = "",
    var couponPath: String = "",
    var orderMenuList: MutableList<MenuInfo> = mutableListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readDate(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        mutableListOf<MenuInfo>().apply {
            parcel.readList(this, MenuInfo::class.java.classLoader)
        }
    ) {
    }

    fun isPickedup(): Boolean {
        return when (state) {
            "픽업완료" -> true
            else -> false
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderTitle)
        parcel.writeLong(orderTime.time)
        parcel.writeString(state)
        parcel.writeString(couponPath)
        parcel.writeList(orderMenuList)
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

        fun Parcel.readDate(): Date {
            val long = readLong()
            return Date(long)
        }
    }

}
