package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NullableTests {

    private val objectMapper = ObjectMapper()

    data class Domain(
        val name: String?
    )
    private val mapping = jsonMapping(
        ::Domain,
        prop(Domain::name)
    )


    @Test
    fun `null round trip`() {
        val expectedJson = """
        {
            "name" : null
        }""".toJson(objectMapper)
        val domain = Domain(null)

        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `not null round trip`() {
        val expectedJson = """
        {
            "name" : "banana"
        }""".toJson(objectMapper)
        val domain = Domain("banana")

        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }
}