package com.example.my_city_app.data.repository

import com.example.my_city_app.data.dao.RecommendationDao
import com.example.my_city_app.data.model.Recommendation
import kotlinx.coroutines.flow.Flow

class OfflineRecommendationRepository(
    private val dao: RecommendationDao
) : RecommendationRepository {
    override fun getAllRecommendationsStream(): Flow<List<Recommendation>> = dao.getAll()

    override fun getRecommendationStream(id: Int): Flow<Recommendation> = dao.get(id)

    override suspend fun insertRecommendation(recommendation: Recommendation) = dao
        .insert(recommendation)

    override suspend fun deleteRecommendation(recommendation: Recommendation) = dao
        .delete(recommendation)

    override suspend fun updateRecommendation(recommendation: Recommendation) = dao
        .update(recommendation)
}