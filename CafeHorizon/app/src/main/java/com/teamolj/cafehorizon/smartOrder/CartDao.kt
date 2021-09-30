package com.teamolj.cafehorizon.smartOrder

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface CartDao {
    @Query("SELECT * FROM orm_cart ORDER BY category")
    fun getAllByCategory():List<MenuInfo>

    @Query("SELECT * FROM orm_cart WHERE name = :name AND shot = :optionShot AND syrup = :optionSyrup AND whipping = :optionWhipping")
    fun getCafeMenuByPrimaryKey(
        name: String,
        optionShot: Int,
        optionSyrup: Int,
        optionWhipping: Boolean
    ): MenuInfo?


    @Query("SELECT COUNT(*) FROM orm_cart")
    fun getCount(): Int

    @Query("SELECT SUM(amount*price) FROM orm_cart")
    fun getTotalAmount():Int


    @Insert(onConflict = REPLACE)
    fun insert(menuInfo: MenuInfo)

    @Update
    fun update(menuInfo: MenuInfo)


    @Query("UPDATE orm_cart SET amount= amount + :amount WHERE name = :name AND shot = :optionShot AND syrup = :optionSyrup AND whipping = :optionWhipping")
    fun updateAmount(
        amount: Int,
        name: String,
        optionShot: Int,
        optionSyrup: Int,
        optionWhipping: Boolean
    )


    fun insertOrUpdate(menuInfo: MenuInfo) {
        val itemFromRoom = getCafeMenuByPrimaryKey(menuInfo.name, menuInfo.optionShot, menuInfo.optionSyrup, menuInfo.optionWhipping)

        if (itemFromRoom == null) {
            insert(menuInfo)
        } else {
            updateAmount(menuInfo.amount, menuInfo.name, menuInfo.optionShot, menuInfo.optionSyrup, menuInfo.optionWhipping)
        }
    }


    @Delete
    fun delete(menuInfo:MenuInfo)

    @Query("DELETE FROM orm_cart")
    fun deleteAll()
}