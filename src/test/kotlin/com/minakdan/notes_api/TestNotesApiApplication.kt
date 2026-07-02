package com.minakdan.notes_api

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<NotesApiApplication>().with(TestcontainersConfiguration::class).run(*args)
}
