package com.example.my_city_app.utils


sealed interface ValidationError {
    data object ValidationErrorTitle : ValidationError
    data object ValidationErrorRate : ValidationError
    data object ValidationErrorDescription : ValidationError
}


