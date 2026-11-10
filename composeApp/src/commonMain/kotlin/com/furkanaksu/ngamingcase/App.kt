package com.furkanaksu.ngamingcase

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.furkanaksu.ngamingcase.navigation.PostDetailRoute
import com.furkanaksu.ngamingcase.navigation.PostListRoute
import com.furkanaksu.ngamingcase.ui.detail.PostDetailScreen
import com.furkanaksu.ngamingcase.ui.list.PostListScreen

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = PostListRoute) {
            composable<PostListRoute> { entry ->
                PostListScreen(
                    onPostClick = { postId ->
                        if (entry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            navController.navigate(PostDetailRoute(postId))
                        }
                    },
                )
            }

            composable<PostDetailRoute> { entry ->
                val route = entry.toRoute<PostDetailRoute>()
                PostDetailScreen(
                    postId = route.postId,
                    onBack = dropUnlessResumed { navController.popBackStack() },
                )
            }
        }
    }
}
