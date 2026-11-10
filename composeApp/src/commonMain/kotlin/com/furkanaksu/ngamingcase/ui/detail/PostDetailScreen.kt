package com.furkanaksu.ngamingcase.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.furkanaksu.ngamingcase.resources.Res
import com.furkanaksu.ngamingcase.resources.action_save
import com.furkanaksu.ngamingcase.resources.cd_back
import com.furkanaksu.ngamingcase.resources.detail_not_found
import com.furkanaksu.ngamingcase.resources.detail_save_error
import com.furkanaksu.ngamingcase.resources.detail_title
import com.furkanaksu.ngamingcase.resources.error_description_empty
import com.furkanaksu.ngamingcase.resources.error_title_empty
import com.furkanaksu.ngamingcase.resources.ic_arrow_back
import com.furkanaksu.ngamingcase.resources.label_description
import com.furkanaksu.ngamingcase.resources.label_title
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = koinViewModel { parametersOf(postId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnBack by rememberUpdatedState(onBack)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PostDetailEvent.Saved -> currentOnBack()
                PostDetailEvent.SaveFailed ->
                    snackbarHostState.showSnackbar(getString(Res.string.detail_save_error))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = stringResource(Res.string.cd_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (uiState.notFound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.detail_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            PostEditForm(
                uiState = uiState,
                onTitleChange = viewModel::onTitleChange,
                onBodyChange = viewModel::onBodyChange,
                onSave = viewModel::save,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun PostEditForm(
    uiState: PostDetailUiState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        AsyncImage(
            model = uiState.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(Res.string.label_title)) },
                isError = !uiState.isTitleValid,
                supportingText = if (!uiState.isTitleValid) {
                    { Text(stringResource(Res.string.error_title_empty)) }
                } else {
                    null
                },
                enabled = !uiState.isSaving,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.body,
                onValueChange = onBodyChange,
                label = { Text(stringResource(Res.string.label_description)) },
                isError = !uiState.isBodyValid,
                supportingText = if (!uiState.isBodyValid) {
                    { Text(stringResource(Res.string.error_description_empty)) }
                } else {
                    null
                },
                enabled = !uiState.isSaving,
                minLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.action_save))
                }
            }
        }
    }
}
