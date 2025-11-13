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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.anos.model.Repo
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
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.spacing4),
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
                    imageModel = { repoInfo.owner?.avatarUrl },
                    imageOptions = ImageOptions(contentScale = ContentScale.Inside),
                    previewPlaceholder = painterResource(id = com.anos.ui.R.drawable.outline_background_replace_24),
                )
                Text(
                    modifier = Modifier
                        .weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    text = repoInfo.fullName.orEmpty()
                )
            }
            Text(
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                text = repoInfo.description.orEmpty()
            )
        }
    }
}
