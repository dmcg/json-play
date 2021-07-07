package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionsTests {

    private val objectMapper = ObjectMapper()

    private data class Domain(
        val things: List<String>
    )
    private val mapping = jsonMapping(
        ::Domain,
        collectionProp("things", Domain::things),
    )

    @Test
    fun `round trip`() {
        val domain = Domain(listOf("banana", "kumquat"))
        val expectedJson = """
        {
            "things" : [ "banana", "kumquat" ]
        }""".toJson(objectMapper)
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }
}