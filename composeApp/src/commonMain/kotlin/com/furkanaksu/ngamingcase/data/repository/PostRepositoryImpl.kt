package com.furkanaksu.ngamingcase.data.repository

import com.furkanaksu.ngamingcase.data.model.Post
import com.furkanaksu.ngamingcase.data.remote.PostApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostRepositoryImpl(private val api: PostApi) : PostRepository {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    override suspend fun refresh(): Result<Unit> = runCatchingCancellable {
        _posts.value = api.getPosts()
    }

    override fun getPost(id: Int): Post? = _posts.value.firstOrNull { it.id == id }

    override suspend fun updatePost(id: Int, title: String, body: String): Result<Post> =
        runCatchingCancellable {
            val current = getPost(id) ?: throw NoSuchElementException("Post $id not found")
            val updated = current.copy(title = title.trim(), body = body.trim())
            api.updatePost(updated)
            _posts.update { list -> list.map { if (it.id == id) updated else it } }
            updated
        }

    override suspend fun deletePost(id: Int): Result<Unit> {
        val snapshot = _posts.value
        val index = snapshot.indexOfFirst { it.id == id }
        if (index == -1) return Result.success(Unit)
        val post = snapshot[index]

        _posts.update { list -> list.filterNot { it.id == id } }
        return runCatchingCancellable { api.deletePost(id) }
            .onFailure { restorePost(post, index) }
    }

    override fun restorePost(post: Post, index: Int) {
        _posts.update { list ->
            if (list.any { it.id == post.id }) {
                list
            } else {
                list.toMutableList().apply { add(index.coerceIn(0, size), post) }
            }
        }
    }
}

internal inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
