package com.anos.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anos.feature.home.R
import com.anos.ui.theme.AppTheme
import com.anos.ui.theme.AppThemeProps
import com.anos.ui.theme.Dimens
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun HomeTopBarWithSearch(
    searchQuery: String,
    onQueryContent: (String) -> Unit,
    onCloseClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf(searchQuery) }

    TopSearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppThemeProps.colors.primary),
        state = searchBarState,
        windowInsets = SearchBarDefaults.windowInsets.add(
            WindowInsets(left = Dimens.spacing8, right = Dimens.spacing8)
        ),
        inputField = {
            TextField(
                value = query,
                onValueChange = { query = it }, // called on each keystroke
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.appBarSearchHeight)
                    .padding(PaddingValues.Zero)
                    .focusRequester(focusRequester),
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.home_search_hint)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = com.anos.ui.R.drawable.outline_search_24),
                        contentDescription = "Search Icon",
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onCloseClick()
                        scope.launch { searchBarState.animateToCollapsed() }
                    }) {
                        Icon(
                            painter = painterResource(id = com.anos.ui.R.drawable.outline_close_24),
                            contentDescription = "Close Search",
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppThemeProps.background.color,
                    unfocusedContainerColor = AppThemeProps.background.color,
                ),
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = AppThemeProps.background.color,
            inputFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = AppThemeProps.background.color,
                unfocusedContainerColor = AppThemeProps.background.color,
            ),
        ),
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(300L) // change debounce duration as needed
            .distinctUntilChanged()
            .collectLatest { value ->
                onQueryContent(value)
            }
    }
}

@Preview
@Composable
private fun HomeTopBarWithSearchPreview() {
    AppTheme {
        HomeTopBarWithSearch(
            searchQuery = "",
            onQueryContent = {},
            onCloseClick = {}
        )
    }
}