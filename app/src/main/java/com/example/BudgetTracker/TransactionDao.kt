package com.example.BudgetTracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Query("SELECT * from transactions")
    fun getAll():List<Transact>

    @Insert
    fun insertAll(vararg transact: Transact)

    @Delete
    fun delete(transact: Transact)

    @Update
    fun update(vararg transact: Transact)
}