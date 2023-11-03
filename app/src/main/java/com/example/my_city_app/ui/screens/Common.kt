package com.example.my_city_app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.my_city_app.R
import com.example.my_city_app.R.dimen.padding_medium
import com.example.my_city_app.ui.theme.MyCityappTheme
import kotlinx.coroutines.launch

@Composable
fun RatingBar(selectedRate: Int, onClick: (Int) -> Unit = { }, modifier: Modifier = Modifier) {
    LazyRow(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        items(items = (1..5).toList()) {rateIt ->
            if(selectedRate >= rateIt) {
                IconButton(onClick = {
                    onClick(rateIt)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = colorResource(id = R.color.orange_soft)
                    )
                }
            }
            else {
                IconButton(onClick = {
                    onClick(rateIt)
                }) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.baseline_star_outline_24
                        ),
                        contentDescription = null,
                    )
                }
            }

        }
    }
}

@Composable
fun ActionButtonsSaveUpdate(onSave: suspend () -> Unit, onDiscard: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    onSave()
                }
            },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6EE66E)
            ),
            modifier = Modifier
                .weight(1.0f)
                //.fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(text = "Save")
        }
        Spacer(Modifier.weight(1.5f))
        Button(
            onClick = onDiscard,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF50087)
            ),
            modifier = Modifier
                .weight(1.0f)
                //.fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(text = "Discard")
        }
    }
}
@Composable
fun YesNoDialog(description: String, yesAction: () -> Unit, noAction: () -> Unit) {
    Dialog(onDismissRequest = noAction) {
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(id = padding_medium))

            ) {
                Text(
                    text = description,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(dimensionResource(id = padding_medium)))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = Color(0xFF83CC98),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = yesAction
                            )
                            .weight(0.5f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.yes),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier
                                .padding(
                                    horizontal = dimensionResource(id = padding_medium),
                                    vertical = dimensionResource(id = R.dimen.padding_small),
                                )
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.padding_large))
                            .weight(1f)
                    )
                    Surface(
                        color = Color(0xFFCC8398),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = noAction
                            )
                            .weight(0.5f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.no),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier
                                .padding(
                                    horizontal = dimensionResource(id = padding_medium),
                                    vertical = dimensionResource(id = R.dimen.padding_small),
                                )
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun BaseTools(
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = Color.Red
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = null
            )
        }
    }
}

@Composable
fun EditTools(onSave: () -> Unit, onCancel: () -> Unit) {
    Row {
        IconButton(onClick = onSave) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.Green
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = null
            )
        }
    }
}
@Preview
@Composable
fun YesNoDialogPreview() {
    MyCityappTheme {
        Surface(Modifier.fillMaxSize()) {
            YesNoDialog(description = "hello", yesAction = { /*TODO*/ }) {
                
            }
        }
    }
}
