package com.oneeyedmen.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SchemaTests {

    private val objectMapper = ObjectMapper()

    private data class Parent(
        val aString: String,
        val thing: Domain
    )

    private data class Domain(
        val name: String,
        val count: Int
    )

    @Test
    fun `simple schema`() {
        val mapping = jsonMapping(
            ::Domain,
            prop("the-name", Domain::name),
            prop(Domain::count),
        )

        assertEquals("""
            {
                "type" : "object",
                "properties" : {
                    "the-name" : { "type" : "string" },
                    "count" : { "type" : "number" }
                }
            }""".toJson(objectMapper),
            mapping.schema(objectMapper.asNodeFactory())
        )
    }

    @Test
    fun `nested schema`() {
        val mapping = jsonMapping(
            ::Parent,
            prop(Parent::aString),
            prop(
                "child",
                Parent::thing,
                jsonMapping(
                    ::Domain,
                    prop("the-name", Domain::name),
                    prop(Domain::count),
                )
            ),
        )

        assertEquals("""
            {
                "type" : "object",
                "properties" : {
                    "aString" : { "type" : "string" },
                    "child" : {
                        "type" : "object",
                        "properties" : {
                            "the-name" : { "type" : "string" },
                            "count" : { "type" : "number" }
                        }
                    }
                }
            }""".toJson(objectMapper),
            mapping.schema(objectMapper.asNodeFactory())
        )
    }
}





