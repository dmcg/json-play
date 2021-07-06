package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KProperty1

interface JsonConverter<D> {
    fun toJson(value: D): JsonNode
    fun fromJson(node: JsonNode): D
}

interface JsonProperty<D, T> {
    fun addTo(node: ObjectNode, value: D)
    fun extractFrom(node: JsonNode): T
}

fun <D, P1, P2> ObjectMapper.converter(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {
    override fun toJson(value: D): JsonNode =
        this@converter.createObjectNode().apply {
            p1.addTo(this, value)
            p2.addTo(this, value)
        }

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.extractFrom(node),
            p2.extractFrom(node)
        )
}

fun <D> jsonString(property: KProperty1<D, String>) = jsonString(property.name, property::get)

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

fun <P, C> jsonObject(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>
) = jsonObject(property.name, property::get, converter)

fun <P, C> jsonObject(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>
) = object: JsonProperty<P, C> {
    override fun addTo(node: ObjectNode, value: P) {
        node.set<JsonNode>(name, converter.toJson(extractor(value)))
    }

    override fun extractFrom(node: JsonNode) =
        converter.fromJson(node.get(name) as ObjectNode)
}