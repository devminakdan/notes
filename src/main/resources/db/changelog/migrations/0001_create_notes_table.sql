-- liquibase formatted sql

-- changeset minakdan:0001-create-notes-table
CREATE TABLE notes
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title      TEXT                     NOT NULL,
    content    TEXT                     NOT NULL,
    tags       TEXT[]                   NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
-- rollback DROP TABLE notes;
