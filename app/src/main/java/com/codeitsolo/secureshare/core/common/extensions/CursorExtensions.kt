package com.codeitsolo.secureshare.core.common.extensions

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf

/**
 * Projection for [MediaStore.Images.Media] query for API level 29 and above.
 */
private val projectionForApiMoreThanQ by lazy {
    arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.RESOLUTION,
        MediaStore.Images.Media.SIZE
    )
}

/**
 * Projection for [MediaStore.Images.Media] query for API level 28 and below.
 */
private val projectionForApiLessThanR by lazy {
    arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )
}

/**
 * Get the column index for the given projection column index.
 *
 * @param projectionColumnIndex The projection column index
 *
 * @return The column index or null if the column index is not found
 */
internal fun Cursor.getColumnIndexOrNull(projectionColumnIndex: Int): Int? {
    val minApi30 = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
    val columnName = if (minApi30) {
        projectionForApiMoreThanQ.getOrNull(projectionColumnIndex)
    } else {
        projectionForApiLessThanR.getOrNull(projectionColumnIndex)
    } ?: return null
    return try {
        getColumnIndexOrThrow(columnName)
    } catch (e: Exception) {
        null
    }
}

/**
 * Create a cursor for the given limit and offset.
 *
 * @param limit The limit of the cursor
 * @param offset The offset of the cursor
 *
 * @return The cursor
 */
internal fun Context.createCursor(limit: Int = 1, offset: Int = 0, uri: Uri? = null): Cursor? {
    val uriToQuery = when {
        uri != null -> uri
        else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    return try {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val bundle = bundleOf(
                ContentResolver.QUERY_ARG_OFFSET to offset,
                ContentResolver.QUERY_ARG_LIMIT to limit,
                ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.Media.DATE_ADDED),
                ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            contentResolver.query(
                uriToQuery,
                projectionForApiMoreThanQ,
                bundle,
                null
            )
        } else {
            contentResolver.query(
                uriToQuery,
                projectionForApiLessThanR,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT $limit OFFSET $offset",
                null
            )
        }
    } catch (e: SecurityException) {
        null
    }
}
