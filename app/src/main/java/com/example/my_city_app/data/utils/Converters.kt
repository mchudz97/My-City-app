package com.example.my_city_app.data.utils

import androidx.room.TypeConverter
import java.util.Date

class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name
    @TypeConverter
    fun toCategory(category: String): Category = enumValueOf(category)
}

class DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long) = Date(dateLong)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time

}