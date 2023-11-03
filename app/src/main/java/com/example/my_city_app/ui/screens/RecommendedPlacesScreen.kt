package com.example.my_city_app.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_city_app.R
import com.example.my_city_app.R.dimen.padding_medium
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.theme.MyCityappTheme
import com.example.my_city_app.ui.viewmodels.RecommScreenViewModel
import com.example.my_city_app.ui.viewmodels.RecommUiState
import com.example.my_city_app.utils.ValidationError
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat





@Composable
fun RecommendationScreen(
    onBackClicked: () -> Unit,
    onCreateClick: (String) -> Unit,
    windowMode: WindowWidthSizeClass,
    //onCategoryClick: (String) -> Unit = { },
    viewModel: RecommScreenViewModel = viewModel(factory = RecommScreenViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val deletionConfirmation = rememberSaveable{ mutableStateOf(false) }

    when(val uiState = viewModel.uiState.collectAsState().value) {
        is RecommUiState.Intro -> {
            when(windowMode) {
                WindowWidthSizeClass.Expanded -> {
                    RecommendationViewExpanded(
                        city = uiState.city,
                        onBackClicked = onBackClicked,
                        onClick = viewModel::beginFocusing,
                        onCreateClick = onCreateClick,
                        windowMode = windowMode,
                        onCategoryClick = viewModel::toDefaultState
                    )
                }
                else -> {
                    CategoryView(
                        city = uiState.city,
                        onBack = onBackClicked,
                        onCategoryClick = viewModel::toDefaultState
                    )
                }
            }
        }
        is RecommUiState.Default -> {
            when(windowMode) {
                WindowWidthSizeClass.Expanded -> {
                    RecommendationViewExpanded(
                        city = uiState.city,
                        category = uiState.category,
                        recommendations = uiState.recommendations,
                        onBackClicked = onBackClicked,
                        onClick = viewModel::beginFocusing,
                        onCreateClick = onCreateClick,
                        windowMode = windowMode,
                        onCategoryClick = viewModel::toDefaultState
                    )
                }
                else -> {
                    RecommendationView(
                        city = uiState.city,
                        category = uiState.category,
                        recommendations = uiState.recommendations,
                        onBackClicked = viewModel::toIntroState,
                        onClick = viewModel::beginFocusing,
                        onCreateClick = onCreateClick,
                    )
                }
            }
        }
        is RecommUiState.Focusing -> {
            when(windowMode) {
                WindowWidthSizeClass.Expanded -> {
                    RecommendationViewExpanded(
                        city = uiState.city,
                        category = uiState.category,
                        recommendations = uiState.recommendations,
                        onBackClicked = { viewModel.toDefaultState(uiState.category) },
                        onClick = viewModel::beginFocusing,
                        onCreateClick = onCreateClick,
                        windowMode = windowMode,
                        onCategoryClick = viewModel::toDefaultState,
                        focusedRecomm = uiState.focusedRecomm,
                        onDelete = { deletionConfirmation.value = true },
                        onEditClick = viewModel::beginEdit
                    )
                }
                else -> {
                    RecommDetailsView(
                        city = uiState.city,
                        category = uiState.category,
                        recommendation = uiState.focusedRecomm,
                        onBackClicked = { viewModel.toDefaultState(uiState.category) },
                        onSave = viewModel::save,
                        onDelete = { deletionConfirmation.value = true },
                        onEditClick = viewModel::beginEdit,
                        //updateDraft = viewModel::updateDraft,
                        windowSize = windowMode,
                    )
                }
            }
            if(deletionConfirmation.value) {
                YesNoDialog(
                    description = stringResource(id = R.string.confirm_question),
                    yesAction = {
                        coroutineScope.launch {
                            viewModel.delete()
                            deletionConfirmation.value = false
                            viewModel.toDefaultState(uiState.category)
                        }
                    },
                    noAction = {
                        deletionConfirmation.value = false
                    }
                )
            }
        }
        is RecommUiState.Editing -> {
            when(windowMode) {
                WindowWidthSizeClass.Expanded -> {
                    RecommendationViewExpanded(
                        city = uiState.city,
                        category = uiState.category,
                        recommendations = uiState.recommendations,
                        validationErrorBundle = uiState.validationErrorBundle,
                        onBackClicked = { viewModel.beginFocusing(uiState.draft) },
                        //onClick = viewModel::beginFocusing,
                        //onCreateClick = onCreateClick,
                        windowMode = windowMode,
                        //onCategoryClick = onCategoryClick,
                        focusedRecomm = uiState.draft,
                        isEditing = true,
                        onSave = {
                            coroutineScope.launch {
                                viewModel.save()
                            }
                        },
                        updateDraft = viewModel::updateDraft
                    )
                }
                else -> {
                    RecommDetailsView(
                        city = uiState.city,
                        category = uiState.category,
                        recommendation = uiState.draft,
                        validationErrorBundle = uiState.validationErrorBundle,
                        onBackClicked = { viewModel.beginFocusing(uiState.draft) },
                        onSave = viewModel::save,
                        //onDelete = { deletionConfirmation.value = true },
                        //onEditClick = viewModel::beginEdit,
                        updateDraft = viewModel::updateDraft,
                        draft = uiState.draft,
                        isEditing = true,
                        windowSize = windowMode,
                        removeError = viewModel::removeError

                    )
                }
            }

        }
    }

}
@Composable
fun RecommendationViewExpanded(
    city: City,
    category: Category? = null,
    recommendations: List<Recommendation>? = null,
    validationErrorBundle: Set<ValidationError>? = null,
    onBackClicked: () -> Unit,
    onClick: (Recommendation) -> Unit = { },
    onCreateClick: (String) -> Unit = { },
    windowMode: WindowWidthSizeClass,
    onCategoryClick: (Category) -> Unit = { },
    focusedRecomm: Recommendation? = null,
    isEditing: Boolean = false,
    onEditClick: () -> Unit = { },
    onDelete: () -> Unit = { },
    onSave: () -> Unit = { },
    updateDraft: (String?, Int?, String?) -> Unit = {_, _, _ -> }
) {

    BackHandler {
        onBackClicked()
    }
    Scaffold(
        topBar = {
            RecommTopBar(
                cityText = city.name,
                categoryRes = category?.stringResId,
                onBackClicked = onBackClicked,
                actions = {
                    if(category != null) {
                        IconButton(
                            onClick = {
                                onCreateClick("$ROUTE_CATEGORY/${city.id}/$category/Create")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = Color.Green
                            )
                        }
                    }

                }
            )
        }
    ){
        Row(){
            CategoryList(
                onCategoryClick = onCategoryClick,
                selectedCategory = category,
                modifier = Modifier
                    .padding(it)
                    .weight(0.5f)
            )
            Spacer(
                Modifier
                    .padding(
                        top = it.calculateTopPadding() + dimensionResource(id = padding_medium),
                        bottom = dimensionResource(id = padding_medium)
                    )
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .weight(0.01f)
                    .fillMaxSize()
            )
            if(recommendations != null){
                RecommendationList(
                    recommendations = recommendations,
                    onClick = onClick,
                    windowSize = windowMode,
                    focusedRecomm = focusedRecomm,
                    validationErrorBundle = validationErrorBundle,
                    isEditing = isEditing,
                    onEditClick = onEditClick,
                    onDelete = onDelete,
                    onCancel = onBackClicked,
                    onSave = onSave,
                    updateDraft = updateDraft,
                    modifier = Modifier
                        .padding(it)
                        .weight(1.5f)
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .weight(1.5f)
                ) {
                    Text(
                        text = stringResource(id = R.string.helper_category),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }

        }
    }


}
@Composable
fun RecommendationView(
    city: City,
    category: Category,
    recommendations: List<Recommendation>,
    onBackClicked: () -> Unit,
    onClick: (Recommendation) -> Unit,
    onCreateClick: (String) -> Unit,
) {
    BackHandler {
        onBackClicked()
    }
    val routeCreate = "$ROUTE_CATEGORY/${city.id}/$category/Create"
    Scaffold(
        floatingActionButton = {
            RecommFloatingButton(onClick = {
                onCreateClick(routeCreate)
            })
        },
        topBar = {
            RecommTopBar(
                cityText = city.name,
                categoryRes = category.stringResId,
                onBackClicked = onBackClicked,
            )
        }
    ) {
        RecommendationList(
            recommendations = recommendations,
            onClick = onClick,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun RecommendationList(
    recommendations: List<Recommendation>,
    onClick: (Recommendation) -> Unit = { },
    windowSize: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    focusedRecomm: Recommendation? = null,
    validationErrorBundle: Set<ValidationError>? = null,
    isEditing: Boolean = false,
    onEditClick: () -> Unit = { },
    onDelete: () -> Unit = { },
    onCancel: () -> Unit = { },
    onSave: () -> Unit = { },
    updateDraft: (String?, Int?, String?) -> Unit = {_, _, _ -> },
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = padding_medium)),
        contentPadding = PaddingValues(
            dimensionResource(id = padding_medium)
        ),
        modifier = modifier
    ) {
        items(items = recommendations) {
            if(windowSize == WindowWidthSizeClass.Expanded){
                RecommendationCardExpanded(
                    recommendation = it,
                    onClick = onClick,
                    draft = focusedRecomm,
                    validationErrorBundle = validationErrorBundle,
                    isEditing = isEditing,
                    onEditClick = onEditClick,
                    onDelete = onDelete,
                    onCancel = onCancel,
                    onSave = onSave,
                    updateDraft = updateDraft
                )
            }
            else{
                RecommendationCard(recommendation = it, onClick = onClick)
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    onClick: (Recommendation) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        modifier = Modifier
            .clickable(onClick = {
                onClick(
                    recommendation
                    /*"$ROUTE_CATEGORY/${recommendation.cityId}/" +
                            "${recommendation.category}/${recommendation.id}"*/
                )
            })
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = padding_medium))
        ) {
            Column(Modifier.weight(2.0f)) {
                Text(
                    text = recommendation.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = buildAnnotatedString {
                        append(
                            AnnotatedString(
                                text = recommendation.description,
                                paragraphStyle = ParagraphStyle(
                                    textIndent = TextIndent(
                                        firstLine = 20.sp
                                    )
                                )
                            )
                        )
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                Modifier
                    .width(16.dp)
                    //.weight(0.1f)
            )
            LazyRow(
                horizontalArrangement = Arrangement.End,
                //modifier = Modifier.weight(1.3f)
            ) {
                items(items = (1..5).toList()) {
                    if(recommendation.rate >= it) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = colorResource(id = R.color.orange_soft)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_star_outline_24),
                            contentDescription = null,
                            //tint = colorResource(id = R.color.orange_soft)
                        )
                    }

                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RecommendationCardExpanded(
    recommendation: Recommendation,
    onClick: (Recommendation) -> Unit,
    draft: Recommendation? = null,
    validationErrorBundle: Set<ValidationError>? = null,
    isEditing: Boolean = false,
    onEditClick: () -> Unit = { },
    onDelete: () -> Unit = { },
    onCancel: () -> Unit = { },
    onSave: () -> Unit = { },
    updateDraft: (String?, Int?, String?) -> Unit,
    modifier: Modifier = Modifier
) {

    val isFocused = draft?.id == recommendation.id

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if(isFocused) {
                MaterialTheme.colorScheme.inversePrimary
            } else
                MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
            .clickable(onClick = {
                onClick(recommendation)
            })
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            if(isFocused) {
                if(isEditing) {
                    EditTools(
                        onSave = onSave,
                        onCancel = onCancel,
                    )
                }
                else {
                    BaseTools(
                        onEditClick = onEditClick,
                        onDelete = onDelete,
                        onCancel = onCancel
                    ) 
                }
            }
            Column(
                modifier = Modifier
                    .padding(dimensionResource(id = padding_medium))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(isFocused && isEditing) {
                        val isError = validationErrorBundle
                            ?.contains(ValidationError.ValidationErrorTitle) == true
                        TextField(
                            value = draft?.title ?: "",
                            onValueChange = { title: String ->
                                updateDraft(title, null, null)
                            },
                            textStyle = MaterialTheme.typography.headlineSmall,
                            singleLine = true,
                            isError = isError,
                            supportingText = {
                                if(isError)
                                {
                                    Text(
                                        text = stringResource(id = R.string.title_error),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            placeholder = { Text(text = "Title") },
                            modifier = Modifier.weight(2f)
                        )
                    } else {
                        Text(
                            text = recommendation.title,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(2f)
                        )
                    }
                    Spacer(
                        Modifier
                            .width(16.dp)
                            .weight(0.05f)
                    )
                    RatingBar(
                        selectedRate = if(isEditing && isFocused)
                            draft?.rate ?: 0 else recommendation.rate,
                        onClick = if(isEditing) { rate ->
                            updateDraft(null, rate, null)
                        } else { _ -> },
                        modifier = Modifier.weight(2f)
                    )
                }
                Spacer(Modifier.height(dimensionResource(id = padding_medium)))
                if(isFocused && isEditing) {
                    val isError = validationErrorBundle
                        ?.contains(ValidationError.ValidationErrorDescription) == true
                    TextField(
                        value = draft?.description ?: "",
                        onValueChange = { description: String ->
                            updateDraft(null, null, description)
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        isError = isError,
                        supportingText = {
                            if(isError)
                            {
                                Text(
                                    text = stringResource(id = R.string.description_error),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        placeholder = { Text(text = "Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                    )
                } else {
                    Text(
                        text = buildAnnotatedString {
                            append(
                                AnnotatedString(
                                    text = recommendation.description,
                                    paragraphStyle = ParagraphStyle(
                                        textIndent = TextIndent(
                                            firstLine = 20.sp
                                        )
                                    )
                                )
                            )
                        },
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "${stringResource(id = R.string.label_recomm_last_edit)}: " +
                        SimpleDateFormat("dd/MM/yyyy")
                            .format(recommendation.dateModified),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}
@Composable
fun RecommFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = Color.Green
        )
    }
}
@Composable
fun RecommTopBar(
    cityText: String,
    categoryRes: Int?,
    onBackClicked: () -> Unit,
    actions: @Composable() RowScope.() -> Unit = { }
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = cityText,
                    style = MaterialTheme.typography.titleLarge
                )
                if(categoryRes != null){
                    Text(
                        text = stringResource(id = categoryRes),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = actions
    )
}
@Preview(device = Devices.TABLET)
@Composable
fun RecommendationCardExpandedPreview() {
    Row {
        Spacer(Modifier.weight(0.6f))
        RecommendationCardExpanded(
            recommendation = Recommendation(
                id = 0,
                title = "Test Title",
                description = stringResource(id = R.string.lorem_ipsum),
                category = Category.COFFEE_SHOP,
                rate = 3,
                cityId = 1
            ),
            onClick = { },
            onSave = { },
            updateDraft = { _, _, _ ->},
            modifier = Modifier.weight(1.5f)
        )
    }
}
@Preview
@Composable
fun RecommendationCardPreview() {
    RecommendationCard(
        recommendation = Recommendation(
        id = 0,
        title = "Test Title",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
        category = Category.COFFEE_SHOP,
        rate = 3,
        cityId = 1
        ),
        onClick = { }
    )
}

@Preview
@Composable
fun RecommendationListPreview() {
    val recommendations = listOf(
        Recommendation(
            id = 0,
            title = "Test Title",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            category = Category.COFFEE_SHOP,
            rate = 3,
            cityId = 1
        ),
        Recommendation(
            id = 0,
            title = "Test Title",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            category = Category.COFFEE_SHOP,
            rate = 3,
            cityId = 1
        )
    )

    MyCityappTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            RecommendationList(recommendations = recommendations, onClick = { })
        }
    }
}