package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `converter can convert`() {
        val domain = Domain("fred", 42)
        val converter: (Domain) -> JDomain = createConverter(
            ::JDomain,
            Domain::name,
            Domain::count
        )
        assertEquals(JDomain("fred", 42), converter(domain))
    }

    @Test
    fun `to JsonNode`() {
        val domain = Domain("fred", 42)
        val expected: JsonNode = objectMapper.createObjectNode().apply {
            this.put("name", "fred")
            this.put("count", 42)
        }

        val putter1: (ObjectNode, Domain) -> Unit = { node, value -> node.put("name", value.name) }
        val putter2: (ObjectNode, Domain) -> Unit = { node, value -> node.put("count", value.count) }

        val putters = listOf(putter1, putter2)
        val converter = object {
            fun toJson(value: Domain): JsonNode = objectMapper.createObjectNode().apply {
                putters.forEach {
                    it(this, value)
                }
            }
        }
        assertEquals(expected, converter.toJson(domain))
    }


    data class Domain(
        val name: String,
        val count: Int
    )

    data class JDomain(
        val name: String,
        val count: Int
    )
}


fun <I, O, P1, P2> createConverter(
    constructor: (P1, P2) -> O,
    p1: (I) -> P1,
    p2: (I) -> P2
): (I) -> O = {
    constructor(p1(it), p2(it))
}


