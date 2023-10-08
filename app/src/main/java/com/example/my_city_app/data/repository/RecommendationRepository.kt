package com.example.my_city_app.data.repository

import com.example.my_city_app.data.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getAllRecommendationsStream(): Flow<List<Recommendation>>
    fun getRecommendationStream(id: Int): Flow<Recommendation>
    suspend fun insertRecommendation(recommendation: Recommendation)
    suspend fun deleteRecommendation(recommendation: Recommendation)
    suspend fun updateRecommendation(recommendation: Recommendation)
}