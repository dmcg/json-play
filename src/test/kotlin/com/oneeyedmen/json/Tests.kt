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

        val converter = converter(
            jsonString("name", Domain::name),
            jsonInt("count", Domain::count),
        )
        assertEquals(expected, converter(domain))
    }

    @Test
    fun `from JsonNode`() {
        val node: JsonNode = objectMapper.createObjectNode().apply {
            this.put("name", "fred")
            this.put("count", 42)
        }
        val expected = Domain("fred", 42)

        val converter = converter(
            jsonString("name", Domain::name),
            jsonInt("count", Domain::count),
        )
        assertEquals(expected, converter(node))
    }

    fun <D> converter(putters: List<(ObjectNode, D) -> Unit>): (D) -> JsonNode = { value ->
        objectMapper.createObjectNode().apply {
            putters.forEach {
                it(this, value)
            }
        }
    }

    fun <D> converter(vararg putters: (ObjectNode, D) -> Unit) = converter(putters.asList())

    fun <D> jsonString(name: String, extractor: (D) -> String): (ObjectNode, D) -> Unit = { node, value ->
        node.put(name, extractor(value))
    }

    fun <D> jsonInt(name: String, extractor: (D) -> Int): (ObjectNode, D) -> Unit = { node, value ->
        node.put(name, extractor(value))
    }

    data class Domain(
        val name: String,
        val count: Int
    )
}






