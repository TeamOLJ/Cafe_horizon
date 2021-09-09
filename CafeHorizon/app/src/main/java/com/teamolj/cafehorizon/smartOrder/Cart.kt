package com.teamolj.cafehorizon.smartOrder

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "orm_cart",
    primaryKeys = ["name", "shot", "syrup", "whipping"],
    indices = [Index(value = ["type"])]
)
data class Cart(
    @ColumnInfo(name = "name") var cafeMenuName: String,
    @ColumnInfo(name = "type") var menuType: Int,
    @ColumnInfo(name = "shot") var optionShot: Int,
    @ColumnInfo(name = "syrup") var optionSyrup: Int,
    @ColumnInfo(name = "whipping") var optionWhipping: Boolean,
    @ColumnInfo(name = "each_price") var eachPrice: Int,
    @ColumnInfo(name = "amount") var cafeMenuAmount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun toString(): String {
        return "$cafeMenuName | $menuType | $optionShot | $optionSyrup | $optionWhipping | $eachPrice | $cafeMenuAmount"
    }

    constructor() : this("", -1, 0, 0, true, 0, 1)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cafeMenuName)
        parcel.writeInt(menuType)
        parcel.writeInt(optionShot)
        parcel.writeInt(optionSyrup)
        parcel.writeByte(if (optionWhipping) 1 else 0)
        parcel.writeInt(eachPrice)
        parcel.writeInt(cafeMenuAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}