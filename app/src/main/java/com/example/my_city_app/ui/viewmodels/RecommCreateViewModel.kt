package com.example.my_city_app.ui.viewmodels

import androidx.compose.runtime.MutableState
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
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.repository.CityRepository
import com.example.my_city_app.data.repository.RecommendationRepository
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.screens.CATEGORY
import com.example.my_city_app.ui.screens.CITY
import com.example.my_city_app.utils.ValidationError
import kotlinx.coroutines.launch
import java.util.Calendar

data class RecommCreateUiState(
    val recommendationDraft: Recommendation,
    val city: City,
    val category: Category
)

class RecommCreateViewModel(
    savedStateHandle: SavedStateHandle,
    cityRepository: CityRepository,
    val recommendationRepository: RecommendationRepository
) : ViewModel() {
    private val cityId: Int = checkNotNull(savedStateHandle[CITY])
    private val category: Category = checkNotNull(savedStateHandle[CATEGORY])
    val city: MutableState<City> = mutableStateOf(City(name = ""))
    val uiState: MutableState<RecommCreateUiState> = mutableStateOf(
        RecommCreateUiState(
            Recommendation(
                title = "",
                description = "",
                rate = 0,
                category = category,
                cityId = cityId
            ),
            City(name = ""),
            category = category
        )

    )
    init{
        viewModelScope.launch {
            cityRepository.getCityStream(checkNotNull(savedStateHandle[CITY])).collect{
                uiState.value = uiState.value.copy(
                    city = it
                )
            }
        }
    }
    fun updateTitle(title: String) {
        uiState.value = uiState.value.copy(
            recommendationDraft = uiState.value.recommendationDraft.copy(title = title)
        )
    }
    fun updateRate(rate: Int) {
        uiState.value = uiState.value.copy(
            recommendationDraft = uiState.value.recommendationDraft.copy(rate = rate)
        )
    }
    fun updateDescription(description: String) {
        uiState.value = uiState.value.copy(
            recommendationDraft = uiState.value.recommendationDraft.copy(description = description)
        )
    }
    suspend fun save(): Set<ValidationError>? {
        val errorBundle = mutableSetOf<ValidationError>()
        if(uiState.value.recommendationDraft.title.isEmpty()) {
            errorBundle.add(ValidationError.ValidationErrorTitle)
        }
        if(uiState.value.recommendationDraft.rate == 0) {
            errorBundle.add(ValidationError.ValidationErrorRate)
        }
        if(uiState.value.recommendationDraft.description.isEmpty()) {
            errorBundle.add(ValidationError.ValidationErrorDescription)
        }
        if(errorBundle.isNotEmpty())
            return errorBundle
        uiState.value = uiState.value.copy(
            recommendationDraft = uiState.value.recommendationDraft.copy(
                dateModified = Calendar.getInstance().time
            )
        )
        recommendationRepository.insertRecommendation(uiState.value.recommendationDraft)
        return null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MyCityApplication
                RecommCreateViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    cityRepository = application.appContainer.cityRepository,
                    recommendationRepository = application.appContainer.recommendationRepository
                )
            }
        }
    }
}