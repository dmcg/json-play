package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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

        val converter = objectMapper.converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        assertEquals(expectedJson, converter(domain))
        assertEquals(domain, converter(expectedJson))
    }

    @Test
    fun `round trip sub-object`() {
        val domain = Composite(
            "banana",
            Domain("fred", 42)
        )
        val expectedJson = objectMapper.createObjectNode().apply {
            put("aString", "banana")
            put("thing",
                objectMapper.createObjectNode().apply {
                    put("the-name", "fred")
                    put("count", 42)
                }
            )
        }

        val converter1 = objectMapper.converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        val converter = objectMapper.converter(
            ::Composite,
            jsonString(Composite::aString),
            jsonObject("thing", Composite::thing, converter1),
        )
        assertEquals(expectedJson, converter(domain))
        assertEquals(domain, converter(expectedJson))
    }

    private fun <P, C> jsonObject(
        name: String,
        extractor: (P) -> C,
        converter: JsonConverter<C>
    ) = object: JsonProperty<P, C> {
        override fun addTo(node: ObjectNode, value: P) {
            node.put(name, converter(extractor(value)))
        }

        override fun extractFrom(node: JsonNode) =
            converter(node.get(name) as ObjectNode)
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




