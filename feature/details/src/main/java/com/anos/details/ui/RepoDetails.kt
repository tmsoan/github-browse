package com.anos.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anos.common.convertUtcToLocal
import com.anos.common.decodeBase64
import com.anos.details.ui.component.CollapsingTopBar
import com.anos.details.ui.component.IconLabel
import com.anos.details.ui.component.RepoDetailsSkeleton
import com.anos.details.ui.component.SlightHorizontalDivider
import com.anos.feature.details.R
import com.anos.model.ReadmeContent
import com.anos.model.RepoInfo
import com.anos.ui.components.RetryBox
import com.anos.ui.theme.Dimens
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun DetailsRoute(
    onBackClick: () -> Unit,
    repoDetailsViewModel: RepoDetailsViewModel = hiltViewModel()
) {
    val uiState: DetailsUiState by repoDetailsViewModel.uiState.collectAsStateWithLifecycle()
    val repoInfo: RepoInfo? by repoDetailsViewModel.repoInfo.collectAsStateWithLifecycle()
    val readMeContent: ReadmeContent? by repoDetailsViewModel.readMeContent.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        repoInfo = repoInfo,
        readmeContent = readMeContent,
        onBack = onBackClick,
        onRetryClick = {
            repoDetailsViewModel.refreshData()
        }
    )
}

@Composable
private fun DetailsScreen(
    uiState: DetailsUiState,
    repoInfo: RepoInfo?,
    readmeContent: ReadmeContent?,
    onBack: () -> Unit,
    onRetryClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingTopBar(
                title = repoInfo?.name.orEmpty(),
                avatarUrl = repoInfo?.owner?.avatarUrl,
                onBackClick = onBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        when {
            uiState == DetailsUiState.Loading -> {
                RepoDetailsSkeleton(
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState == DetailsUiState.Idle && repoInfo != null -> {
                DetailsContent(
                    modifier = Modifier.padding(innerPadding),
                    repoInfo = repoInfo,
                    readmeContent = readmeContent,
                )
            }
            uiState is DetailsUiState.Error -> {
                RetryBox(
                    modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    title = stringResource(R.string.details_error_title),
                    message = stringResource(R.string.details_repos_empty_message),
                    buttonText = stringResource(R.string.details_retry_btn),
                    onRetryClick = { onRetryClick() }
                )
            }
        }
    }
}

@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    repoInfo: RepoInfo,
    readmeContent: ReadmeContent?,
) {
    Box(
        modifier = modifier
            .padding(Dimens.spacing16)
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
        ) {
            item {
                Text(
                    text = repoInfo.fullName.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing8))
                Text(
                    text = repoInfo.description.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                SlightHorizontalDivider()
            }
            item {
                if (repoInfo.homepage.isNullOrEmpty().not()) {
                    IconLabel(
                        iconResId = com.anos.ui.R.drawable.link_24,
                        label = repoInfo.homepage.orEmpty(),
                    )
                }
            }
            item {
                IconLabel(
                    iconResId = com.anos.ui.R.drawable.star_24dp,
                    label = stringResource(
                        R.string.details_stars_label,
                        repoInfo.stargazersCount
                    )
                )
            }
            item {
                IconLabel(
                    iconResId = com.anos.ui.R.drawable.alt_route_24dp,
                    label = stringResource(
                        R.string.details_forks_label,
                        repoInfo.forksCount
                    )
                )
            }
            item {
                IconLabel(
                    iconResId = com.anos.ui.R.drawable.clock,
                    label = stringResource(
                        R.string.details_updated_at_label,
                        repoInfo.updatedAt?.let { convertUtcToLocal(it) }.orEmpty()
                    )
                )
            }
            item {
                if (repoInfo.topics.isNullOrEmpty().not()) {
                    Spacer(modifier = Modifier.height(Dimens.spacing8))
                    TopicContent(topics = repoInfo.topics.orEmpty())
                }
            }
            item {
                SlightHorizontalDivider()
            }
            item {
                ReadmeContent(
                    readmeContent = readmeContent
                )
            }
        }
    }
}

@Composable
private fun TopicContent(
    modifier: Modifier = Modifier,
    topics: List<String>,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.details_topics_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        FlowRow{
            topics.forEach { topic ->
                Card(
                    modifier = Modifier.padding(Dimens.spacing4),
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                ) {
                    Text(
                        text = topic,
                        modifier = Modifier.padding(horizontal = Dimens.spacing8, vertical = Dimens.spacing4),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadmeContent(
    readmeContent: ReadmeContent?,
) {
    val downloadUrl = readmeContent?.downloadUrl
    if (downloadUrl.isNullOrEmpty().not()) {
        Spacer(modifier = Modifier.height(Dimens.spacing16))
        Text(
            text = stringResource(R.string.details_readme_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(Dimens.spacing8))
        MarkdownText(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            markdown = decodeBase64(readmeContent.content.orEmpty()).orEmpty()
        )
    }
}