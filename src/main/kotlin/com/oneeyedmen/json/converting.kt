package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1

interface JsonConverter<D> {
    fun toJson(value: D, factory: ObjectMapper): JsonNode
    fun fromJson(node: JsonNode): D
}

interface JsonProperty<D, T> {
    fun addTo(node: ObjectNode, value: D, factory: ObjectMapper)
    fun extractFrom(node: JsonNode): T
}

fun <D, P1, P2> converter(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {
    override fun toJson(value: D, factory: ObjectMapper): JsonNode =
        factory.createObjectNode().apply {
            p1.addTo(this, value, factory)
            p2.addTo(this, value, factory)
        }

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.extractFrom(node),
            p2.extractFrom(node)
        )
}

fun <D> jsonString(property: KProperty1<D, String>) = jsonString(property.name, property::get)

fun <D> jsonString(name: String, extractor: (D) -> String) =
    jsonObject(name, extractor, JsonString)

fun <D> jsonInt(property: KProperty1<D, Int>) = jsonInt(property.name, property::get)

fun <D> jsonInt(name: String, extractor: (D) -> Int) =
    jsonObject(name, extractor, JsonInt)

fun <P, C> jsonObject(
    property: KProperty1<P, C>,
    converter: JsonConverter<C>
) = jsonObject(property.name, property::get, converter)

fun <P, C> jsonObject(
    name: String,
    extractor: (P) -> C,
    converter: JsonConverter<C>
) = object: JsonProperty<P, C> {
    override fun addTo(node: ObjectNode, value: P, factory: ObjectMapper) {
        node.set<JsonNode>(name, converter.toJson(extractor(value), factory))
    }

    override fun extractFrom(node: JsonNode) =
        converter.fromJson(node.get(name))
}

object JsonString : JsonConverter<String> {
    override fun toJson(value: String, factory: ObjectMapper): JsonNode =
        TextNode.valueOf(value)

    override fun fromJson(node: JsonNode): String =
        (node as TextNode).textValue()
}

object JsonInt : JsonConverter<Int> {
    override fun toJson(value: Int, factory: ObjectMapper): JsonNode =
        IntNode.valueOf(value)

    override fun fromJson(node: JsonNode): Int =
        (node as IntNode).intValue()
}
