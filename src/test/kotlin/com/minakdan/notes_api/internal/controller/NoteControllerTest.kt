package com.minakdan.notes_api.internal.controller

import com.minakdan.notes_api.api.Note
import com.minakdan.notes_api.internal.dto.NoteCreateRequest
import com.minakdan.notes_api.internal.service.NoteService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test

@TestConfiguration
internal class NoteControllerTestConfig {
    @Bean
    fun noteService(): NoteService = mockk()
}

@WebMvcTest(NoteController::class)
@Import(NoteControllerTestConfig::class)
class NoteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var service: NoteService

    @BeforeEach
    fun resetMocks() {
        clearMocks(service)
    }

    private val testNote = Note(
        id = UUID.randomUUID(),
        title = "Test title",
        content = "Test content",
        tags = listOf("kotlin"),
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now(),
    )

    @Nested
    inner class CreateNote {
        @Test
        fun `valid request returns 201 with created note`() {
            every { service.create("Test title", "Test content", listOf("kotlin")) } returns testNote

            val request = NoteCreateRequest("Test title", "Test content", listOf("kotlin"))

            mockMvc.perform(
                post("/api/notes/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
        }

        @Test
        fun `blank title returns 400 with validation error`() {
            val request = NoteCreateRequest("", "Test content", listOf("kotlin"))

            mockMvc.perform(
                post("/api/notes/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.title").exists())
        }
    }

    @Nested
    inner class GetNote {
        @Test
        fun `existing id returns 200 with note`() {
            every { service.getById(testNote.id) } returns testNote

            mockMvc.perform(get("/api/notes/{id}", testNote.id))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(testNote.id.toString()))
        }

        @Test
        fun `unknown id returns 404`() {
            val id = UUID.randomUUID()
            every { service.getById(id) } throws NoSuchElementException("Note with id $id not found")

            mockMvc.perform(get("/api/notes/{id}", id))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class DeleteNote {
        @Test
        fun `existing id returns 204`() {
            every { service.delete(testNote.id) } just runs

            mockMvc.perform(delete("/api/notes/{id}", testNote.id))
                .andExpect(status().isNoContent)
        }
    }
}
