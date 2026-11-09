package com.furkanaksu.ngamingcase.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
) {
    val imageUrl: String
        get() = "https://picsum.photos/300/300?random=$id&grayscale"
}
