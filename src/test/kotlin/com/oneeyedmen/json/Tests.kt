package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    private val objectMapper = ObjectMapper()

    private data class Domain(
        val name: String,
        val count: Int
    ) {
        constructor() : this("fred", 42) // check we can pick the right ctor
    }

    private data class Parent(
        val aString: String,
        val thing: Domain
    )

    @Test
    fun `simple round trip`() {
        val domain = Domain("fred", 42)
        val mapping = jsonMapping(
            ::Domain,
            prop("the-name", Domain::name),
            prop(Domain::count),
        )

        val expectedJson = """
        {
            "the-name" : "fred",
            "count" : 42
        }""".toJson(objectMapper)
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `nested round trip`() {
        val domain = Parent(
            "banana",
            Domain("fred", 42)
        )
        val mapping = jsonMapping(
            ::Parent,
            prop(Parent::aString),
            prop(
                "child",
                Parent::thing,
                jsonMapping(
                    ::Domain,
                    prop("the-name", Domain::name),
                    prop(Domain::count),
                )
            ),
        )

        with(JsonContext(objectMapper)) {
            val expectedJson = """{
                "aString" : "banana",
                "child" : {
                    "the-name" : "fred",
                    "count" : 42
                }
            }""".toJson(objectMapper)
            assertEquals(expectedJson, mapping.toJson(domain))
            assertEquals(domain, mapping.fromJson(expectedJson))
        }

    }
}

fun String.toJson(objectMapper: ObjectMapper): JsonNode =
    objectMapper.readTree(this.trimIndent())

