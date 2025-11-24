package com.treymartin.tiltskier.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import com.treymartin.tiltskier.game.RunState

@Composable
fun KeepScreenOn(runState: RunState) {
    val view = LocalView.current

    DisposableEffect(runState) {
        view.keepScreenOn = (runState == RunState.RUNNING)

        onDispose {
            view.keepScreenOn = false
        }
    }
}
