package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `simple round trip`() {
        val domain = Domain("fred", 42)
        val expectedJson = objectNode().apply {
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
    fun `nested round trip`() {
        val domain = Composite(
            "banana",
            Domain("fred", 42)
        )
        val expectedJson = objectNode().apply {
            put("aString", "banana")
            set<JsonNode>("child",
                objectNode().apply {
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

        with(JsonContext(objectMapper)) {
            assertEquals(expectedJson, converter.toJson(domain))
        }

        assertEquals(domain, converter.fromJson(expectedJson))
    }

    @Test
    fun `simple schema`() {
        val converter = jsonMapping(
            ::Domain,
            stringProp("the-name", Domain::name),
            intProp(Domain::count),
        )

        val expectedSchema = objectNode().apply {
            put("type", "object")
            set<JsonNode>("properties",
                objectNode().apply {
                    set<JsonNode>("the-name",
                        objectNode().apply {
                            put("type", "string")
                        }
                    )
                    set<JsonNode>("count",
                        objectNode().apply {
                            put("type", "number")
                        }
                    )
                }
            )
        }
        assertEquals(expectedSchema, converter.schema(objectMapper.asNodeFactory()))
    }

    fun objectNode() = objectMapper.createObjectNode()
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




