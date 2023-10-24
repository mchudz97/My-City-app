package com.example.my_city_app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_city_app.R
import com.example.my_city_app.data.model.Recommendation
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.theme.MyCityappTheme
import com.example.my_city_app.ui.viewmodels.RecommScreenViewModel

@Composable
fun RecommendationView(
    onBackClicked: () -> Unit,
    onClick: (String) -> Unit,
    onCreateClick: (String) -> Unit,
    viewModel: RecommScreenViewModel = viewModel(factory = RecommScreenViewModel.Factory)
) {
    val uiState = viewModel.recommUiState.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onCreateClick("$ROUTE_CATEGORY/${uiState.city.id}/${uiState.category}/Create")
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        },
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
        BackHandler {
            onBackClicked()
        }
        RecommendationList(
            recommendations = uiState.recommendations,
            onClick = onClick,
            modifier = Modifier.padding(it))
    }
}



@Composable
fun RecommendationList(
    recommendations: List<Recommendation>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        contentPadding = PaddingValues(
            dimensionResource(id = R.dimen.padding_medium)
        ),
        modifier = modifier
    ) {
        items(items = recommendations) {
            RecommendationCard(recommendation = it, onClick = onClick)
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation, onClick: (String) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        modifier = Modifier
            .clickable(onClick = {
                onClick("$ROUTE_CATEGORY/${recommendation.cityId}/" +
                        "${recommendation.category}/${recommendation.id}")
            })
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Column(Modifier.weight(2.0f)) {
                Text(
                    text = recommendation.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = recommendation.description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                Modifier
                    .width(16.dp)
                    .weight(0.1f))
            LazyRow(
                modifier = Modifier.weight(1.3f)
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