package com.furkanaksu.ngamingcase.data.repository

import com.furkanaksu.ngamingcase.data.model.Post
import kotlinx.coroutines.flow.StateFlow

interface PostRepository {
    val posts: StateFlow<List<Post>>

    suspend fun refresh(): Result<Unit>

    fun getPost(id: Int): Post?

    suspend fun updatePost(id: Int, title: String, body: String): Result<Post>

    suspend fun deletePost(id: Int): Result<Unit>

    fun restorePost(post: Post, index: Int)
}
