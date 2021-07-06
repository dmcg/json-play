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

        val converter = converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        assertEquals(expectedJson, converter.toJson(domain, objectMapper))
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

        val innerConverter = converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        val converter = converter(
            ::Composite,
            jsonString(Composite::aString),
            jsonObject("child", Composite::thing, innerConverter),
        )
        assertEquals(expectedJson, converter.toJson(domain, objectMapper))
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




