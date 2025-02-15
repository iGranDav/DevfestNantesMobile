package com.gdgnantes.devfest.androidapp.ui.components.appbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gdgnantes.devfest.androidapp.R
import com.gdgnantes.devfest.androidapp.ui.theme.DevFest_NantesTheme

object AppBarIcons {
    @Composable
    fun Back(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurface,
        onClick: () -> Unit
    ) {
        AppBarIcon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.action_back),
            modifier = modifier,
            color = color,
            onClick = onClick
        )
    }
}

@Composable
internal fun AppBarIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(contentColor = color)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
fun AppBarIconPreview() {
    DevFest_NantesTheme {
        AppBarIcons.Back {
        }
    }
}
