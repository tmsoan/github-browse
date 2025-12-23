package com.anos.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.anos.model.OwnerInfo
import com.anos.model.Repo
import com.anos.ui.R
import com.anos.ui.theme.AppTheme
import com.anos.ui.theme.AppThemeProps
import com.anos.ui.theme.Dimens
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun RepoCard(
    repoInfo: Repo,
    onItemClick: (Repo) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(Dimens.spacing8)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onItemClick(repoInfo) },
        shape = RoundedCornerShape(Dimens.spacing8),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevationSmall),
        colors = CardColors(
            containerColor = AppThemeProps.colors.white,
            disabledContainerColor = AppThemeProps.background.color,
            disabledContentColor = AppThemeProps.background.color,
            contentColor = AppThemeProps.colors.white,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingValues(Dimens.spacing8)),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(Dimens.spacing32)
                        .clip(RoundedCornerShape(Dimens.spacing16)),
                    imageModel = { repoInfo.owner.avatarUrl },
                    imageOptions = ImageOptions(contentScale = ContentScale.Inside),
                    previewPlaceholder = painterResource(id = R.drawable.outline_background_replace_24),
                )
                Text(
                    modifier = Modifier
                        .weight(1f),
                    maxLines = 1,
                    color = AppThemeProps.colors.black,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    text = repoInfo.fullName.orEmpty()
                )
            }
            Text(
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                color = AppThemeProps.colors.black,
                fontWeight = FontWeight.Normal,
                text = repoInfo.description.orEmpty()
            )
        }
    }
}

@Preview
@Composable
private fun RepoCardPreview() {
    AppTheme {
        RepoCard(
            repoInfo = Repo(
                id = 1,
                name = "repo name",
                fullName = "owner/repo name",
                description = "This is a sample description for the repository card component preview.",
                owner = OwnerInfo(
                    login = "owner",
                    avatarUrl = "https://avatars.githubusercontent.com/u/9919?s=200&v=4"
                )
            ),
            onItemClick = {}
        )
    }
}
