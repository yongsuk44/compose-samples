/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui.shared

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.EpisodeInfo
import com.example.jetcaster.core.model.PodcastInfo
import com.example.jetcaster.core.player.model.PlayerEpisode
import com.example.jetcaster.designsystem.component.HtmlTextContainer
import com.example.jetcaster.designsystem.component.PodcastImage
import com.example.jetcaster.ui.tooling.JetCasterPreviewWrapper
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EpisodeListItem(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onClick: (EpisodeInfo) -> Unit,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = false,
) {
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No AnimatedVisibilityScope")

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedTransitionScope")

    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = EpisodeSharedElementKey(
                            uri = episode.uri,
                            type = EpisodeSharedElementType.BOUNDS
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope
                )

        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                onClick = { onClick(episode) }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Top Part
                    EpisodeListItemHeader(
                        episode = episode,
                        podcast = podcast,
                        showPodcastImage = showPodcastImage,
                        showSummary = showSummary,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Bottom Part
                    EpisodeListItemFooter(
                        episode = episode,
                        podcast = podcast,
                        onQueueEpisode = onQueueEpisode,
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeListItemFooter(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    onQueueEpisode: (PlayerEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) { /* TODO */ }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
        )

        val duration = episode.duration
        Text(
            text = when {
                duration != null -> {
                    // If we have the duration, we combine the date/duration via a
                    // formatted string
                    stringResource(
                        R.string.episode_date_duration,
                        MediumDateFormatter.format(episode.published),
                        duration.toMinutes().toInt()
                    )
                }
                // Otherwise we just use the date
                else -> MediumDateFormatter.format(episode.published)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )

        IconButton(
            onClick = {
                onQueueEpisode(
                    PlayerEpisode(
                        podcastInfo = podcast,
                        episodeInfo = episode
                    )
                )
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { /* TODO */ },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun EpisodeListItemHeader(
    episode: EpisodeInfo,
    podcast: PodcastInfo,
    showPodcastImage: Boolean,
    showSummary: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Row(modifier = modifier) {
            Column(
                modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = episode.title,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = EpisodeSharedElementKey(
                                    uri = episode.uri,
                                    type = EpisodeSharedElementType.TITLE
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )

                if (showSummary) {
                    HtmlTextContainer(text = episode.summary) {
                        Text(
                            text = it,
                            maxLines = 2,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
//                            modifier = Modifier
//                                .sharedBounds(
//                                    sharedContentState = rememberSharedContentState(
//                                        key = EpisodeSharedElementKey(
//                                            uri = episode.uri,
//                                            type = EpisodeSharedElementType.SUMMARY
//                                        )
//                                    ),
//                                    animatedVisibilityScope = animatedVisibilityScope
//                                )
                        )
                    }
                } else {
                    Text(
                        text = podcast.title,
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(
                                    key = EpisodeSharedElementKey(
                                        uri = episode.uri,
                                        type = EpisodeSharedElementType.PODCAST_TITLE
                                    )
                                ),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }
            }
            if (showPodcastImage) {
                EpisodeListItemImage(
                    podcast = podcast,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Blue)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = EpisodeSharedElementKey(
                                    uri = podcast.imageUrl,
                                    type = EpisodeSharedElementType.IMAGE
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
            }
        }
    }
}

@Composable
private fun EpisodeListItemImage(
    podcast: PodcastInfo,
    modifier: Modifier = Modifier
) {
    PodcastImage(
        podcastImageUrl = podcast.imageUrl,
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EpisodeListItemPreview() {
    JetCasterPreviewWrapper {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            podcast = PreviewPodcasts[0],
            onClick = {},
            onQueueEpisode = {},
            showSummary = true
        )
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}
