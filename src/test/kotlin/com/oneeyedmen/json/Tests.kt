package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `round trip simple`() {
        val domain = Domain("fred", 42)
        val expectedJson = objectMapper.createObjectNode().apply {
            put("the-name", "fred")
            put("count", 42)
        }

        val converter = jsonMapping(
            ::Domain,
            stringProp("the-name", Domain::name),
            intProp(Domain::count),
        )
        assertEquals(expectedJson, converter.toJson(domain, objectMapper.asNodeFactory()))
        assertEquals(domain, converter.fromJson(expectedJson))
    }

    @Test
    fun `round trip sub-object`() {
        val domain = Composite(
            "banana",
            Domain("fred", 42)
        )
        val expectedJson = objectMapper.createObjectNode().apply {
            put("aString", "banana")
            set<JsonNode>("child",
                objectMapper.createObjectNode().apply {
                    put("the-name", "fred")
                    put("count", 42)
                }
            )
        }

        val converter = jsonMapping(
            ::Composite,
            prop(Composite::aString),
            prop(
                "child",
                Composite::thing,
                jsonMapping(
                    ::Domain,
                    prop("the-name", Domain::name),
                    prop(Domain::count),
                )
            ),
        )

        with (JsonContext(objectMapper)) {
            assertEquals(expectedJson, converter.toJson(domain))
        }

        assertEquals(domain, converter.fromJson(expectedJson))
    }
}

data class Domain(
    val name: String,
    val count: Int
) {
    constructor() : this("fred", 42)
}

data class Composite(
    val aString: String,
    val thing: Domain
)




