package com.sachet.post_review_microservice.service

import com.sachet.post_review_microservice.model.PostComment
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PostService {

    fun saveComment(postComment: Mono<PostComment>): Mono<PostComment>
    fun getCommentsOnPost(postId:String, page: Long, size: Long): Mono<List<PostComment>>
    fun updateComment(commentId:String, postComment: Mono<PostComment>): Mono<PostComment>
    fun deletePost(commentId: String):Mono<Void>

}