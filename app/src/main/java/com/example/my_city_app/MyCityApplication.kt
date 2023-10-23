package com.example.my_city_app

import android.app.Application
import com.example.my_city_app.data.AppContainer
import com.example.my_city_app.data.AppDataContainer

class MyCityApplication: Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppDataContainer(this)
    }
}