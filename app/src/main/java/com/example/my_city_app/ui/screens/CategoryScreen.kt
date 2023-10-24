package com.example.my_city_app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_city_app.R
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.theme.MyCityappTheme
import com.example.my_city_app.ui.viewmodels.CategoryScreenViewModel

@Composable
fun CategoryView(
    onBack: () -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: CategoryScreenViewModel = viewModel(factory = CategoryScreenViewModel.Factory)
) {

    val categories = Category.values()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = viewModel.city.value.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(Modifier.padding(it)) {
            items(items = categories) {
                Card(
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.clickable(
                        onClick = {
                            onCategoryClick(
                                "$ROUTE_CATEGORY/${viewModel.city.value.id}/${it}"
                            )
                        }
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = it.drawableResId),
                            contentDescription = null,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                        )
                        Text(
                            text = stringResource(id = it.stringResId),
                            modifier = Modifier.weight(1.0f)
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun CategoryListPreview() {
    MyCityappTheme (darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()){
            CategoryView(onBack = { }, onCategoryClick = { })
        }
    }
}