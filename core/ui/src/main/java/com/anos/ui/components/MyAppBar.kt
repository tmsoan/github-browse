package com.anos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anos.ui.theme.AppTheme
import com.anos.ui.theme.AppThemeProps

@Composable
fun MyAppBar(
    title: String,
    showBackIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    leftActions: @Composable RowScope.() -> Unit = {},
    rightActions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = AppThemeProps.colors.absoluteWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { onBackClick?.invoke() }) {
                    Icon(
                        painter = painterResource(id = com.anos.ui.R.drawable.outline_arrow_back_24),
                        contentDescription = "Back",
                        tint = AppThemeProps.colors.absoluteWhite
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = leftActions
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = AppThemeProps.colors.primary,
        ),
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = rightActions
            )
        }
    )
}

@Preview
@Composable
private fun AppAppBarPreview() {
    AppTheme {
        MyAppBar(
            title = "My App",
            leftActions = {
                Text("L", modifier = Modifier.padding(start = 8.dp))
            },
            rightActions = {
                Text("R", modifier = Modifier.padding(end = 8.dp))
            }
        )
    }
}
