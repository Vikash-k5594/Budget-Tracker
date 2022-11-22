package com.example.BudgetTracker

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class Transact(
    @PrimaryKey(autoGenerate = true)val id:Int,
    val label:String,
    val amount:Double,
    val description:String):java.io.Serializable {
}