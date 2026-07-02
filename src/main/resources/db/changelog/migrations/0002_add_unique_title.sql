-- liquibase formatted sql

-- changeset minakdan:0002-add-unique-title
ALTER TABLE notes ADD CONSTRAINT uq_notes_title UNIQUE (title);
-- rollback ALTER TABLE notes DROP CONSTRAINT uq_notes_title;
