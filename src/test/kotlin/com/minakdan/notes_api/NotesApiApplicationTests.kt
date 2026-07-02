package com.minakdan.notes_api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class NotesApiApplicationTests {

    @Test
    fun contextLoads() {
    }

}
