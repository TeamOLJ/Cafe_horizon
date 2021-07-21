package com.teamolj.cafehorizon.coupon

import android.os.Parcel
import android.os.Parcelable

data class Coupons (
    val type: String,
    val date: String,
    val status : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(date)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coupons> {
        override fun createFromParcel(parcel: Parcel): Coupons {
            return Coupons(parcel)
        }

        override fun newArray(size: Int): Array<Coupons?> {
            return arrayOfNulls(size)
        }
    }


}

