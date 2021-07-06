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

    interface Converter<D> {
        operator fun invoke(value: D): JsonNode
        operator fun invoke(node: JsonNode): D
    }

    fun <D> converter(putters: List<JsonProperty<D>>) = object: Converter<D> {
        override fun invoke(value: D): JsonNode =
            objectMapper.createObjectNode().apply {
                putters.forEach {
                    it.addTo(this, value)
                }
            }

        override fun invoke(node: JsonNode): D {
            return Domain("fred", 42) as D
        }
    }

    fun <D> converter(vararg putters: JsonProperty<D>) = converter(putters.asList())

    interface JsonProperty<D> {
        fun addTo(node: ObjectNode, value: D)
    }

    fun <D> jsonString(name: String, extractor: (D) -> String) =
        object : JsonProperty<D> {
            override fun addTo(node: ObjectNode, value: D) {
                node.put(name, extractor(value))
            }
        }

    fun <D> jsonInt(name: String, extractor: (D) -> Int) =
        object: JsonProperty<D> {
            override fun addTo(node: ObjectNode, value: D) {
                node.put(name, extractor(value))
            }
        }

    data class Domain(
        val name: String,
        val count: Int
    )
}





