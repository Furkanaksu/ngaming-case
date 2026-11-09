package com.furkanaksu.ngamingcase.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furkanaksu.ngamingcase.data.model.Post
import com.furkanaksu.ngamingcase.resources.Res
import com.furkanaksu.ngamingcase.resources.action_retry
import com.furkanaksu.ngamingcase.resources.post_delete_error
import com.furkanaksu.ngamingcase.resources.posts_empty
import com.furkanaksu.ngamingcase.resources.posts_load_error
import com.furkanaksu.ngamingcase.resources.posts_title
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    onPostClick: (Int) -> Unit,
    viewModel: PostListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PostListEvent.DeleteFailed ->
                    snackbarHostState.showSnackbar(getString(Res.string.post_delete_error))
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.posts_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                uiState.posts.isNotEmpty() -> PostList(
                    posts = uiState.posts,
                    onPostClick = onPostClick,
                    onDelete = viewModel::deletePost,
                )

                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                else -> MessageState(
                    message = stringResource(
                        if (uiState.hasError) Res.string.posts_load_error else Res.string.posts_empty,
                    ),
                    actionLabel = stringResource(Res.string.action_retry),
                    onAction = viewModel::loadPosts,
                )
            }
        }
    }
}

@Composable
private fun PostList(
    posts: List<Post>,
    onPostClick: (Int) -> Unit,
    onDelete: (Post) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(items = posts, key = { _, post -> post.id }) { index, post ->
            Column(modifier = Modifier.animateItem()) {
                SwipeToDelete(onDelete = { onDelete(post) }) {
                    PostItem(post = post, onClick = { onPostClick(post.id) })
                }
                if (index < posts.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp + PostImageSize + 16.dp))
                }
            }
        }
    }
}

@Composable
private fun MessageState(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAction) {
            Text(actionLabel)
        }
    }
}
