package com.example.my_city_app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.data.utils.CategoryConverter
import com.example.my_city_app.data.utils.DateConverter
import java.util.Calendar
import java.util.Date

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
    @TypeConverters(CategoryConverter::class)
    val category: Category,
    val rate: Int,
    @ColumnInfo(name = "date_modified")
    val dateModified: Date = Calendar.getInstance().time,
    @ColumnInfo(name = "city_id", index = true)
    val cityId: Int
)
