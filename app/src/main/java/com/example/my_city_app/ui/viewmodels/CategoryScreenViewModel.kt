package com.example.my_city_app.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.my_city_app.MyCityApplication
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.repository.CityRepository
import com.example.my_city_app.ui.screens.CITY
import kotlinx.coroutines.launch


class CategoryScreenViewModel(
    savedStateHandle: SavedStateHandle,
    cityRepository: CityRepository
) : ViewModel() {
    val city = mutableStateOf(City(name = ""))

    init {
        viewModelScope.launch {
            val cityId: Int = checkNotNull(savedStateHandle[CITY])
            cityRepository.getCityStream(cityId).collect{
                city.value = it
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[APPLICATION_KEY] as MyCityApplication)
                CategoryScreenViewModel(
                    savedStateHandle = this.createSavedStateHandle(),
                    cityRepository = application.appContainer.cityRepository
                )
            }
        }
    }
}