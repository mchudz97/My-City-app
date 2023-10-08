package com.example.my_city_app.data.utils

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name
    @TypeConverter
    fun toCategory(category: String): Category = enumValueOf(category)
}