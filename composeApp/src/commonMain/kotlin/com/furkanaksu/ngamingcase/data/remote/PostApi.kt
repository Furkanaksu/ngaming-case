package com.furkanaksu.ngamingcase.data.remote

import com.furkanaksu.ngamingcase.data.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PostApi(private val client: HttpClient) {

    suspend fun getPosts(): List<Post> = client.get("posts").body()

    suspend fun updatePost(post: Post): Post = client.put("posts/${post.id}") {
        contentType(ContentType.Application.Json)
        setBody(post)
    }.body()

    suspend fun deletePost(id: Int) {
        client.delete("posts/$id")
    }
}
