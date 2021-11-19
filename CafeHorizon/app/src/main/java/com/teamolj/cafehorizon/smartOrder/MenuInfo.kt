package com.teamolj.cafehorizon.smartOrder

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "orm_cart",
    primaryKeys = ["name", "shot", "syrup", "whipping"],
    indices = [Index(value = ["category"])]
)
class MenuInfo(
    @ColumnInfo(name = "name") var name: String,
    var description: String,
    @ColumnInfo(name = "image") var imageUrl: String,
    @ColumnInfo(name = "price") var price: Int,
    @ColumnInfo(name = "category") var category: Int,
    var optionType: Int,
    @ColumnInfo(name = "amount") var amount: Int,
    @ColumnInfo(name = "shot") var optionShot: Int,
    @ColumnInfo(name = "syrup") var optionSyrup: Int,
    @ColumnInfo(name = "whipping") var optionWhipping: Boolean,
) : Parcelable {

    constructor() : this("", "", "", 0, -1, 0, 1, 0, 0, true)

    constructor(
        name: String,
        description: String,
        imageUrl: String,
        price: Int,
        category: Int,
        type: Int,
    ) : this(name, description, imageUrl, price, category, type, 1, 0, 0, true)

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeInt(price)
        parcel.writeInt(category)
        parcel.writeInt(optionType)
        parcel.writeInt(amount)
        parcel.writeInt(optionShot)
        parcel.writeInt(optionSyrup)
        parcel.writeByte(if (optionWhipping) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuInfo> {
        override fun createFromParcel(parcel: Parcel): MenuInfo {
            return MenuInfo(parcel)
        }

        override fun newArray(size: Int): Array<MenuInfo?> {
            return arrayOfNulls(size)
        }
    }
}