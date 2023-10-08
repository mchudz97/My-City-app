package com.example.my_city_app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CityWithRecomm(
    @Embedded val city: City,
    @Relation(
        parentColumn = "id",
        entityColumn = "cityId"
    )
    val recommendations: List<Recommendation>
)
