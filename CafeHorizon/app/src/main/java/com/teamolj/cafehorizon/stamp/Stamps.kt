package com.teamolj.cafehorizon.stamp

import android.os.Parcel
import android.os.Parcelable

data class Stamps (
    val type: String,
    val date: String,
    val validity : String
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
        parcel.writeString(validity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stamps> {
        override fun createFromParcel(parcel: Parcel): Stamps {
            return Stamps(parcel)
        }

        override fun newArray(size: Int): Array<Stamps?> {
            return arrayOfNulls(size)
        }
    }


}

