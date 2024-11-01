package com.codeitsolo.secureshare.domain.paging.mediastore

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.codeitsolo.secureshare.core.common.dispatcher.AppDispatchers
import com.codeitsolo.secureshare.core.common.dispatcher.Dispatcher
import com.codeitsolo.secureshare.core.common.extensions.createCursor
import com.codeitsolo.secureshare.core.common.extensions.getColumnIndexOrNull
import com.codeitsolo.secureshare.core.common.models.MediaStoreImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Represents a data source for media store.
 *
 * @param context The application context.
 * @param ioDispatcher The coroutine dispatcher for IO operations.
 */
class MediaStoreDataSource(
    private val context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PagingSource<Int, MediaStoreImage>() {

    override fun getRefreshKey(state: PagingState<Int, MediaStoreImage>): Int? =
        state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaStoreImage> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val pictures = withContext(ioDispatcher) {
            fetchPagePicture(pageSize, pageNumber * pageSize)
        }
        val prevKey = if (pageNumber > 0) pageNumber.minus(1) else null
        val nextKey = if (pictures.isNotEmpty()) pageNumber.plus(1) else null
        return LoadResult.Page(
            data = pictures,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    private fun fetchPagePicture(limit: Int, offset: Int): List<MediaStoreImage> {
        val pictures = mutableListOf<MediaStoreImage>()

        context.createCursor(limit, offset)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrNull(0)
            val displayNameColumn = cursor.getColumnIndexOrNull(1)
            val dateTakenColumn = cursor.getColumnIndexOrNull(2)
            val bucketDisplayName = cursor.getColumnIndexOrNull(3)
            val resolutionColumn = cursor.getColumnIndexOrNull(4)
            val sizeColumn = cursor.getColumnIndexOrNull(5)

            while (cursor.moveToNext()) {
                val id = idColumn?.let { cursor.getLongOrNull(it) }
                val dateTaken = dateTakenColumn?.let { cursor.getLongOrNull(it) }
                val displayName = displayNameColumn?.let { cursor.getStringOrNull(it) }
                val contentUri = id?.let {
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        it
                    )
                }
                val folderName = bucketDisplayName?.let { cursor.getString(it) }
                val resolution = resolutionColumn?.let { cursor.getStringOrNull(it) }
                val size = sizeColumn?.let { cursor.getLong(it) }

                pictures.add(
                    MediaStoreImage(
                        uri = contentUri.toString(),
                        dateTaken = dateTaken ?: 0L,
                        displayName = displayName.orEmpty(),
                        id = id ?: 0,
                        folderName = folderName.orEmpty(),
                        resolution = resolution.orEmpty(),
                        size = size ?: 0
                    )
                )
            }
        }
        return pictures
    }
}
