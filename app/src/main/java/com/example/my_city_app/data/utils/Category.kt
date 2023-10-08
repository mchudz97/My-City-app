package com.example.my_city_app.data.utils

import com.example.my_city_app.R

enum class Category(val idResource: Int) {
    COFFEE_SHOP(R.string.category_coffee_shops),
    RESTAURANTS(R.string.category_restaurants),
    KID_FRIENDLY(R.string.category_kid_friendly_places),
    PARKS(R.string.category_parks),
    SHOPPING_CENTERS(R.string.category_shopping_centers),
    OTHER(R.string.category_other)
}