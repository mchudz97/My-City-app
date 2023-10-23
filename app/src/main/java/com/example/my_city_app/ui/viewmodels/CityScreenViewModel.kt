package com.example.my_city_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.my_city_app.MyCityApplication
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.repository.CityRepository
import com.example.my_city_app.utils.TextFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CityScreenUiState{
    data class Default(val cities: List<City>) : CityScreenUiState
    data class Focusing(val cities: List<City>, val focusedCity: City) : CityScreenUiState
    data class Creating(val cities: List<City>, val city: City) : CityScreenUiState
    data class Editing(val cities: List<City>, val city: City) : CityScreenUiState
}
class CityScreenViewModel(
    private val cityRepository: CityRepository
) : ViewModel() {
    val uiState: MutableStateFlow<CityScreenUiState> = MutableStateFlow(
        CityScreenUiState.Default(emptyList())
    )
    init{
        toListView()
    }
    fun toListView() {
        viewModelScope.launch {
            cityRepository.getAllCitiesStream().collect{
                uiState.value = CityScreenUiState.Default(it)
            }
        }
    }
    fun beginCreating() {
        val state = uiState.value as CityScreenUiState.Default
        uiState.value = CityScreenUiState.Creating(cities = state.cities, city = City(name = ""))
    }
    suspend fun finishCreating() {
        val state = uiState.value as CityScreenUiState.Creating
        holdDraft(TextFormat.removeUnexpectedSpaces(state.city.name))
        cityRepository.insertCity((uiState.value as CityScreenUiState.Creating).city)
        toListView()
    }
    fun beginEditing(city: City) {
        val state = uiState.value as CityScreenUiState.Focusing
        uiState.value = CityScreenUiState.Editing(cities = state.cities, city = city)
    }
    suspend fun finishEditing() {
        val state = uiState.value
        holdDraft(
            TextFormat.removeUnexpectedSpaces(
                (state as CityScreenUiState.Editing).city.name
            )
        )
        updateCity()
        toListView()
    }
    fun focus(city: City) {
        val state = uiState.value as CityScreenUiState.Default
        uiState.value = CityScreenUiState.Focusing(state.cities, city)
    }
    fun holdDraft(name: String) {
        when(uiState.value) {
            is CityScreenUiState.Creating -> uiState.update {
                (it as CityScreenUiState.Creating).copy(
                    city = it.city.copy(name = name)
                )
            }
            is CityScreenUiState.Editing -> uiState.update {
                (it as CityScreenUiState.Editing).copy(
                    city = it.city.copy(name = name)
                )
            }
            is CityScreenUiState.Default -> {}
            is CityScreenUiState.Focusing -> {}
        }
    }
    suspend fun removeCity(city: City) {
        if(uiState.value is CityScreenUiState.Focusing)
            cityRepository.deleteCity(city)
    }
    suspend fun updateCity() {
        val state = uiState.value
        if(state is CityScreenUiState.Editing)
            cityRepository.updateCity(state.city)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MyCityApplication)
                CityScreenViewModel(
                    application.appContainer.cityRepository
                )
            }
        }
    }
}