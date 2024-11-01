package com.codeitsolo.secureshare.core.common.paging

import androidx.paging.LoadState
import androidx.paging.LoadStates

/**
 * Represents a completed load state for a paging source.
 */
val completedLoadState = LoadStates(
    refresh = LoadState.NotLoading(endOfPaginationReached = true),
    prepend = LoadState.NotLoading(endOfPaginationReached = true),
    append = LoadState.NotLoading(endOfPaginationReached = true),
)
