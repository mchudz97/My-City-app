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
import com.example.my_city_app.ui.screens.CITY
import com.example.my_city_app.utils.ValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface RecommUiState {
    data class Intro(
        val city: City,
    ) : RecommUiState
    data class Default(
        val city: City,
        val category: Category,
        val recommendations: List<Recommendation>
    ) : RecommUiState
    data class Focusing(
        val city: City,
        val category: Category,
        val recommendations: List<Recommendation>,
        val focusedRecomm: Recommendation
    ) : RecommUiState
    data class Editing(
        val city: City,
        val category: Category,
        val recommendations: List<Recommendation>,
        val validationErrorBundle: Set<ValidationError>,
        val draft: Recommendation
    ) : RecommUiState
}

class RecommScreenViewModel(
    savedStateHandle: SavedStateHandle,
    val recommendationRepository: RecommendationRepository,
    val cityRepository: CityRepository,
) : ViewModel() {
    private val cityId: Int = checkNotNull(savedStateHandle[CITY])
    private val _uiState: MutableStateFlow<RecommUiState> =
        MutableStateFlow(
            RecommUiState.Intro(City(name = ""))
        )
    val uiState: StateFlow<RecommUiState> = _uiState.asStateFlow()

    init{
        toIntroState()
    }
    fun beginFocusing(recommendation: Recommendation) {
        when(val state = _uiState.value) {
            is RecommUiState.Intro -> { }
            is RecommUiState.Default -> {
                _uiState.value = RecommUiState.Focusing(
                    city = state.city,
                    category = state.category,
                    recommendations = state.recommendations,
                    focusedRecomm = recommendation
                )
            }
            is RecommUiState.Focusing -> {
                _uiState.value = RecommUiState.Focusing(
                    city = state.city,
                    category = state.category,
                    recommendations = state.recommendations,
                    focusedRecomm = recommendation
                )
            }
            is RecommUiState.Editing -> {
                viewModelScope.launch {
                    recommendationRepository.getRecommendationStream(recommendation.id).collect{
                        _uiState.value = RecommUiState.Focusing(
                            city = state.city,
                            category = state.category,
                            recommendations = state.recommendations,
                            focusedRecomm = it
                        )
                    }
                }
            }
        }
    }
    fun beginEdit() {
        when(val state = _uiState.value) {
            is RecommUiState.Focusing -> {
                _uiState.value = RecommUiState.Editing(
                    city = state.city,
                    category = state.category,
                    recommendations = state.recommendations,
                    draft = state.focusedRecomm.copy(),
                    validationErrorBundle = emptySet()
                )
            }
            else -> { }
        }
    }
    fun toDefaultState(category: Category) {
        viewModelScope.launch {
            cityRepository.getCityStream(cityId).collect{ city ->
                recommendationRepository.searchRecommendationsStream(
                    idCity = cityId,
                    category = category
                ).collect{ recomms ->
                    _uiState.value = RecommUiState.Default(
                        city = city,
                        category = category,
                        recommendations = recomms
                    )
                }
            }
        }
    }
    fun toIntroState() {
        viewModelScope.launch {
            cityRepository.getCityStream(cityId).collect{
                _uiState.value = RecommUiState.Intro(it)
            }
        }
    }
    suspend fun save() {
        when(val state = _uiState.value) {
            is RecommUiState.Editing -> {
                val errorBundle = mutableSetOf<ValidationError>()
                if(state.draft.title.isBlank())
                    errorBundle.add(ValidationError.ValidationErrorTitle)
                if(state.draft.rate == 0)
                    errorBundle.add(ValidationError.ValidationErrorRate)
                if(state.draft.description.isBlank())
                    errorBundle.add(ValidationError.ValidationErrorDescription)

                if(errorBundle.isEmpty()) {
                    val stateUpdatedDate = state.copy(
                        draft = state.draft.copy(
                            dateModified = Calendar.getInstance().time
                        )
                    )
                    recommendationRepository.updateRecommendation(stateUpdatedDate.draft)
                    toDefaultState(state.category)
                } else {
                    _uiState.value = state.copy(validationErrorBundle = errorBundle)
                }
            }
            else -> { }
        }
    }
    suspend fun delete() {
        when (val state = _uiState.value) {
            is RecommUiState.Focusing -> {
                recommendationRepository.deleteRecommendation(state.focusedRecomm)
            }
            else -> { }
        }
    }
    fun updateDraft(
        title: String? = null,
        rate: Int? = null,
        description: String? = null,
    ) {
        when(val state = _uiState.value) {
            is RecommUiState.Editing -> {
                _uiState.value = state.copy(
                    draft = state.draft.copy(
                        title = title ?: state.draft.title,
                        description = description ?: state.draft.description,
                        rate = rate ?: state.draft.rate
                    )
                )
            }
            else -> { }
        }
    }
    fun removeError(error: ValidationError) {
        when(val state = _uiState.value) {
            is RecommUiState.Editing -> {
                _uiState.value = state.copy(
                    validationErrorBundle = state.validationErrorBundle
                        .filter { it != error }
                        .toSet()
                )
            }
            else -> { }
        }

    }
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