package com.anos.home.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.anos.feature.home.R
import com.anos.ui.theme.Dimens

@Composable
fun HeaderSearchBox(
    searchQuery: String,
    onQueryContent: (String) -> Unit,
    onCloseClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onQueryContent(it) },
            modifier = Modifier.weight(1f).focusRequester(focusRequester),
            placeholder = { Text(stringResource(R.string.home_search_hint)) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        IconButton(onClick = {
            onCloseClick()
        }) {
            Icon(
                painter = painterResource(id = com.anos.ui.R.drawable.outline_close_24),
                contentDescription = "Cancel Search"
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}