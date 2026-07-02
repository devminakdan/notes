package com.minakdan.notes_api.internal.service

import com.minakdan.notes_api.api.Note
import com.minakdan.notes_api.internal.dto.PageResponse
import com.minakdan.notes_api.internal.repository.NoteRepository
import org.springframework.stereotype.Service
import java.util.UUID



@Service
internal class NoteService(
    private val repository: NoteRepository
) {
    fun create(title: String, content: String, tags: List<String>): Note =
        repository.create(UUID.randomUUID(), title, content, tags)

    fun update(id: UUID, title: String, content: String, tags: List<String>): Note =
        repository.update(id, title, content, tags)

    fun getById(id: UUID): Note =
        repository.getById(id)

    fun findAll(page: Int, limit: Int): PageResponse<Note> {
        val offset = page * limit
        val notes = repository.findAll(offset, limit)
        val count = repository.count()
        return PageResponse(notes, page, limit, count, ((count + limit - 1) / limit).toInt())
    }

    fun delete(id: UUID) =
        repository.delete(id)
}
