package com.codeitsolo.secureshare.domain.repository.mediastore

import android.net.Uri
import androidx.paging.PagingData
import com.codeitsolo.secureshare.core.common.models.MediaStoreImage
import kotlinx.coroutines.flow.Flow

/**
 * Represents a repository media.
 */
interface MediaStoreRepository {

    /**
     * Get a paging source for media.
     *
     * @return A paging source for media.
     */
    fun getPicturePagingSource(): Flow<PagingData<MediaStoreImage>>

    /**
     * Get image from uri.
     *
     * @param uri The uri of image.
     *
     * @return The image from uri.
     */
    suspend fun getImageFromUri(uri: Uri): MediaStoreImage?

    /**
     * Observe on delete media.
     *
     * @param onDelete The callback for delete media.
     */
    fun onDeleteMediaListener(onDelete: (Long?) -> Unit)
}
