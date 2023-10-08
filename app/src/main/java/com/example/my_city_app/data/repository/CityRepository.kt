package com.example.my_city_app.data.repository

import com.example.my_city_app.data.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getAllCitiesStream(): Flow<List<City>>
    fun getCityStream(id: Int): Flow<City>
    suspend fun insertCity(city: City)
    suspend fun deleteCity(city: City)
    suspend fun updateCity(city: City)
}