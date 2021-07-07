package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1


@JvmName("propNameString")
fun <D> prop(name: String, extractor: (D) -> String) = stringProp(name, extractor)

@JvmName("propString")
fun <D> prop(property: KProperty1<D, String>) = stringProp(property.name, property)

@JvmName("propStringCollectionString")
inline fun <D, reified C: Collection<String>> prop(
    name: String,
    noinline extractor: (D) -> C,
) = prop(name, extractor, JsonString)

@JvmName("propStringCollection")
inline fun <D, E, reified C: Collection<String>> prop(
    property: KProperty1<D, C>,
) = prop(property.name, property)

fun <D> stringProp(name: String, extractor: (D) -> String) =
    prop(name, extractor, JsonString)


object JsonString : JsonConverter<String> {

    override fun toJson(value: String, factory: NodeFactory): JsonNode =
        TextNode.valueOf(value)

    override fun fromJson(node: JsonNode): String =
        (node as TextNode).textValue()

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("string")
        )
}