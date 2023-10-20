package com.example.my_city_app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "city", indices = [Index(value = ["name"], unique = true)])
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)
