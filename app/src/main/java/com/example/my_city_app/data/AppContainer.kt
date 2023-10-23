package com.example.my_city_app.data

import android.content.Context
import com.example.my_city_app.data.repository.CityRepository
import com.example.my_city_app.data.repository.OfflineCityRepository
import com.example.my_city_app.data.repository.OfflineRecommendationRepository
import com.example.my_city_app.data.repository.RecommendationRepository

interface AppContainer {
    val cityRepository: CityRepository
    val recommendationRepository: RecommendationRepository
}

class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val cityRepository: CityRepository by lazy {
        OfflineCityRepository(
            MyCityDatabase.getDatabase(context).cityDao()
        )
    }
    override val recommendationRepository: RecommendationRepository by lazy {
        OfflineRecommendationRepository(
            MyCityDatabase.getDatabase(context).recommendationDao()
        )
    }
}