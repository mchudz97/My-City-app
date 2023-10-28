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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

sealed interface CityScreenUiState{
    data class Default(val cities: List<City>) : CityScreenUiState
    data class Focusing(val cities: List<City>, val focusedCity: City) : CityScreenUiState
    data class Creating(val cities: List<City>, val city: City) : CityScreenUiState
    data class Editing(val cities: List<City>, val city: City) : CityScreenUiState
}
class CityScreenViewModel(
    private val cityRepository: CityRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<CityScreenUiState> = MutableStateFlow(
        CityScreenUiState.Default(emptyList())
    )
    val uiState: StateFlow<CityScreenUiState> = _uiState.asStateFlow()
    init{
        toListView()
    }
    fun toListView() {
        viewModelScope.launch {
            cityRepository.getAllCitiesStream().collect{
                _uiState.value = CityScreenUiState.Default(it)
            }
        }
    }
    fun beginCreating() {
        val state = _uiState.value as CityScreenUiState.Default
        _uiState.value = CityScreenUiState.Creating(cities = state.cities, city = City(name = ""))
    }
    suspend fun finishCreating() {
        val state = _uiState.value as CityScreenUiState.Creating
        holdDraft(TextFormat.removeUnexpectedSpaces(state.city.name))
        if((_uiState.value as CityScreenUiState.Creating).city.name.isEmpty())
            throw IllegalArgumentException("City Name cannot be empty.")
        cityRepository.insertCity((_uiState.value as CityScreenUiState.Creating).city)
        toListView()
    }
    fun beginEditing(city: City) {
        val state = _uiState.value as CityScreenUiState.Focusing
        _uiState.value = CityScreenUiState.Editing(cities = state.cities, city = city)
    }
    suspend fun finishEditing() {
        val state = _uiState.value as CityScreenUiState.Editing
        holdDraft(
            TextFormat.removeUnexpectedSpaces(
                state.city.name
            )
        )
        if(state.city.name.isEmpty())
            throw IllegalArgumentException("City Name cannot be empty.")
        updateCity()
        toListView()
    }
    fun focus(city: City) {
        when(val state = _uiState.value) {
            is CityScreenUiState.Default -> {
                _uiState.value = CityScreenUiState.Focusing(state.cities, city)
            }
            is CityScreenUiState.Focusing -> {
                _uiState.value = CityScreenUiState.Focusing(state.cities, city)
            }
            is CityScreenUiState.Editing -> { }
            is CityScreenUiState.Creating -> { }
        }
    }
    fun holdDraft(name: String) {
        when(uiState.value) {
            is CityScreenUiState.Creating -> _uiState.update {
                (it as CityScreenUiState.Creating).copy(
                    city = it.city.copy(name = name)
                )
            }
            is CityScreenUiState.Editing -> _uiState.update {
                (it as CityScreenUiState.Editing).copy(
                    city = it.city.copy(name = name)
                )
            }
            is CityScreenUiState.Default -> {}
            is CityScreenUiState.Focusing -> {}
        }
    }
    suspend fun removeCity(city: City) {
        if(_uiState.value is CityScreenUiState.Focusing)
            cityRepository.deleteCity(city)
    }
    private suspend fun updateCity() {
        val state = _uiState.value
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