package com.example.my_city_app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.data.utils.Converters

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
    @TypeConverters(Converters::class)
    val category: Category,
    val rate: Int,
    @ColumnInfo(name = "city_id")
    val cityId: Int
)
