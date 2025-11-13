package com.anos.details.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anos.ui.components.ShimmerBox
import com.anos.ui.components.ShimmerCircle
import com.anos.ui.theme.Dimens

@Composable
fun RepoDetailsSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(Dimens.spacing16)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
        ) {
            // Full name skeleton
            item {
                ShimmerBox(
                    height = Dimens.spacing24,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }

            // Description skeleton
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)) {
                    ShimmerBox(height = Dimens.spacing16, modifier = Modifier.fillMaxWidth())
                    ShimmerBox(height = Dimens.spacing16, modifier = Modifier.fillMaxWidth(0.9f))
                    ShimmerBox(height = Dimens.spacing16, modifier = Modifier.fillMaxWidth(0.6f))
                }
            }

            // Divider
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                ShimmerBox(height = 1.dp, modifier = Modifier.fillMaxWidth())
            }

            // Homepage link skeleton
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
                ) {
                    ShimmerCircle(size = Dimens.iconSizeMedium)
                    ShimmerBox(height = Dimens.spacing16, width = 200.dp)
                }
            }

            // Stars skeleton
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
                ) {
                    ShimmerCircle(size = Dimens.iconSizeMedium)
                    ShimmerBox(height = Dimens.spacing16, width = 100.dp)
                }
            }

            // Forks skeleton
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
                ) {
                    ShimmerCircle(size = Dimens.iconSizeMedium)
                    ShimmerBox(height = Dimens.spacing16, width = 120.dp)
                }
            }

            // Updated at skeleton
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
                ) {
                    ShimmerCircle(size = Dimens.iconSizeMedium)
                    ShimmerBox(height = Dimens.spacing16, width = 180.dp)
                }
            }

            // Topics skeleton
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                ShimmerBox(height = Dimens.spacing16, width = 80.dp)
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ShimmerBox(height = Dimens.spacing24, width = 80.dp, cornerRadius = Dimens.radiusMedium)
                    ShimmerBox(height = Dimens.spacing24, width = 100.dp, cornerRadius = Dimens.radiusMedium)
                    ShimmerBox(height = Dimens.spacing24, width = 90.dp, cornerRadius = Dimens.radiusMedium)
                }
            }

            // Divider
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                ShimmerBox(height = 1.dp, modifier = Modifier.fillMaxWidth())
            }

            // README title skeleton
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing16))
                ShimmerBox(height = Dimens.spacing16, width = 100.dp)
                Spacer(modifier = Modifier.height(Dimens.spacing8))
            }

            // README content skeleton
            items(5) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)) {
                    ShimmerBox(height = Dimens.spacing12, modifier = Modifier.fillMaxWidth())
                    ShimmerBox(height = Dimens.spacing12, modifier = Modifier.fillMaxWidth(0.95f))
                    ShimmerBox(height = Dimens.spacing12, modifier = Modifier.fillMaxWidth(0.85f))
                    Spacer(modifier = Modifier.height(Dimens.spacing8))
                }
            }
        }
    }
}

