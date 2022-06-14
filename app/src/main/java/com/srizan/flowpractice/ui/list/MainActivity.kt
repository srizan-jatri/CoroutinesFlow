package com.srizan.flowpractice.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.srizan.flowpractice.databinding.ActivityMainBinding
import com.srizan.flowpractice.ui.dialog.DialogActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Create the recycler adapter and pass the callback function for item click
         * */
        val adapter = PostAdapter { id ->
            viewModel.accept.invoke(UiAction.OpenDialog(id))
        }
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerViewPosts.adapter = adapter
        binding.recyclerViewPosts.addItemDecoration(decoration)
        binding.swiperefresh.setOnRefreshListener {
            refreshPosts()
        }
        refreshPosts()


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isLoading)
                        binding.progressBar.visibility = View.VISIBLE
                    else binding.progressBar.visibility = View.GONE
                    adapter.postList = uiState.posts
                    if (uiState.openDialog){
                        openDialog(uiState.selectedPostId)
                    }
                }
            }
        }
    }

    private fun refreshPosts() {
        viewModel.accept.invoke(UiAction.FetchPosts)
        binding.swiperefresh.isRefreshing = false
    }
    private fun openDialog(id: Int){
        val intent = Intent(this, DialogActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetDialog()
    }
}