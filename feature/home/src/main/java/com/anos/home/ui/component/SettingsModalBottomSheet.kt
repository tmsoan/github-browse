package com.anos.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.anos.feature.home.R
import com.anos.home.ui.SortOption
import com.anos.ui.theme.Dimens

@Composable
internal fun SettingsModalBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SettingsSheetContent(onClose = onDismiss)
    }
}

@Composable
private fun SettingsSheetContent(
    onClose: () -> Unit,
    onApplySort: ((List<SortOption>) -> Unit)? = null
) {
    val sortOptions = listOf(
        SortOption.STARS,
        SortOption.LAST_UPDATED,
        SortOption.FORKS
    )
    val sortLabels = mapOf(
        SortOption.STARS to "Stars",
        SortOption.LAST_UPDATED to "Last Updated",
        SortOption.FORKS to "Forks"
    )
    var selectedOptions by remember { mutableStateOf(listOf<SortOption>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacing8, horizontal = Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
    ) {
        Text(
            stringResource(R.string.home_sheet_reorder_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        sortOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable(
                    onClick = {
                        selectedOptions = if (selectedOptions.contains(option)) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                    }
                )
            ) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { }
                )
                Text(sortLabels[option] ?: option.name)
            }
        }
        Text(
            "(This needs authenticated GitHub API. Coming soon!)",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onClose
            ) {
                Text(stringResource(R.string.home_sheet_reorder_cancel))
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    onApplySort?.invoke(selectedOptions)
                    onClose()
                },
                enabled = selectedOptions.isNotEmpty()
            ) {
                Text(stringResource(R.string.home_sheet_reorder_apply))
            }
        }
    }
}
