package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Tests {

    val objectMapper = ObjectMapper()

    @Test
    fun `round trip`() {
        val domain = Domain("fred", 42)
        val expectedJson = objectMapper.createObjectNode().apply {
            this.put("the-name", "fred")
            this.put("count", 42)
        }

        val converter = objectMapper.converter(
            ::Domain,
            jsonString("the-name", Domain::name),
            jsonInt(Domain::count),
        )
        assertEquals(expectedJson, converter(domain))
        assertEquals(domain, converter(expectedJson))
    }
}

data class Domain(
    val name: String,
    val count: Int
) {
    constructor(): this("fred", 42)
}




