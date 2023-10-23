package com.example.my_city_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.my_city_app.data.dao.CityDao
import com.example.my_city_app.data.dao.RecommendationDao
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.DateConverter

@Database(
    entities = [City::class, Recommendation::class],
    version = 1,
    exportSchema = true,

)
@TypeConverters(DateConverter::class)
abstract class MyCityDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun recommendationDao(): RecommendationDao

    companion object {
        @Volatile
        private var Instance: MyCityDatabase? = null
        fun getDatabase(context: Context): MyCityDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MyCityDatabase::class.java, "my_city"
                )
                    .build().also { Instance = it }
            }
        }

    }
}