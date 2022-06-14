package com.srizan.flowpractice.data

import com.srizan.flowpractice.api.ApiService
import com.srizan.flowpractice.api.NetworkResource
import com.srizan.flowpractice.api.safeApiCall
import com.srizan.flowpractice.model.Post
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPosts(): NetworkResource<List<Post>> = safeApiCall { apiService.getPosts() }
    suspend fun getPostById(id: Int): NetworkResource<Post> =
        safeApiCall { apiService.getPostById(id) }
}