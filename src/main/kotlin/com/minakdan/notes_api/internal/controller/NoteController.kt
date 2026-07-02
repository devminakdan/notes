package com.minakdan.notes_api.internal.controller

import com.minakdan.notes_api.api.Note
import com.minakdan.notes_api.internal.dto.NoteCreateRequest
import com.minakdan.notes_api.internal.dto.NoteUpdateRequest
import com.minakdan.notes_api.internal.dto.PageResponse
import com.minakdan.notes_api.internal.service.NoteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/notes")
internal class NoteController(
    private val service: NoteService
) {
    @PostMapping("/create")
    fun createNote(@Valid @RequestBody req: NoteCreateRequest): ResponseEntity<Note> {
        val created = service.create(req.title, req.content, req.tags)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/update")
    fun updateNote(@Valid @RequestBody req: NoteUpdateRequest): ResponseEntity<Note> {
        val updated = service.update(req.id, req.title, req.content, req.tags)
        return ResponseEntity.ok(updated)
    }

    @GetMapping("/{id}")
    fun getNote(@PathVariable id: UUID): ResponseEntity<Note> =
        ResponseEntity.ok(service.getById(id))

    @GetMapping("/pagination")
    fun getByPagination(
        @RequestParam("page") page: Int,
        @RequestParam("limit") limit: Int): ResponseEntity<PageResponse<Note>> =
        ResponseEntity.ok(service.findAll(page, limit))

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}
