package com.anos.details.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.anos.ui.theme.Dimens
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

private val titleSizeBig = 22.sp
private val titleSizeSmall = 18.sp

@Composable
internal fun CollapsingTopBar(
    title: String,
    avatarUrl: String?,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    // fraction = 0 when fully expanded, → 1 when fully collapsed
    val fraction = scrollBehavior.state.overlappedFraction

    // Interpolate sizes
    val avatarSize = lerp(Dimens.iconSizeXLarge, Dimens.iconSizeLarge, fraction) // shrink avatar
    val titleFontSize = lerp(titleSizeBig, titleSizeSmall, fraction) // shrink text

    LargeTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
                modifier = Modifier.graphicsLayer {
                    // subtle vertical move to look natural
                    translationY = fraction * -6f
                }
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(RoundedCornerShape(Dimens.radiusLarge)),
                    imageModel = { avatarUrl },
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    previewPlaceholder = painterResource(id = com.anos.ui.R.drawable.outline_background_replace_24),
                )
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = com.anos.ui.R.drawable.outline_arrow_back_24),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun lerp(start: Dp, stop: Dp, fraction: Float): Dp {
    return start + (stop - start) * fraction
}

@Composable
private fun lerp(start: TextUnit, stop: TextUnit, fraction: Float): TextUnit {
    return (start.value + (stop.value - start.value) * fraction).sp
}
