package com.sachet.post_review_microservice.controller

import com.sachet.post_review_microservice.model.PostComment
import com.sachet.post_review_microservice.repository.PostCommentRepository
import org.bson.json.JsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class PostCommentControllerTest
@Autowired
constructor(
    val postCommentRepository: PostCommentRepository,
    val webTestClient: WebTestClient
) {

    private val id = UUID.randomUUID().toString()
    private val postcomment_base_url = "/api/v1/post_comment"

    @BeforeEach
    fun setUp() {
        val postComments = listOf<PostComment>(
            PostComment(
                commentId = UUID.randomUUID().toString(),
                postId = id,
                comment = "The post is awesome1",
                dateCreated = LocalDateTime.now()
            ),
            PostComment(
                commentId = UUID.randomUUID().toString(),
                postId = id,
                comment = "This is another awesome post1",
                dateCreated = LocalDateTime.now()
            ),
            PostComment(
                commentId = UUID.randomUUID().toString(),
                postId = id,
                comment = "This is yet another awesome post1",
                dateCreated = LocalDateTime.now()
            ),
            PostComment(
                commentId = UUID.randomUUID().toString(),
                postId = id,
                comment = "The post is awesome2",
                dateCreated = LocalDateTime.now()
            ),
            PostComment(
                commentId = UUID.randomUUID().toString(),
                postId = id,
                comment = "This is another awesome post2",
                dateCreated = LocalDateTime.now()
            ),
            PostComment(
                commentId = id,
                postId = id,
                comment = "",
                dateCreated = LocalDateTime.now()
            )
        )
        postCommentRepository.saveAll(postComments).blockLast()
    }

    @AfterEach
    fun tearDown() {
        postCommentRepository.deleteAll().block()
    }

    @Test
    fun getCommentsOnPost(){
        val page = 0
        val size = 3
        webTestClient
            .get()
            .uri{
                it
                    .path("$postcomment_base_url/comments/$id/$page/$size")
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(PostComment::class.java)
            .consumeWith<WebTestClient.ListBodySpec<PostComment>> {
                assertTrue(it.responseBody?.size == 3)
            }
    }

    @Test
    fun getCommentsOnPostPageNumberMoreReceiveNotFound(){
        val page = 2
        val size = 3
        webTestClient
            .get()
            .uri{
                it
                    .path("$postcomment_base_url/comments/$id/$page/$size")
                    .build()
            }
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun savePostComment(){
        val comment = PostComment(
            postId = UUID.randomUUID().toString(),
            comment = "Awesome Comment for an Awesome Post",
            dateCreated = LocalDateTime.now()
        )
        webTestClient
            .post()
            .uri("${postcomment_base_url}/create")
            .bodyValue(comment)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(PostComment::class.java)
            .consumeWith {
                val pst = it.responseBody
                assertEquals("Awesome Comment for an Awesome Post", pst?.comment)
                assertNotNull(pst?.postId)
            }
    }

    @Test
    fun savePostCommentWithNullParams(){
        val comment = PostComment(
            postId = UUID.randomUUID().toString(),
            dateCreated = LocalDateTime.now()
        )
        webTestClient
            .post()
            .uri("${postcomment_base_url}/create")
            .bodyValue(comment)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .consumeWith {
                val response = it.responseBody
                assertTrue(response?.contains("errorCode")!!)
            }
    }

    @Test
    fun updatePost(){
        val postComment = PostComment(
            postId = id,
            comment = "This is random updated comment!",
            dateCreated = LocalDateTime.now()
        )
        webTestClient
            .put()
            .uri("${postcomment_base_url}/$id")
            .bodyValue(postComment)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(PostComment::class.java)
            .consumeWith {
                var updatedResponse = it.responseBody
                assertEquals("This is random updated comment!", updatedResponse?.comment)
            }
    }

    @Test
    fun updatePostCommentWithNullParamsReceiveBadRequest(){
        val comment = PostComment(
            postId = UUID.randomUUID().toString(),
            dateCreated = LocalDateTime.now()
        )
        webTestClient
            .put()
            .uri("${postcomment_base_url}/$id")
            .bodyValue(comment)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .consumeWith {
                val response = it.responseBody
                println(response)
                assertTrue(response?.contains("errorCode")!!)
                assertTrue(response.contains("400 BAD_REQUEST"))
            }
    }

    @Test
    fun updatePostCommentWithCommentNotExistReceiveError(){
        val comment = PostComment(
            postId = UUID.randomUUID().toString(),
            comment = "This is random updated comment!",
            dateCreated = LocalDateTime.now()
        )
        webTestClient
            .put()
            .uri("${postcomment_base_url}/${UUID.randomUUID()}")
            .bodyValue(comment)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody(String::class.java)
            .consumeWith {
                val response = it.responseBody
                assertTrue(response?.contains("errorCode")!!)
                assertTrue(response.contains("404 NOT_FOUND"))
            }
    }

    @Test
    fun deletePost(){
        webTestClient
            .delete()
            .uri("${postcomment_base_url}/$id")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun deletePostPostNotExistReceiveError(){
        webTestClient
            .delete()
            .uri("${postcomment_base_url}/${UUID.randomUUID()}")
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody(String::class.java)
            .consumeWith {
                var response = it.responseBody
                assertTrue(response?.contains("errorCode")!!)
                assertTrue(response.contains("404 NOT_FOUND"))
            }
    }

}











