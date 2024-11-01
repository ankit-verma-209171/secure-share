package com.codeitsolo.secureshare.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codeitsolo.secureshare.domain.repository.mediastore.MediaStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View Model for [HomeRoute]
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaStoreRepository: MediaStoreRepository
) : ViewModel() {

    val mediaStoreImagesState = mediaStoreRepository.getPicturePagingSource()
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )
}
