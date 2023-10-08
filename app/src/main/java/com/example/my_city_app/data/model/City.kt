package com.example.my_city_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)
