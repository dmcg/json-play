package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionsTests {

    private val objectMapper = ObjectMapper()

    private val expectedJson = """
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
            prop(ListOfStrings::things),
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

    data class Thing(
        val field: String
    )
    data class Domain(
        val strings: List<String>,
        val things: List<Thing>,
    )

    @Test
    fun `list of objects`() {
        val domain = Domain(
            emptyList(),
            listOf(Thing("banana"), Thing("kumquat"))
        )
        val mapping = jsonMapping(
            ::Domain,
            prop(Domain::strings),
            prop(Domain::things, jsonMapping(::Thing, prop(Thing::field))),
        )

        val expectedJson = """
        {
            "strings" : [],
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
        val mapping = jsonMapping(
            ::Domain,
            prop(Domain::strings),
            prop(Domain::things, jsonMapping(::Thing, prop(Thing::field))),
        )

        val expectedJson = """{
            "type" : "object",
            "properties" : {
                "strings" : {
                    "type" : "array",
                    "items" : {
                        "type" : "string"
                    }
                },
                "things" : {
                    "type" : "array",
                    "items" : {
                        "type" : "object",
                        "properties" : {
                            "field" : { "type" : "string" }
                        }
                    }
                }
            }
        }""".trimMargin().toJson(objectMapper)
        assertEquals(
            expectedJson,
            mapping.schema(objectMapper.asNodeFactory())
        )
    }
}