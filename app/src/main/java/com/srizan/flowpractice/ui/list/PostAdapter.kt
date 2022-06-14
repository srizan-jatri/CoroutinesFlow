package com.srizan.flowpractice.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srizan.flowpractice.databinding.ItemPostBinding
import com.srizan.flowpractice.model.Post

class PostAdapter(
    val onItemClick: (id: Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    var postList: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.textViewTitle.text = post.title
            binding.textViewBody.text = post.body

            itemView.setOnClickListener {
                onItemClick.invoke(post.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder =
        ViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size
}