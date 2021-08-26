package com.teamolj.cafehorizon.smartOrder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

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
) : Serializable {
    override fun toString(): String {
        return "$cafeMenuName | $menuType | $optionShot | $optionSyrup | $optionWhipping | $eachPrice | $cafeMenuAmount"
    }

    constructor() : this("", -1, 0, 0, true, 0, 1)
}