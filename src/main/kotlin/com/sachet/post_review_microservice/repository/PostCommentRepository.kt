package com.sachet.post_review_microservice.repository

import com.sachet.post_review_microservice.model.PostComment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostCommentRepository: ReactiveMongoRepository<PostComment, String> {

    fun getPostCommentByPostId(postId: String)

}