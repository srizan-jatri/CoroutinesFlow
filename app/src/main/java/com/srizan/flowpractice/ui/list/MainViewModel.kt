package com.srizan.flowpractice.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srizan.flowpractice.api.NetworkResource
import com.srizan.flowpractice.data.PostRepository
import com.srizan.flowpractice.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

private const val TAG = "ASD"

data class PostListUiState(
    val posts: List<Post> = emptyList(),
    val post: Post? = null,
    val isLoading: Boolean = false,
    val openDialog: Boolean = false,
    val selectedPostId: Int = 0
)

sealed class UiAction {
    object FetchPosts : UiAction()
    data class FetchOnePost(val id: Int) : UiAction()
    data class OpenDialog(val id: Int) : UiAction()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()


    val accept: (UiAction) -> Unit

    init {
        accept = { action ->
            when (action) {
                is UiAction.FetchPosts -> getPosts()
                is UiAction.OpenDialog -> {
                    _uiState.update {
                        it.copy(
                            openDialog = true,
                            selectedPostId = action.id
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private var makeRequestJob: Job? = null

    private fun getPosts() {
        if (makeRequestJob != null) return
        makeRequestJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _uiState.update { it.copy(isLoading = true) }
                    when (val result = postRepository.getPosts()) {
                        is NetworkResource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    posts = result.data!!
                                )
                            }
                        }
                        else -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }

                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                } finally {
                    makeRequestJob = null
                }
            }
        }
    }

    fun resetDialog(){
        _uiState.update {
            it.copy(
                openDialog = false,
            )
        }
    }
}

