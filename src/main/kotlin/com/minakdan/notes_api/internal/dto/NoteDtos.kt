package com.minakdan.notes_api.internal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.UUID


internal data class NoteCreateRequest(
    @field:NotBlank
    @field:Size(min = 5, max = 30, message = "Note must have between 5 and 30 characters")
    val title: String,

    @field:NotBlank
    @field:Size(min = 5, max = 100, message = "Note must have between 5 and 100 characters")
    val content: String,

    @field:NotEmpty
    val tags: List<String>
)

internal data class NoteUpdateRequest(
    @field:NotBlank
    val id: UUID,

    @field:NotBlank
    @field:Size(min = 5, max = 30, message = "Note must have between 5 and 30 characters")
    val title: String,

    @field:NotBlank
    @field:Size(min = 5, max = 100, message = "Note must have between 5 and 100 characters")
    val content: String,

    @field:NotEmpty
    val tags: List<String>
)

internal data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

