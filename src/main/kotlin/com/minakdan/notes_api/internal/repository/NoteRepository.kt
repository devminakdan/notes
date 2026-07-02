package com.minakdan.notes_api.internal.repository

import com.minakdan.notes_api.api.Note
import com.minakdan.notes_api.jooq.tables.references.NOTES
import org.jooq.DSLContext
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
internal class NoteRepository(
    private val dsl: DSLContext,
) {
    fun create(id: UUID, title: String, content: String, tags: List<String>): Note {
        try {
            val record = dsl.insertInto(NOTES)
                .set(NOTES.ID, id)
                .set(NOTES.TITLE, title)
                .set(NOTES.CONTENT, content)
                .set(NOTES.TAGS, tags.toTypedArray())
                .returning()
                .fetchSingle()

            return record.toNote()
        } catch (_: DuplicateKeyException) {
            throw IllegalArgumentException("Note with title '$title' already exists")
        }
    }

    fun update(id: UUID, title: String, content: String, tags: List<String>): Note {
        try {
            val now = OffsetDateTime.now()

            val updated = dsl.update(NOTES)
                .set(NOTES.TITLE, title)
                .set(NOTES.CONTENT, content)
                .set(NOTES.TAGS, tags.toTypedArray())
                .set(NOTES.UPDATED_AT, now)
                .where(NOTES.ID.eq(id))
                .returning()
                .fetchOne() ?: throw NoSuchElementException("Note with id $id not found")

            return updated.toNote()
        } catch (_: DuplicateKeyException) {
            throw IllegalArgumentException("Note with title '$title' already exists")
        }
    }

    fun getById(id: UUID): Note {
        val record = dsl.selectFrom(NOTES)
            .where(NOTES.ID.eq(id))
            .fetchOne() ?: throw NoSuchElementException("Note with id $id not found")
        return record.toNote()
    }

    fun findAll(offset: Int, limit: Int): List<Note> =
        dsl.selectFrom(NOTES)
            .orderBy(NOTES.CREATED_AT.desc())
            .offset(offset)
            .limit(limit)
            .fetch { it.toNote() }

    fun count(): Long =
        dsl.fetchCount(NOTES).toLong()

    private fun org.jooq.Record.toNote() = Note(
        id = get(NOTES.ID)!!,
        title = get(NOTES.TITLE)!!,
        content = get(NOTES.CONTENT)!!,
        tags = get(NOTES.TAGS)?.filterNotNull() ?: emptyList(),
        createdAt = get(NOTES.CREATED_AT)!!,
        updatedAt = get(NOTES.UPDATED_AT)!!,
    )

    fun delete(id: UUID) {
        val rowsAffected = dsl.deleteFrom(NOTES)
            .where(NOTES.ID.eq(id))
            .execute()

        if (rowsAffected == 0) throw NoSuchElementException("Note with id $id not found")
    }
}
