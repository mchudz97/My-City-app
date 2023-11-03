package com.example.my_city_app.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.my_city_app.R
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.theme.MyCityappTheme
import com.example.my_city_app.utils.ValidationError
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun RecommDetailsView(
    city: City,
    category: Category,
    recommendation: Recommendation,
    validationErrorBundle: Set<ValidationError>? = null,
    draft: Recommendation? = null,
    isEditing: Boolean = false,
    windowSize: WindowWidthSizeClass,
    onBackClicked: () -> Unit,
    onEditClick: () -> Unit = { },
    onDelete: () -> Unit = { },
    onSave: suspend () -> Unit = { },
    updateDraft: (String?, Int?, String?) -> Unit = {_, _, _ -> },
    removeError: (ValidationError) -> Unit = { }
) {
    BackHandler {
        onBackClicked()
    }
    val coroutineScope = rememberCoroutineScope()
    if(isEditing) {
        Scaffold(
            bottomBar = {
                ActionButtonsSaveUpdate(
                    onSave = {
                        coroutineScope.launch {
                            onSave()
                        }
                    },
                    onDiscard = onBackClicked
                )
            }
        ) {
            RecommDetails(
                recommendation = recommendation,
                city = city,
                category = category,
                draft = draft,
                updateDraft = updateDraft,
                validationErrorBundle = validationErrorBundle,
                clearTitleError = {
                    removeError(ValidationError.ValidationErrorTitle)
                },
                clearDescriptionError = {
                    removeError(ValidationError.ValidationErrorDescription)
                },
                windowSize = windowSize,
                modifier = Modifier.padding(it)
            )
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    actions = {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = Color.Green,
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color.Red,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = null
                            )
                        }

                    }
                )
            }
        ) {
            RecommDetails(
                recommendation = recommendation,
                city = city,
                category = category,
                windowSize = windowSize,
                modifier = Modifier.padding(it)
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RecommDetails(
    recommendation: Recommendation,
    city: City,
    category: Category,
    draft: Recommendation? = null,
    updateDraft: (String?, Int?, String?) -> Unit = { _, _, _ -> },
    validationErrorBundle: Set<ValidationError>? = null,
    clearTitleError: () -> Unit = {},
    clearDescriptionError: () -> Unit = {},
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {

    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
    val isModified = draft != null

    Box(modifier){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingMedium)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {

                    val weightLabel: Float
                    val weightValue: Float
                    if(windowSize != WindowWidthSizeClass.Compact) {
                        weightLabel = 0.5f
                        weightValue = 0.5f
                    } else {
                        weightLabel = 0.4f
                        weightValue = 0.6f
                    }
                    LabelValue(
                        label = R.string.label_recomm_title,
                        value = draft?.title ?: recommendation.title,
                        editable = isModified,
                        onChange = { titleDraft ->
                            if(isModified) {
                                updateDraft(titleDraft, null, null)
                                clearTitleError()
                            }
                        },
                        isError = validationErrorBundle
                            ?.contains(ValidationError.ValidationErrorTitle) == true,
                        errorMessage = stringResource(id = R.string.title_error)
                    )
                    SpacerLineBreak()
                    LabelValue(
                        label = R.string.label_city,
                        value = city.name
                    )
                    SpacerLineBreak()
                    LabelValue(
                        label = R.string.label_category,
                        value = stringResource(id = category.stringResId)
                    )
                    SpacerLineBreak()
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            text = stringResource(id = R.string.label_recomm_rate) + ":",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(paddingMedium)
                                .weight(weightLabel)
                        )
                        Box(
                            modifier = Modifier.weight(weightValue),
                            //contentAlignment = Alignment.CenterEnd
                        ) {
                            RatingBar(
                                selectedRate = draft?.rate ?: recommendation.rate,
                                onClick = { rateDraft ->
                                    if (isModified) updateDraft(null, rateDraft, null)
                                },

                            )
                        }
                    }
                    if(validationErrorBundle
                            ?.contains(ValidationError.ValidationErrorRate) == true &&
                        isModified
                    ) {
                        Text(
                            text = stringResource(id = R.string.rate_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = dimensionResource(id = R.dimen.padding_small),
                                    start = dimensionResource(id = R.dimen.padding_medium)
                                )
                        )
                    }
                    SpacerLineBreak()
                    LabelValue(
                        label = R.string.label_recomm_last_edit,
                        value = SimpleDateFormat("dd/MM/yyyy")
                            .format(recommendation.dateModified)
                    )
                }
            }
            Spacer(Modifier.height(paddingMedium))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )) {
                Text(
                    text = stringResource(id = R.string.label_recomm_description) + ":",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(paddingMedium)
                )
                if(isModified) {
                    OutlinedTextField(
                        value = draft?.description ?: "-",
                        onValueChange = { descriptionDraft ->
                            updateDraft(null, null, descriptionDraft)
                            clearDescriptionError()
                        },
                        singleLine = false,
                        isError = validationErrorBundle
                            ?.contains(ValidationError.ValidationErrorDescription) == true,
                        supportingText = {
                            if(validationErrorBundle
                                    ?.contains(ValidationError.ValidationErrorDescription) == true
                            ) {
                                Text(
                                    text = stringResource(id = R.string.description_error)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                            .padding(paddingMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                } else {
                    Text(
                        text = recommendation.description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(paddingMedium)
                    )

                }
                Spacer(Modifier.height(paddingMedium))
            }
        }
    }
}


@Composable
fun SpacerLineBreak() {
    Spacer(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .height(1.dp)
            .fillMaxWidth()
    )
}
@Composable
fun LabelValue(
    @StringRes label: Int,
    value: String,
    editable: Boolean = false,
    onChange: (String) -> Unit = { },
    isError: Boolean = false,
    errorMessage: String? = null
) {
    if(editable)
        LabelValueEditable(
            label = label,
            value = value,
            onChange = onChange,
            isError = isError,
            errorMessage = errorMessage
        )
    else
        LabelValueReadonly(label = label, value = value)
}
@Composable
fun LabelValueReadonly(@StringRes label: Int, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_medium)
        )
    ) {
        Text(
            text = stringResource(id = label) + ":",
            style = MaterialTheme.typography.labelLarge,
            //textAlign = TextAlign.End,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RectangleShape
                )
                .weight(1f)
        )
        Spacer(Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(id = R.dimen.padding_small))
        )
    }
}

