package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class EnumTests {

    private val objectMapper = ObjectMapper()

    enum class Fruit {
        BANANA, KUMQUAT, PEAR
    }
    private data class Domain(
        val fruit: Fruit,
    )

    private val mapping = jsonMapping(
        ::Domain,
        prop(Domain::fruit),
    )

    @Test
    fun `round trip`() {
        val domain = Domain(Fruit.BANANA)
        val expectedJson = """
        {
            "fruit" : "BANANA"
        }""".toJson(objectMapper)
        assertEquals(
            expectedJson,
            mapping.toJson(domain, objectMapper.asNodeFactory())
        )
        assertEquals(domain, mapping.fromJson(expectedJson))
    }

    @Test
    fun `not found`() {
        val json = """
        {
            "fruit" : "TOMATO"
        }""".toJson(objectMapper)
        assertThrows<RuntimeException> {
            mapping.fromJson(json)
        }
    }

    @Test
    fun schema() {
        assertEquals("""
            {
                "type" : "object",
                "properties" : {
                    "fruit" : {
                        "type" : "string",
                        "emum" : [
                            "BANANA", "KUMQUAT", "PEAR"
                        ]
                    }
                }
            }""".toJson(objectMapper),
            mapping.schema(objectMapper.asNodeFactory())
        )
    }
}