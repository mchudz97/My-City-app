package com.example.my_city_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "recommendation",
    foreignKeys = [ForeignKey(
        entity = City::class,
        parentColumns =["id"],
        childColumns = ["city_id"],
        onDelete = CASCADE
        )]
    )
data class Recommendation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    @ColumnInfo(name = "is_recommended")
    val isRecommended: Boolean,
    @ColumnInfo(name = "city_id")
    val cityId: Int
)
