package com.teamolj.cafehorizon.smartOrder

import android.util.Log
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface CartDao {
    @Query("SELECT * FROM orm_cart ORDER BY type")
    fun getAllByType(): List<Cart>


    @Query("SELECT * FROM orm_cart WHERE name = :name AND shot = :optionShot AND syrup = :optionSyrup AND whipping = :optionWhipping")
    fun getCafeMenuByPrimaryKey(
        name: String,
        optionShot: Int,
        optionSyrup: Int,
        optionWhipping: Boolean
    ): Cart?


    @Query("SELECT COUNT(*) FROM orm_cart")
    fun getCount(): Int

    @Query("SELECT SUM(amount*each_price) FROM orm_cart")
    fun getTotalAmount():Int


    @Insert(onConflict = REPLACE)
    fun insert(cart: Cart)

    @Update
    fun update(cart:Cart)


    @Query("UPDATE orm_cart SET amount= amount + :amount WHERE name = :name AND shot = :optionShot AND syrup = :optionSyrup AND whipping = :optionWhipping")
    fun updateAmount(
        amount: Int,
        name: String,
        optionShot: Int,
        optionSyrup: Int,
        optionWhipping: Boolean
    )


    fun insertOrUpdate(cart: Cart) {
        val itemFromRoom = getCafeMenuByPrimaryKey(cart.cafeMenuName, cart.optionShot, cart.optionSyrup, cart.optionWhipping)

        if (itemFromRoom == null) {
            insert(cart)
        } else {
            updateAmount(cart.cafeMenuAmount, cart.cafeMenuName, cart.optionShot, cart.optionSyrup, cart.optionWhipping)
        }
    }


    @Delete
    fun delete(cart: Cart)
}