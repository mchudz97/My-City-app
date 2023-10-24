package com.example.my_city_app.ui.screens

import android.app.Activity
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.my_city_app.R
import com.example.my_city_app.R.dimen.padding_large
import com.example.my_city_app.R.dimen.padding_medium
import com.example.my_city_app.data.model.City
import com.example.my_city_app.ui.theme.MyCityappTheme
import com.example.my_city_app.ui.viewmodels.CityScreenUiState
import com.example.my_city_app.ui.viewmodels.CityScreenViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navigateToCategories: (String) -> Unit,
    viewModel: CityScreenViewModel = viewModel(factory = CityScreenViewModel.Factory)
){
    val uiState = viewModel.uiState.collectAsState().value
    val activity = LocalContext.current as Activity
    when(uiState) {
        is CityScreenUiState.Default -> {
            BackHandler {
                activity.finish()
            }
            MainScreen(
                cities = uiState.cities,
                onClick = navigateToCategories,
                onHold = viewModel::focus,
                onCreateClick = viewModel::beginCreating
            )
        }
        is CityScreenUiState.Focusing -> {
            MainScreen(
                cities = uiState.cities,
                focused = uiState.focusedCity,
                onClick = { },
                onHold = viewModel::focus,
                onCancel = viewModel::toListView,
                onEditClick = viewModel::beginEditing,
                onDeleteClick = viewModel::removeCity
            )
        }
        is CityScreenUiState.Editing -> {
            MainScreen(
                cities = uiState.cities,
                focused = uiState.city,
                isModified = true,
                onClick = { },
                onHold = { },
                onCancel = viewModel::toListView,
                updateName = viewModel::holdDraft,
                updateCity = viewModel::finishEditing
            )
        }
        is CityScreenUiState.Creating -> {
            MainScreen(
                cities = uiState.cities,
                onClick = { },
                onHold = { },
            )
            CreationDialog(
                draft = uiState.city,
                save = viewModel::finishCreating,
                updateDraft = viewModel::holdDraft,
                toDefaultState = viewModel::toListView
            )
        }
    }
}

@Composable
fun MainScreen(
    cities: List<City>,
    focused: City? = null,
    isModified: Boolean = false,
    onClick: (String) -> Unit,
    onHold: (City) -> Unit,
    onCreateClick: () -> Unit = { },
    onEditClick: (City) -> Unit = { },
    onCancel: () -> Unit = { },
    onDeleteClick: suspend (City) -> Unit = { },
    updateName: (String) -> Unit = { },
    updateCity: suspend () -> Unit = { },
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                }
            )
        },
        floatingActionButton = {
            if(focused == null)
                FloatingActionButton(
                    onClick = onCreateClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Color.Green
                    )
                }
        }
    ) {
        CitiesList(
            cities = cities,
            focused = focused,
            isModified = isModified,
            onClick = onClick,
            onHold = onHold,
            onEditClick = onEditClick,
            onCancel = onCancel,
            onDeleteClick = onDeleteClick,
            updateName = updateName,
            updateCity = updateCity,
            modifier = Modifier.padding(it)
        )
    }
}
@Composable
fun CitiesList(
    cities: List<City>,
    focused: City?,
    isModified: Boolean = false,
    onClick: (String) -> Unit,
    onHold: (City) -> Unit,
    onEditClick: (City) -> Unit = { },
    onCancel: () -> Unit = { },
    onDeleteClick: suspend (City) -> Unit = { },
    updateName: (String) -> Unit = { },
    updateCity: suspend () -> Unit = { },
    modifier: Modifier = Modifier
) {
    LazyColumn(
        userScrollEnabled = true,
        contentPadding = PaddingValues(
            dimensionResource(id = padding_medium)
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = padding_medium)),
        modifier = modifier
    ){
        items(items = cities, key = { city -> city.id }) {

            val isFocused: Boolean = focused?.id == it.id

            CityCard(
                city = if(focused?.id == it.id) focused else it,
                isFocused = isFocused,
                isModified = isModified && isFocused,
                onClick = onClick,
                onHold = onHold,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onCancel = onCancel,
                updateName = updateName,
                updateCity = updateCity
            )
        }
    }
}

@Composable
fun CityCard(
    city: City,
    onClick: (String) -> Unit,
    onHold: (City) -> Unit,
    onEditClick: (City) -> Unit = { },
    onDeleteClick: suspend (City) -> Unit = { },
    onCancel: () -> Unit = { },
    updateName: (String) -> Unit = { },
    updateCity: suspend () -> Unit = { },
    isFocused: Boolean = false,
    isModified: Boolean = false,
    modifier: Modifier = Modifier
) {

    val coroutineScope = rememberCoroutineScope()

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { },
                onLongClick = { if (!isFocused) onHold(city) }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = dimensionResource(id = padding_medium))
        ){
            if(isModified) {
                var isDuplicate by remember { mutableStateOf(false) }

                TextField(
                    value = city.name,
                    onValueChange = {
                        updateName(it)
                        isDuplicate = false
                    },
                    isError = isDuplicate,
                    supportingText = {
                        if(isDuplicate) {
                            Text(
                                text = stringResource(id = R.string.error_duplicate, city.name),
                                color = Color.Red
                            )
                        }
                    },
                    modifier = Modifier.weight(1.0f)
                )
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                updateCity()
                            } catch (e: SQLiteConstraintException) {
                                isDuplicate = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null
                    )
                }
                IconButton(onClick = onCancel ) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = null
                    )
                }
            }
            else{
                Text(
                    text = city.name,
                    modifier = Modifier.weight(1.0f)
                )
                if(!isFocused) {
                    IconButton(
                        onClick = {
                            onClick("$ROUTE_CATEGORY/${city.id}")
                            Log.d("Route", "$ROUTE_CATEGORY/${city.id}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
                else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { onEditClick(city) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    onDeleteClick(city)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreationDialog(
    draft: City,
    save: suspend () -> Unit,
    updateDraft: (String) -> Unit,
    toDefaultState: () -> Unit
) {
    var isDuplicate by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = { toDefaultState() }) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = padding_medium))

            ) {
                Text(
                    text = "Add city:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.name,
                    onValueChange = {
                        updateDraft(it)
                        isDuplicate = false
                    },
                    isError = isDuplicate,
                    supportingText = {
                        if(isDuplicate) {
                            Text(
                                text = stringResource(id = R.string.error_duplicate, draft.name),
                                color = Color.Red
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    save()
                                } catch (e: SQLiteConstraintException) {
                                    isDuplicate = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(id = padding_large)))
                    IconButton(
                        onClick = {
                            toDefaultState()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreationDialogPreview(){
    MyCityappTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CreationDialog(
                draft = City(name = ""),
                save = { },
                updateDraft = { },
                toDefaultState = { })
        }
    }
}
@Preview
@Composable
fun CitiesListPreview() {
    val focused = City(id = 1, name = "Cracow")
    val cities = listOf<City>(
        City(name = "Warsaw"),
        focused,
        City(id = 2, name = "Gdansk"),
    )
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        CitiesList(cities = cities, onClick = { }, onHold = { }, focused = focused)
    }
}