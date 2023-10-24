package com.example.my_city_app.ui.viewmodels

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
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.repository.CityRepository
import com.example.my_city_app.data.repository.RecommendationRepository
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.screens.CATEGORY
import com.example.my_city_app.ui.screens.CITY
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


data class RecommUiState(
    val city: City,
    val category: Category,
    val recommendations: List<Recommendation>
)

class RecommScreenViewModel(
    savedStateHandle: SavedStateHandle,
    recommendationRepository: RecommendationRepository,
    cityRepository: CityRepository
) : ViewModel() {

    val recommUiState: StateFlow<RecommUiState> = recommendationRepository
        .searchRecommendationsStream(
            checkNotNull(savedStateHandle[CITY]),
            checkNotNull(savedStateHandle[CATEGORY])
        ).map {
            RecommUiState(
                city = cityRepository.getCityStream(checkNotNull(savedStateHandle[CITY])).first(),
                category = checkNotNull(savedStateHandle[CATEGORY]),
                recommendations = it
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecommUiState(City(name = ""), Category.OTHER, emptyList())
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application: MyCityApplication = (this[APPLICATION_KEY] as MyCityApplication)
                RecommScreenViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    recommendationRepository = application.appContainer.recommendationRepository,
                    cityRepository = application.appContainer.cityRepository
                )
            }
        }
    }
}