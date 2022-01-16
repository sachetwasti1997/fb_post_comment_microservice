package com.sachet.post_review_microservice.controller

import com.sachet.post_review_microservice.model.PostComment
import com.sachet.post_review_microservice.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/post_comment")
class PostCommentController(
    val postCommentService: PostService
){

    @GetMapping("/comments/{postId}")
    fun getCommentsOnAPost(
        @PathVariable postId: String,
        @RequestParam(value = "page") page: Long,
        @RequestParam(value = "size") size: Long
    ): Mono<ResponseEntity<List<PostComment>>> {
        return postCommentService
            .getCommentsOnPost(postId, page, size)
            .map {
                if (it.isEmpty()){
                    return@map ResponseEntity(it, HttpStatus.NOT_FOUND)
                }
                ResponseEntity(it, HttpStatus.OK)
            }
            .log()
    }

    @PostMapping("/create")
    fun createComment(@RequestBody postComment: Mono<PostComment>): Mono<ResponseEntity<PostComment>>{
        return postCommentService
            .saveComment(postComment)
            .map {
                ResponseEntity(it, HttpStatus.OK)
            }
            .log()
    }

    @PutMapping("/{commentId}")
    fun updateComment(@PathVariable commentId:String, @RequestBody postComment: Mono<PostComment>): Mono<ResponseEntity<PostComment>>{
        return postCommentService
            .updateComment(commentId, postComment)
            .map {
                ResponseEntity(it, HttpStatus.OK)
            }
            .log()
    }

    @DeleteMapping("/{commentId}")
    fun deletePost(@PathVariable commentId: String):Mono<Void>{
        return postCommentService
            .deletePost(commentId)
    }
}