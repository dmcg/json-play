package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionsTests {

    private val objectMapper = ObjectMapper()

    val expectedJson = """
        {
            "things" : [ "banana", "kumquat" ]
        }""".toJson(objectMapper)

    @Test
    fun `list of strings`() {
        data class ListOfStrings(
            val things: List<String>
        )

        val domain = ListOfStrings(listOf("banana", "kumquat"))
        val mapping = jsonMapping(
            ::ListOfStrings,
            prop("things", ListOfStrings::things),
        )
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `set of strings`() {
        data class SetOfStrings(
            val things: Set<String>
        )

        val domain = SetOfStrings(setOf("banana", "kumquat"))
        val mapping = jsonMapping(
            ::SetOfStrings,
            prop("things", SetOfStrings::things),
        )
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `list of objects`() {
        data class Thing(
            val field: String
        )
        data class Domain(
            val things: List<Thing>
        )
        val domain = Domain(listOf(Thing("banana"), Thing("kumquat")))
        val mapping = jsonMapping(
            ::Domain,
            prop(Domain::things, jsonMapping(::Thing, prop(Thing::field))),
        )

        val expectedJson = """
        {
            "things" : [
                { "field" : "banana" },
                { "field" : "kumquat" }
            ]
        }""".toJson(objectMapper)
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun schema() {

    }
}