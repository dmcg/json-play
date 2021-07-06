package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `to JsonNode`() {
        val domain = Domain("fred", 42)
        val expected: JsonNode = objectMapper.createObjectNode().apply {
            this.put("name", "fred")
            this.put("count", 42)
        }

        val putters = listOf(
            jsonString("name", Domain::name),
            jsonInt("count", Domain::count),
        )
        val converter = object {
            fun toJson(value: Domain): JsonNode = objectMapper.createObjectNode().apply {
                putters.forEach {
                    it(this, value)
                }
            }
        }
        assertEquals(expected, converter.toJson(domain))
    }

    fun <D> jsonString(name: String, extractor: (D) -> String) = { node: ObjectNode, value: D ->
        node.put(name, extractor(value))
    }

    fun <D> jsonInt(name: String, extractor: (D) -> Int) = { node: ObjectNode, value: D ->
        node.put(name, extractor(value))
    }

    data class Domain(
        val name: String,
        val count: Int
    )
}






