package com.example.my_city_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_city_app.R
import com.example.my_city_app.ui.viewmodels.RecommCreateUiState
import com.example.my_city_app.ui.viewmodels.RecommCreateViewModel
import com.example.my_city_app.utils.ValidationError
import kotlinx.coroutines.launch

@Composable
fun RecommCreateView(
    onBack: () -> Unit,
    viewModel: RecommCreateViewModel = viewModel(factory = RecommCreateViewModel.Factory),
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    val uiState: RecommCreateUiState = viewModel.uiState.value
    val coroutineScope = rememberCoroutineScope()
    val validationErrorBundle: MutableState<Set<ValidationError>?> = rememberSaveable {
        mutableStateOf(emptySet())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.city.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(id = uiState.category.stringResId),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            )
        },
        bottomBar = {
            ActionButtonsSaveUpdate(
                onSave = {
                    coroutineScope.launch {
                        validationErrorBundle.value = viewModel.save()
                        if(validationErrorBundle.value?.isNotEmpty() != true) onBack()
                    }
                },
                onDiscard = onBack
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                TextField(
                    value = uiState.recommendationDraft.title,
                    onValueChange = { title ->
                        viewModel.updateTitle(title)
                        validationErrorBundle.value = validationErrorBundle.value?.filter { e ->
                            e != ValidationError.ValidationErrorTitle
                        }?.toSet()
                    },
                    placeholder = { Text(text = "Title") },
                    isError = validationErrorBundle
                        .value
                        ?.contains(ValidationError.ValidationErrorTitle) == true,
                    supportingText = {
                        if(validationErrorBundle
                            .value
                            ?.contains(ValidationError.ValidationErrorTitle) == true
                        ){
                            Text(
                                text = stringResource(id = R.string.title_error)
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                RatingBar(
                    selectedRate = uiState.recommendationDraft.rate,
                    onClick = { rate ->
                        viewModel.updateRate(rate)
                        validationErrorBundle.value = validationErrorBundle
                            .value
                            ?.filter { e ->
                                e != ValidationError.ValidationErrorRate
                            }?.toSet()
                    }
                    )
                if(validationErrorBundle
                    .value
                    ?.contains(ValidationError.ValidationErrorRate) == true
                ) {
                    Text(
                        text = stringResource(id = R.string.rate_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = dimensionResource(id = R.dimen.padding_small)
                            )
                    )
                }
                TextField(
                    value = uiState.recommendationDraft.description,
                    onValueChange = { desc ->
                        viewModel.updateDescription(desc)
                        validationErrorBundle.value = validationErrorBundle.value?.filter {e ->
                            e != ValidationError.ValidationErrorDescription
                        }?.toSet()
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.placeholder_recomm_description))
                    },
                    isError = validationErrorBundle
                        .value
                        ?.contains(ValidationError.ValidationErrorDescription) == true,
                    supportingText = {
                        if(validationErrorBundle
                            .value
                            ?.contains(ValidationError.ValidationErrorDescription) == true
                        ) {
                            Text(
                                text = stringResource(id = R.string.description_error)
                            )
                        }
                    },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                )
            }
        }
    }

}
