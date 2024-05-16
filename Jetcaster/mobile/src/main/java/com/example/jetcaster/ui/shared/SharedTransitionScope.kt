@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.jetcaster.ui.shared

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

@Composable
fun SharedTransitionBackground(
    content: @Composable () -> Unit
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this
        ) {
            content()
        }
    }
}

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

data class EpisodeSharedElementKey(
    val id: String,
    val type: EpisodeSharedElementType
)

enum class EpisodeSharedElementType {
    IMAGE,
    TITLE,
    PODCAST_TITLE,
    BACKGROUND,
    BOUNDS
}
