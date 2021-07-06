package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `to JsonNode`() {
        val domain = Domain("fred", 42)
        val expected: JsonNode = objectMapper.createObjectNode().apply {
            this.put("the-name", "fred")
            this.put("count", 42)
        }

        val converter = converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        assertEquals(expected, converter(domain))
    }

    @Test
    fun `from JsonNode`() {
        val node: JsonNode = objectMapper.createObjectNode().apply {
            this.put("the-name", "fred")
            this.put("count", 42)
        }
        val expected = Domain("fred", 42)

        val converter = converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        assertEquals(expected, converter(node))
    }

    interface Converter<D> {
        operator fun invoke(value: D): JsonNode
        operator fun invoke(node: JsonNode): D
    }

    fun <D, P1, P2> converter(
        ctor: (P1, P2) -> D,
        p1: JsonProperty<D, P1>,
        p2: JsonProperty<D, P2>
    ) = object: Converter<D> {
        override fun invoke(value: D): JsonNode =
            objectMapper.createObjectNode().apply {
                p1.addTo(this, value)
                p2.addTo(this, value)
            }

        override fun invoke(node: JsonNode): D =
            ctor(
                p1.extractFrom(node),
                p2.extractFrom(node)
            )
    }

    interface JsonProperty<D, T> {
        fun addTo(node: ObjectNode, value: D)
        fun extractFrom(node: JsonNode): T
    }

    fun <D> jsonString(name: String, extractor: (D) -> String) =
        object : JsonProperty<D, String> {
            override fun addTo(node: ObjectNode, value: D) {
                node.put(name, extractor(value))
            }

            override fun extractFrom(node: JsonNode) =
                node.get(name).asText()
        }

    fun <D> jsonInt(property: KProperty1<D, Int>) = jsonInt(property.name, property::get)

    fun <D> jsonInt(name: String, extractor: (D) -> Int) =
        object: JsonProperty<D, Int> {
            override fun addTo(node: ObjectNode, value: D) {
                node.put(name, extractor(value))
            }

            override fun extractFrom(node: JsonNode) =
                node.get(name).asInt()
        }

    data class Domain(
        val name: String,
        val count: Int
    )
}





