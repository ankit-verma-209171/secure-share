package com.codeitsolo.secureshare.domain.repository.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.codeitsolo.secureshare.core.common.dispatcher.AppDispatchers
import com.codeitsolo.secureshare.core.common.dispatcher.Dispatcher
import com.codeitsolo.secureshare.core.common.extensions.createCursor
import com.codeitsolo.secureshare.core.common.extensions.getColumnIndexOrNull
import com.codeitsolo.secureshare.core.common.models.MediaStoreImage
import com.codeitsolo.secureshare.domain.paging.mediastore.MediaStoreDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

/**
 * Concrete implementation of [MediaStoreRepository]
 *
 * @param context The application context.
 * @param ioDispatcher The coroutine dispatcher for IO operations.
 */
class MediaStoreRepositoryImpl @Inject constructor(
    private val context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : MediaStoreRepository {

    private var mediaDataSource: MediaStoreDataSource = MediaStoreDataSource(
        context = context,
        ioDispatcher = ioDispatcher
    )

    private var onDeleteMediaListener: (Long?) -> Unit = {}

    private val mediaObserver by lazy {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    super.onChange(selfChange, uri, flags) // For API level R and above
                    val isInsert =
                        flags.and(ContentResolver.NOTIFY_INSERT) == ContentResolver.NOTIFY_INSERT
                    val isDelete =
                        flags.and(ContentResolver.NOTIFY_DELETE) == ContentResolver.NOTIFY_DELETE
                    val isUpdate =
                        flags.and(ContentResolver.NOTIFY_UPDATE) == ContentResolver.NOTIFY_UPDATE
                    if (isDelete) {
                        val id = uri?.lastPathSegment?.toLongOrNull()
                        getNewCropFile("$id").delete()
                        onDeleteMediaListener(id)
                    }
                    if (isInsert || isDelete || isUpdate) {
                        invalidateMediaStoreDataSource()
                    }
                } else {
                    // TODO : Need to find a way to identify a change below API level R
                    invalidateMediaStoreDataSource()
                }
            }
        }
    }

    init {
        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            mediaObserver
        )
    }

    override fun getPicturePagingSource(): Flow<PagingData<MediaStoreImage>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            )
        ) {
            renewMediaSourceDataSource()
            mediaDataSource
        }
            .flow
            .flowOn(ioDispatcher)

    override suspend fun getImageFromUri(uri: Uri): MediaStoreImage? {
        return context.createCursor(uri = uri)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrNull(0)
            val displayNameColumn = cursor.getColumnIndexOrNull(1)
            val dateTakenColumn = cursor.getColumnIndexOrNull(2)
            val bucketDisplayName = cursor.getColumnIndexOrNull(3)
            val resolutionColumn = cursor.getColumnIndexOrNull(4)
            val sizeColumn = cursor.getColumnIndexOrNull(5)

            if (cursor.count == 0) return@use null
            cursor.moveToNext()

            val id = idColumn?.let { cursor.getLongOrNull(it) } ?: return@use null
            val dateTaken = dateTakenColumn?.let { cursor.getLong(it) }
            val displayName = displayNameColumn?.let { cursor.getString(it) }
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val folderName = bucketDisplayName?.let { cursor.getString(it) }
            val resolution = resolutionColumn?.let { cursor.getString(it) }
            val size = sizeColumn?.let { cursor.getLong(it) }

            MediaStoreImage(
                uri = contentUri.toString(),
                dateTaken = dateTaken ?: 0L,
                displayName = displayName.orEmpty(),
                id = id,
                folderName = folderName.orEmpty(),
                resolution = resolution.orEmpty(),
                size = size ?: 0
            )
        }
    }

    override fun onDeleteMediaListener(onDelete: (Long?) -> Unit) {
        onDeleteMediaListener = onDelete
    }

    private fun getNewCropFile(id: String): File =
        File(
            getMediaStoreCropDirectory(),
            "${CROP_FILE_PREFIX}$id.jpg"
        )

    private fun getMediaStoreDirectory(): File =
        File(context.cacheDir, MEDIA_STORE_DIR_NAME).also { it.mkdirs() }

    private fun getMediaStoreCropDirectory(): File =
        File(getMediaStoreDirectory(), MEDIA_STORE_DIR_CROP_NAME).also { it.mkdirs() }

    private fun invalidateMediaStoreDataSource() {
        val oldMediaStoreDataSource = mediaDataSource
        renewMediaSourceDataSource()
        oldMediaStoreDataSource.invalidate()
    }

    private fun renewMediaSourceDataSource() {
        mediaDataSource = MediaStoreDataSource(
            context = context,
            ioDispatcher = ioDispatcher
        )
    }

    companion object {
        private const val GRID_COUNT = 4
        private const val PAGE_SIZE = GRID_COUNT * 4
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE
        private const val PREFETCH_DISTANCE = GRID_COUNT * 1
        private const val MEDIA_STORE_DIR_NAME = "mediastore"
        private const val MEDIA_STORE_DIR_CROP_NAME = "crop"
        private const val APP_FILE_PREFIX = "secureshare-"
        private const val CROP_FILE_PREFIX = "${APP_FILE_PREFIX}crop-"
    }
}
