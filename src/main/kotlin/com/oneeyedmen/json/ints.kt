package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1


@JvmName("propNameInt")
fun <D> prop(name: String, extractor: (D) -> Int) = intProp(name, extractor)

@JvmName("propInt")
fun <D> prop(property: KProperty1<D, Int>) = intProp(property.name, property)

fun <D> intProp(name: String, extractor: (D) -> Int) = prop(name, extractor, JsonInt)


object JsonInt : JsonConverter<Int> {

    override fun toJson(value: Int, factory: NodeFactory): JsonNode =
        IntNode.valueOf(value)

    override fun fromJson(node: JsonNode): Int =
        (node as IntNode).intValue()

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("number")
        )
}