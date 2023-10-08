package com.example.my_city_app.data.repository

import com.example.my_city_app.data.dao.CityDao
import com.example.my_city_app.data.model.City
import kotlinx.coroutines.flow.Flow

class OfflineCityRepository(private val dao: CityDao) : CityRepository {
    override fun getAllCitiesStream(): Flow<List<City>> = dao.getAll()

    override fun getCityStream(id: Int): Flow<City> = dao.get(id)

    override suspend fun insertCity(city: City) = dao.insert(city)

    override suspend fun deleteCity(city: City) = dao.delete(city)

    override suspend fun updateCity(city: City) = dao.update(city)

}