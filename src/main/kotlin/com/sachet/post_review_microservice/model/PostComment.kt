package com.sachet.post_review_microservice.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Document(value = "comments")
data class PostComment(
    @Id
    val commentId: String ?= null,
    @field: NotNull(message = "Post Id cannot be null")
    var postId: String ?= null,
    @field: NotNull(message = "Comment cannot be null")
    var comment: String ?= null,
    @field: NotNull(message = "Comment generated date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var dateCreated: LocalDateTime ?= null
)