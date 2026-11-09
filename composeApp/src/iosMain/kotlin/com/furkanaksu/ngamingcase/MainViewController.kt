package com.furkanaksu.ngamingcase

import androidx.compose.ui.window.ComposeUIViewController
import com.furkanaksu.ngamingcase.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }
