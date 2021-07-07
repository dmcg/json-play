package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionsTests {

    private val objectMapper = ObjectMapper()

    private data class ListOfStrings(
        val things: List<String>
    )
    private data class SetOfStrings(
        val things: Set<String>
    )
    val expectedJson = """
        {
            "things" : [ "banana", "kumquat" ]
        }""".toJson(objectMapper)

    @Test
    fun `list of strings`() {
        val domain = ListOfStrings(listOf("banana", "kumquat"))
        val mapping = jsonMapping(
            ::ListOfStrings,
            collectionProp("things", ListOfStrings::things),
        )
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `set of strings`() {
        val domain = SetOfStrings(setOf("banana", "kumquat"))
        val mapping = jsonMapping(
            ::SetOfStrings,
            collectionProp("things", SetOfStrings::things),
        )
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }
}