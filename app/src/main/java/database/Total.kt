package com.example.lab_week_10.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Create an Entity with a table name of "total"
@Entity(tableName = "total")
data class Total(
    // @PrimaryKey sets a column into a primary key
    @PrimaryKey(autoGenerate = true)
    // @ColumnInfo sets the name of the column
    @ColumnInfo(name = "id")
    val id: Long = 0,

    // Here we set another column to store the total value
    @ColumnInfo(name = "total")
    val total: Int = 0
)
