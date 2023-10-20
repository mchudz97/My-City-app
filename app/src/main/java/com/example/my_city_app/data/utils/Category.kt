package com.example.my_city_app.data.utils


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.my_city_app.R

enum class Category(
    @StringRes val stringResId: Int,
    @DrawableRes val drawableResId: Int
    ) {
    COFFEE_SHOP(R.string.category_coffee_shops, R.drawable.baseline_coffee_24),
    RESTAURANTS(R.string.category_restaurants, R.drawable.baseline_restaurant_24),
    KID_FRIENDLY(R.string.category_kid_friendly_places, R.drawable.baseline_family_restroom_24),
    PARKS(R.string.category_parks, R.drawable.baseline_park_24),
    SHOPPING_CENTERS(R.string.category_shopping_centers, R.drawable.baseline_shopping_cart_24),
    OTHER(R.string.category_other, R.drawable.baseline_other_houses_24)
}