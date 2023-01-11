package com.example.android.wearable.composeforwearos.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text

@Composable
fun SampleChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String,
    content: (@Composable () -> Unit)? = null
) {
    Chip(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        label = {
            Text(modifier = Modifier.weight(1f), text = label)
            if (content != null) {
                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    content()
                }
            }
        }
    )
}

@Composable
fun SimpleIconButton(
    imageVector: ImageVector,
    size: Dp = ButtonDefaults.DefaultIconSize,
    colors: ButtonColors = ButtonDefaults.primaryButtonColors(),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = colors,
    ) {
        Icon(
            // https://fonts.google.com/icons
            imageVector = imageVector, // Icons.Rounded.Check,
            contentDescription = "",
            modifier = Modifier
                .size(size)
                .wrapContentSize(align = Alignment.Center)
        )
    }
}