@Composable
fun LabelValueEditable(
    @StringRes label: Int,
    value: String,
    onChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_medium)
        )
    ) {
        Text(
            text = stringResource(id = label) + ":",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RectangleShape
                )
                .weight(1f)
        )
        Spacer(Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            isError = isError,
            supportingText = {
                if(isError){
                    Log.d("validation error", errorMessage?: "no message")
                    Text(
                        text = errorMessage ?: ""
                    )
                }
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(id = R.dimen.padding_small))
        )
    }
}
@Preview
@Composable
fun RecommDetailsPreview(){
    val recomm = Recommendation(
        title = "title",
        description = "description",
        rate = 5,
        category = Category.COFFEE_SHOP,
        dateModified = Date(),
        cityId = 1
    )
    MyCityappTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            RecommDetails(
                recommendation = recomm,
                city = City(
                    name = "city"
                ),
                category = Category.COFFEE_SHOP,
                draft = recomm,
                windowSize = WindowWidthSizeClass.Compact
            )
        }
    }
}
@Preview
@Composable
fun RecommDetailsPreviewReadonly(){
    val recomm = Recommendation(
        title = "title",
        description = "description",
        rate = 5,
        category = Category.COFFEE_SHOP,
        dateModified = Date(),
        cityId = 1
    )
    MyCityappTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            RecommDetails(
                recommendation = recomm,
                city = City(
                    name = "city"
                ),
                category = Category.COFFEE_SHOP,
                windowSize = WindowWidthSizeClass.Compact
            )
        }
    }
}