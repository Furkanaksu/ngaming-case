package com.furkanaksu.ngamingcase.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanaksu.ngamingcase.data.repository.PostRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val imageUrl: String? = null,
    val title: String = "",
    val body: String = "",
    val originalTitle: String = "",
    val originalBody: String = "",
    val isSaving: Boolean = false,
    val notFound: Boolean = false,
) {
    val isTitleValid: Boolean get() = title.isNotBlank()
    val isBodyValid: Boolean get() = body.isNotBlank()
    val hasChanges: Boolean get() = title.trim() != originalTitle || body.trim() != originalBody
    val canSave: Boolean get() = !notFound && !isSaving && isTitleValid && isBodyValid && hasChanges
}

sealed interface PostDetailEvent {
    data object Saved : PostDetailEvent
    data object SaveFailed : PostDetailEvent
}

class PostDetailViewModel(
    private val postId: Int,
    private val repository: PostRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<PostDetailEvent>(Channel.BUFFERED)
    val events: Flow<PostDetailEvent> = _events.receiveAsFlow()

    private fun initialState(): PostDetailUiState {
        val post = repository.getPost(postId) ?: return PostDetailUiState(notFound = true)
        return PostDetailUiState(
            imageUrl = post.imageUrl,
            title = post.title,
            body = post.body,
            originalTitle = post.title,
            originalBody = post.body,
        )
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }

    fun onBodyChange(value: String) = _uiState.update { it.copy(body = value) }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            repository.updatePost(postId, state.title, state.body)
                .onSuccess { updated ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            title = updated.title,
                            body = updated.body,
                            originalTitle = updated.title,
                            originalBody = updated.body,
                        )
                    }
                    _events.send(PostDetailEvent.Saved)
                }
                .onFailure {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(PostDetailEvent.SaveFailed)
                }
        }
    }
}
