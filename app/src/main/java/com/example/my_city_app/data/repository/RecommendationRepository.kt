package com.example.my_city_app.data.repository

import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.Category
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getAllRecommendationsStream(): Flow<List<Recommendation>>
    fun getRecommendationStream(id: Int): Flow<Recommendation>
    fun searchRecommendationsStream(idCity: Int, category: Category): Flow<List<Recommendation>>
    suspend fun insertRecommendation(recommendation: Recommendation)
    suspend fun deleteRecommendation(recommendation: Recommendation)
    suspend fun updateRecommendation(recommendation: Recommendation)
}