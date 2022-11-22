package com.example.BudgetTracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transact::class), version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}