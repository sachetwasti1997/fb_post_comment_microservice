package com.sachet.post_review_microservice.service

import com.sachet.post_review_microservice.custom_exception.CommentNotFound
import com.sachet.post_review_microservice.custom_exception.PostDataException
import com.sachet.post_review_microservice.model.PostComment
import com.sachet.post_review_microservice.repository.PostCommentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.validation.Validator

@Service
class PostServiceImpl(
    val postCommentRepository: PostCommentRepository,
    val validator: Validator
) : PostService {

    override fun saveComment(postComment: Mono<PostComment>):Mono<PostComment>{
        return postComment
            .doOnNext {
                validateRequest(it)
            }
            .flatMap {
                postCommentRepository.save(it)
            }
    }

    override fun getCommentsOnPost(postId: String, page: Long, size: Long): Mono<List<PostComment>> {
        return postCommentRepository
            .findAll()
            .skip(page * size)
            .take(size)
            .collectList()
    }

    override fun updateComment(commentId: String, postComment: Mono<PostComment>): Mono<PostComment> {
        return postComment
            .doOnNext {
                validateRequest(it)
            }
            .flatMap { receivedPost ->

                postCommentRepository
                    .findById(commentId)
                    .switchIfEmpty(
                        Mono.error(CommentNotFound("No Comment Found"))
                    )
                    .flatMap { savedPost ->
                        savedPost.comment = receivedPost.comment
                        savedPost.dateCreated = receivedPost.dateCreated
                        postCommentRepository.save(savedPost)
                    }
            }
    }

    override fun deletePost(commentId: String) :Mono<Void>{
        return postCommentRepository
            .findById(commentId)
            .switchIfEmpty(
                Mono.error(CommentNotFound("Comment Not Found"))
            )
            .flatMap {
                postCommentRepository.delete(it)
            }
    }

    private fun validateRequest(postComment: PostComment){
        val constraintVoilations = validator.validate(postComment)
        if (constraintVoilations.size > 0) {
            val consStr = constraintVoilations
                .stream()
                .map {
                    it.message
                }
                .collect(Collectors.joining(","))
            throw PostDataException(consStr)
        }
    }

}