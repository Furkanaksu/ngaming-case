package com.furkanaksu.ngamingcase

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.furkanaksu.ngamingcase.ui.list.PostListScreen

@Composable
fun App() {
    MaterialTheme {
        PostListScreen(onPostClick = {})
    }
}
