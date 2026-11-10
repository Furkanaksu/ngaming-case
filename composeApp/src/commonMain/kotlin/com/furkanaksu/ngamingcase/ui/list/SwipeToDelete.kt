package com.furkanaksu.ngamingcase.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furkanaksu.ngamingcase.resources.Res
import com.furkanaksu.ngamingcase.resources.action_delete
import com.furkanaksu.ngamingcase.resources.ic_delete
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private enum class SwipeState { Closed, Open }

private val DeleteActionWidth = 88.dp

@Composable
fun SwipeToDelete(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val actionWidthPx = with(LocalDensity.current) { DeleteActionWidth.toPx() }
    val state = remember { AnchoredDraggableState(initialValue = SwipeState.Closed) }
    val scope = rememberCoroutineScope()
    SideEffect {
        state.updateAnchors(
            DraggableAnchors {
                SwipeState.Closed at 0f
                SwipeState.Open at -actionWidthPx
            },
        )
    }

    Box(modifier = modifier) {
        Box(modifier = Modifier.matchParentSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(DeleteActionWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error)
                    .clickable {
                        scope.launch { state.animateTo(SwipeState.Closed) }
                        onDelete()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                )
                Text(
                    text = stringResource(Res.string.action_delete),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onError,
                )
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    val x = state.offset.takeUnless { it.isNaN() } ?: 0f
                    IntOffset(x.roundToInt(), 0)
                }
                .anchoredDraggable(state, Orientation.Horizontal),
        ) {
            content()
        }
    }
}
