package com.example.jetcaster.ui.tooling

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.jetcaster.ui.shared.LocalNavAnimatedVisibilityScope
import com.example.jetcaster.ui.shared.LocalSharedTransitionScope
import com.example.jetcaster.ui.theme.JetcasterTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JetCasterPreviewWrapper(
    content: @Composable () -> Unit
) {
    JetcasterTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this
                ) {
                    content()
                }
            }
        }
    }
}
