package com.codeitsolo.secureshare.feature.home

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codeitsolo.secureshare.R
import com.codeitsolo.secureshare.core.common.extensions.openSettings
import com.codeitsolo.secureshare.core.common.models.MediaStoreImage
import com.codeitsolo.secureshare.core.common.paging.completedLoadState
import com.codeitsolo.secureshare.core.common.permission.rememberAppPermissionState
import com.codeitsolo.secureshare.feature.home.permissions.MediaPermissionAccessLevel
import com.codeitsolo.secureshare.feature.home.permissions.getMediaPermissionLevel
import com.codeitsolo.secureshare.ui.theme.SecureShareTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Home route to show home screen.
 *
 * @param modifier The Modifier to be applied to this composable
 * @param viewModel The ViewModel to be used in this composable
 */
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val appPermissionState =
        rememberAppPermissionState(
            buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    add(READ_MEDIA_VISUAL_USER_SELECTED)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(READ_MEDIA_IMAGES)
                } else {
                    add(READ_EXTERNAL_STORAGE)
                }
            }
        )

    var mediaPermissionAccessLevel by rememberSaveable(Unit) {
        mutableStateOf(appPermissionState.getMediaPermissionLevel())
    }

    val showManageMediaReselection = remember(mediaPermissionAccessLevel) {
        mediaPermissionAccessLevel == MediaPermissionAccessLevel.Partial
    }

    val allPermissionsGranted: Boolean by remember(appPermissionState) {
        val currentMediaPermissionLevel = appPermissionState.getMediaPermissionLevel()
        mutableStateOf(currentMediaPermissionLevel != MediaPermissionAccessLevel.None)
    }

    val images = viewModel.mediaStoreImagesState.collectAsLazyPagingItems()
    val refresh = remember(Unit) {
        {
            images.refresh()
        }
    }

    fun refreshImages() {
        val previousMediaPermissionAccessLevel = mediaPermissionAccessLevel
        val currentMediaPermissionAccessLevel = appPermissionState.getMediaPermissionLevel()
        mediaPermissionAccessLevel = currentMediaPermissionAccessLevel

        when {
            currentMediaPermissionAccessLevel == MediaPermissionAccessLevel.None -> Unit
            previousMediaPermissionAccessLevel != currentMediaPermissionAccessLevel -> refresh()
            previousMediaPermissionAccessLevel == MediaPermissionAccessLevel.Partial -> refresh()
        }
    }

    LifecycleResumeEffect(Unit) {
        refreshImages()
        onPauseOrDispose { }
    }

    val context = LocalContext.current

    if (allPermissionsGranted) {
        HomeScreen(
            modifier = modifier,
            images = images,
            showManageMediaReselection = showManageMediaReselection,
        )
    } else {
        MissingMediaPermissionsScreen(
            modifier = modifier,
            shouldShowOpenSettingsOption = appPermissionState.shouldShowSettings,
            requestPermissions = { appPermissionState.requestPermissions() },
            gotoSettings = { context.openSettings() }
        )
    }
}

/**
 * A standalone screen to show the home screen.
 *
 * @param modifier The modifier needed to be applied to the composable
 */
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<MediaStoreImage>,
    showManageMediaReselection: Boolean,
) {
    val gridCount = remember(Unit) { 4 }
    val context by rememberUpdatedState(LocalContext.current)
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val placeholder = remember(Unit) { ColorPainter(backgroundColor) }
    val requestBuilder = remember(Unit) {
        ImageRequest.Builder(context)
            .allowHardware(true)
            .crossfade(true)
            .size(320, 320)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            if (images.loadState.refresh is LoadState.NotLoading) {
                LazyVerticalGrid(
                    modifier = modifier
                        .fillMaxSize(),
                    columns = GridCells.Fixed(gridCount),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(count = images.itemCount) {
                        val item = images[it]
                        if (item != null) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .aspectRatio(1f),
                                    model = requestBuilder
                                        .data(item.uri)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    placeholder = placeholder,
                                    error = placeholder,
                                    fallback = placeholder,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A standalone screen to show the missing permissions screen.
 *
 * @param modifier The modifier needed to be applied to the composable
 * @param shouldShowOpenSettingsOption Whether to show the open settings option
 * @param requestPermissions Callback for request permissions click
 * @param gotoSettings Callback for navigate to settings click
 */
@Composable
private fun MissingMediaPermissionsScreen(
    modifier: Modifier = Modifier,
    shouldShowOpenSettingsOption: Boolean,
    requestPermissions: () -> Unit,
    gotoSettings: () -> Unit,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(R.string.missing_media_permissions))
            if (shouldShowOpenSettingsOption) {
                Button(onClick = gotoSettings) {
                    Text(text = stringResource(R.string.open_settings))
                }
            } else {
                Button(onClick = requestPermissions) {
                    Text(text = stringResource(R.string.allow))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    SecureShareTheme {
        val images = List(40) {
            MediaStoreImage(
                id = it.toLong(),
                uri = "",
                dateTaken = 1000L,
                displayName = "",
                folderName = "",
                resolution = "1080X1080",
                size = 10000L
            )
        }
        val mediaStoreImageLazyPagingItems = MutableStateFlow(
            PagingData.from(
                data = images,
                sourceLoadStates = completedLoadState,
            )
        ).asStateFlow().collectAsLazyPagingItems()

        HomeScreen(
            images = mediaStoreImageLazyPagingItems,
            showManageMediaReselection = false,
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MissingMediaPermissionsScreenPreview() {
    SecureShareTheme {
        MissingMediaPermissionsScreen(
            shouldShowOpenSettingsOption = false,
            requestPermissions = {},
            gotoSettings = {}
        )
    }
}