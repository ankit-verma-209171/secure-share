package com.codeitsolo.secureshare.core.common.models

import kotlinx.serialization.Serializable

/**
 * Represents a media store image.
 *
 * @param uri The URI of the image.
 * @param dateTaken The date when the image was taken.
 * @param displayName The display name of the image.
 * @param id The ID of the image.
 * @param folderName The name of the folder the image belongs to.
 * @param resolution The resolution of the image.
 * @param size The size of the image.
 */
@Serializable
data class MediaStoreImage(
    val uri: String,
    val dateTaken: Long,
    val displayName: String,
    val id: Long,
    val folderName: String,
    val resolution: String,
    val size: Long
)
