package com.example.my_city_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.my_city_app.data.model.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("Select * from city")
    fun getAll(): Flow<List<City>>
    @Query("Select * from city where city.id = :id")
    fun get(id: Int): Flow<City>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: City)
    @Delete
    suspend fun delete(city: City)
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(city: City)
}