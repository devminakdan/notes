package com.minakdan.notes_api.internal.service

import com.minakdan.notes_api.api.Note
import com.minakdan.notes_api.internal.repository.NoteRepository
import com.minakdan.notes_api.internal.service.NoteService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class NoteServiceTest {
    @MockK
    private lateinit var repository: NoteRepository

    @InjectMockKs
    private lateinit var service: NoteService

    private val testNote = Note(
        id = UUID.randomUUID(),
        title = "Test",
        content = "Content",
        tags = listOf("Kotlin"),
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now(),
    )

    @Test
    fun `create returns new note`() {
        every {
            repository.create(any(), eq("Test"), eq("Content"), eq(listOf("Kotlin")))
        } returns testNote

        val result = service.create("Test", "Content", listOf("Kotlin"))

        assertEquals("Test", result.title)
        assertEquals("Content", result.content)
        verify(exactly = 1) { repository.create(any(), any(), any(), any())}
    }

    @Test
    fun `getById returns note with id`() {
        every { repository.getById(any()) } returns testNote

        val result = service.getById(testNote.id)

        assertEquals(testNote.id, result.id)

        verify(exactly = 1) { repository.getById(any()) }
    }

    @Test
    fun `update note returns updated note`() {
        every {
            repository.update(testNote.id, "New title", "New content", listOf("kotlin", "spring"))
        } returns testNote.copy(title = "New title", content = "New content", tags = listOf("kotlin", "spring"))

        val result = service.update(testNote.id, "New title", "New content", listOf("kotlin", "spring"))

        assertEquals("New title", result.title)
        assertEquals("New content", result.content)
        verify(exactly = 1) { repository.update(testNote.id, any(), any(), any()) }
    }
}