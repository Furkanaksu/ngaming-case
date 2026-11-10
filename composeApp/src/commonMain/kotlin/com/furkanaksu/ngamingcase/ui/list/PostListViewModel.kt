package com.furkanaksu.ngamingcase.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanaksu.ngamingcase.data.model.Post
import com.furkanaksu.ngamingcase.data.repository.PostRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PostListUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
)

sealed interface PostListEvent {
    data class PostDeleted(val post: Post, val index: Int) : PostListEvent
    data object DeleteFailed : PostListEvent
}

class PostListViewModel(private val repository: PostRepository) : ViewModel() {

    private data class LoadState(val isLoading: Boolean = false, val hasError: Boolean = false)

    private val loadState = MutableStateFlow(LoadState())

    val uiState: StateFlow<PostListUiState> =
        combine(repository.posts, loadState) { posts, load ->
            PostListUiState(posts = posts, isLoading = load.isLoading, hasError = load.hasError)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostListUiState(posts = repository.posts.value, isLoading = true),
        )

    private val _events = Channel<PostListEvent>(Channel.BUFFERED)
    val events: Flow<PostListEvent> = _events.receiveAsFlow()

    init {
        if (repository.posts.value.isEmpty()) loadPosts()
    }

    fun loadPosts() {
        if (loadState.value.isLoading) return
        loadState.value = LoadState(isLoading = true)
        viewModelScope.launch {
            val result = repository.refresh()
            loadState.value = LoadState(isLoading = false, hasError = result.isFailure)
        }
    }

    fun deletePost(post: Post) {
        val index = repository.posts.value.indexOfFirst { it.id == post.id }
        if (index == -1) return
        viewModelScope.launch {
            repository.deletePost(post.id)
                .onSuccess { _events.send(PostListEvent.PostDeleted(post, index)) }
                .onFailure { _events.send(PostListEvent.DeleteFailed) }
        }
    }

    fun undoDelete(post: Post, index: Int) {
        repository.restorePost(post, index)
    }
}
