package com.srizan.flowpractice.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.srizan.flowpractice.R
import com.srizan.flowpractice.databinding.ActivityDialogBinding
import com.srizan.flowpractice.ui.list.UiAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "DialogActivity"

@AndroidEntryPoint
class DialogActivity : FragmentActivity() {
    private val viewModel: DialogViewModel by viewModels()
    private lateinit var binding: ActivityDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val id = intent.getIntExtra("id", 0)
            viewModel.accept.invoke(UiAction.FetchOnePost(id))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isLoading)
                        binding.progressBar.visibility = View.VISIBLE
                    else if (uiState.error) {
                        binding.progressBar.visibility = View.GONE
                        binding.ivError.visibility = View.VISIBLE
                        Toast.makeText(this@DialogActivity,"Something went wrong, please check your Internet connection!", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.ivError.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        uiState.post?.let {
                            binding.textViewId.text = getString(R.string.id, it.id)
                            binding.textViewUserId.text = getString(R.string.user_id, it.userId)
                            binding.textViewTitle.text = getString(R.string.title, it.title)
                            binding.textViewBody.text = getString(R.string.body, it.body)
                        }
                    }
                }
            }
        }
    }
}