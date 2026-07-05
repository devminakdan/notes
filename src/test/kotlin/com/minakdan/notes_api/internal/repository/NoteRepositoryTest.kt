package com.minakdan.notes_api.internal.repository

import com.minakdan.notes_api.TestcontainersConfiguration
import com.minakdan.notes_api.jooq.tables.references.NOTES
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class NoteRepositoryTest {

    @Autowired
    private lateinit var repository: NoteRepository

    @Autowired
    private lateinit var dsl: DSLContext

    @BeforeEach
    fun cleanDatabase() {
        dsl.deleteFrom(NOTES).execute()
    }

    @Nested
    inner class Create{
        @Test
        fun `create persists note and returns it`() {
            val created = repository.create(UUID.randomUUID(), "Test title", "Test content", listOf("kotlin"))

            assertEquals("Test title", created.title)
            assertEquals("Test content", created.content)
            assertEquals(listOf("kotlin"), created.tags)
        }

        @Test
        fun `create with duplicate title throws`() {
            repository.create(UUID.randomUUID(), "Duplicate", "content", emptyList())

            assertFailsWith<IllegalArgumentException> {
                repository.create(UUID.randomUUID(), "Duplicate", "other content", emptyList())
            }
        }
    }


    @Nested
    inner class GetById{
        @Test
        fun `getById returns persisted note`() {
            val created = repository.create(UUID.randomUUID(), "Findable", "content", emptyList())

            val found = repository.getById(created.id)

            assertEquals(created.id, found.id)
            assertEquals("Findable", found.title)
        }

        @Test
        fun `getById with unknown id throws`() {
            assertFailsWith<NoSuchElementException> {
                repository.getById(UUID.randomUUID())
            }
        }
    }


    @Nested
    inner class Update{
        @Test
        fun `update modifies existing note`() {
            val created = repository.create(UUID.randomUUID(), "Old title", "Old content", emptyList())

            val updated = repository.update(created.id, "New title", "New content", listOf("updated"))

            assertEquals("New title", updated.title)
            assertEquals("New content", updated.content)
            assertEquals(listOf("updated"), updated.tags)
        }

        @Test
        fun `update does not existing note`() {
            assertFailsWith<NoSuchElementException> {
                repository.update(UUID.randomUUID(), "New title", "New content", emptyList())
            }
        }

        @Test
        fun `update with duplicate title throws`(){
            val created1 = repository.create(UUID.randomUUID(), "Created1", "A", emptyList())
            val created2 = repository.create(UUID.randomUUID(), "Created2", "B", emptyList())

            assertFailsWith<IllegalArgumentException>{
                repository.update(created2.id, "Created1", "some content", emptyList())
            }
        }
    }


    @Nested
    inner class Delete{
        @Test
        fun `delete removes note`() {
            val created = repository.create(UUID.randomUUID(), "To delete", "content", emptyList())

            repository.delete(created.id)

            assertFailsWith<NoSuchElementException> {
                repository.getById(created.id)
            }
        }

        @Test
        fun `delete with unknown id throws`(){
            val id = UUID.randomUUID()
            val exception = assertFailsWith<NoSuchElementException>{
                repository.delete(id)
            }
            assertEquals("Note with id $id not found", exception.message)
        }
    }

    @Test
    fun `find all ordered by created_at descending`() {
        repository.create(UUID.randomUUID(), "First", "some content", emptyList())
        repository.create(UUID.randomUUID(), "Second", "some content", emptyList())
        repository.create(UUID.randomUUID(), "Third", "some content", emptyList())

        val titles = repository.findAll(0, 10).map { it.title }

        assertEquals(listOf("Third", "Second", "First"), titles)
    }

    @Test
    fun `find all with not empty offset`() {
        for (title in listOf("a", "b", "c", "d", "e")) {
            repository.create(UUID.randomUUID(), title, "some content", emptyList())
        }
        val titles = repository.findAll(2, 2).map { it.title }
        assertEquals(listOf("c", "b"), titles)
        assertEquals(5L, repository.count())
    }

}
