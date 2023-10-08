package com.example.my_city_app.data.repository

import com.example.my_city_app.data.dao.CityWithRecommDao
import com.example.my_city_app.data.model.CityWithRecomm
import kotlinx.coroutines.flow.Flow

class OfflineCityWithRecommRepository(
    private val dao: CityWithRecommDao
) : CityWithRecommRepository {
    override fun getCityWithRecommStream(id: Int): Flow<CityWithRecomm> = dao.get(id)
}