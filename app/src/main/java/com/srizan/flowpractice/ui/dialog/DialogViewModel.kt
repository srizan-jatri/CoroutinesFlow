package com.srizan.flowpractice.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srizan.flowpractice.api.NetworkResource
import com.srizan.flowpractice.data.PostRepository
import com.srizan.flowpractice.model.Post
import com.srizan.flowpractice.ui.list.UiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class PostUiState(
    val post: Post? = null,
    val isLoading: Boolean = false,
    val error: Boolean = false,
)

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    val accept: (UiAction) -> Unit

    init {
        accept = { action ->
            when (action) {
                is UiAction.FetchOnePost -> {
                    getPostById(action.id)
                }
                else -> {}
            }
        }
    }

    private fun getPostById(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update { ui ->
                    ui.copy(isLoading = true)
                }
                when (val resource = postRepository.getPostById(id)) {
                    is NetworkResource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                post = resource.data!!
                            )
                        }
                    }
                    is NetworkResource.Error -> {
                        _uiState.update { ui ->
                            ui.copy(
                                isLoading = false,
                                error = true
                            )
                        }
                    }
                }
            }
        }
    }
}