package com.example.my_city_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.data.utils.CategoryConverter
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {
    @Query("Select * from recommendation")
    fun getAll(): Flow<List<Recommendation>>
    @Query("Select * from recommendation where id = :id")
    fun get(id: Int): Flow<Recommendation>
    @TypeConverters(CategoryConverter::class)
    @Query("Select * from recommendation where city_id = :cityId and category = :category")
    fun searchBy(cityId: Int, category: Category): Flow<List<Recommendation>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recommendation: Recommendation)
    @Delete
    suspend fun delete(recommendation: Recommendation)
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(recommendation: Recommendation)
}