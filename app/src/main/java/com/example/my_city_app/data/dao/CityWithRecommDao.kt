package com.example.my_city_app.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.my_city_app.data.model.CityWithRecomm
import kotlinx.coroutines.flow.Flow

@Dao
interface CityWithRecommDao {
    @Query("Select * from city where id = :id")
    fun get(id: Int): Flow<CityWithRecomm>
}