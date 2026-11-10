package com.furkanaksu.ngamingcase.navigation

import kotlinx.serialization.Serializable

@Serializable
data object PostListRoute

@Serializable
data class PostDetailRoute(val postId: Int)
