package com.minakdan.notes_api.api

import java.time.OffsetDateTime
import java.util.UUID

data class Note(
    val id: UUID,
    val title: String,
    val content: String,
    val tags: List<String>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
