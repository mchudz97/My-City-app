package com.example.my_city_app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.my_city_app.R
import com.example.my_city_app.data.model.City
import com.example.my_city_app.data.utils.Category
import com.example.my_city_app.ui.theme.MyCityappTheme

@Composable
fun CategoryView(
    city: City,
    onBack: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    BackHandler {
        onBack()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = city.name) },
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
        CategoryList(
            onCategoryClick = onCategoryClick,
            modifier = Modifier.padding(it)
        )
    }

}
@Composable
fun CategoryList(
    onCategoryClick: (Category) -> Unit,
    selectedCategory: Category? = null,
    modifier: Modifier = Modifier
) {
    val categories = Category.values()

    LazyColumn(
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_medium)),
        modifier = modifier
    ) {
        items(items = categories) {
            Card(
                shape = RectangleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if(it == selectedCategory)
                        MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .clickable(
                        onClick = {
                            onCategoryClick(it)
                        }
                    )
                    .fillMaxWidth()
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
                        //modifier = Modifier.weight(1.0f)
                    )
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
            CategoryView(city = City(name = "test"), onBack = { }, onCategoryClick = { })
        }
    }
}