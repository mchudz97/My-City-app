package com.example.my_city_app.data.repository

import com.example.my_city_app.data.model.CityWithRecomm
import kotlinx.coroutines.flow.Flow

interface CityWithRecommRepository {
    fun getCityWithRecommStream(id: Int): Flow<CityWithRecomm>
}