package lmu.pms.stila.ui.wear.widget

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.ui.Modifier

/**
 * Override a composable to be unswipeable by blocking the swipe-to-dismiss gesture.
 *
 * When a composable is on top of another composable that supports swipe-to-dismiss,
 * then [Modifier.unswipeable] can be applied to the top composable to handle and ignore
 * the swipe gesture. For example, this may be used to prevent swiping away a dialog.
 */
fun Modifier.unswipeable() =
    this.then(
        Modifier.draggable(
            orientation = Orientation.Horizontal,
            enabled = true,
            state = DraggableState {}
        )
    )

//fun Modifier.gestureSwipeableDisabled(disabled: Boolean = true) =
//    if (disabled) {
//        pointerInput(Unit) {
//
//            detectHorizontalDragGestures { change, dragAmount ->
//                // val original = Offset(offsetX.value, 0f)
//                val summed = original + Offset(x = dragAmount, y = 0f)
//                val newValue = Offset(x = summed.x.coerceIn(0f, cardOffset), y = 0f)
//                if (newValue.x >= 10) {
//                    onExpand()
//                    return@detectHorizontalDragGestures
//                } else if (newValue.x <= 0) {
//                    onCollapse()
//                    return@detectHorizontalDragGestures
//                }
//                if (change.positionChange() != Offset.Zero) change.consume()
//                // offsetX.value = newValue.x
//            }
//        }
//    } else {
//        this
//    }