package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1

interface JsonConverter<D> {
    fun toJson(value: D, factory: ObjectMapper): JsonNode
    fun fromJson(node: JsonNode): D
}

interface JsonProperty<P, C> {
    val name: String
    fun toJson(value: P, factory: ObjectMapper): JsonNode
    fun fromJson(node: JsonNode): C
}

fun <D, P1, P2> converter(
    ctor: (P1, P2) -> D,
    p1: JsonProperty<D, P1>,
    p2: JsonProperty<D, P2>
) = object: JsonConverter<D> {
    override fun toJson(value: D, factory: ObjectMapper): JsonNode =
        factory.createObjectNode().apply {
            set<JsonNode>(p1.name, p1.toJson(value, factory))
            set<JsonNode>(p2.name, p2.toJson(value, factory))
        }

    override fun fromJson(node: JsonNode): D =
        ctor(
            p1.fromJson(node.get(p1.name)),
            p2.fromJson(node.get(p2.name)),
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
    override val name get() = name
    override fun toJson(value: P, factory: ObjectMapper): JsonNode =
        converter.toJson(extractor(value), factory)
    override fun fromJson(node: JsonNode): C = converter.fromJson(node)
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
