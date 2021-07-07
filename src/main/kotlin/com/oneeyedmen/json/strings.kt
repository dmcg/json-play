package com.oneeyedmen.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import kotlin.reflect.KProperty1


@JvmName("propNameString")
fun <D> prop(name: String, extractor: (D) -> String) = stringProp(name, extractor)

@JvmName("propString")
fun <D> prop(property: KProperty1<D, String>) = stringProp(property.name, property)
fun <D> prop(property: KProperty1<D, String?>) = stringProp(property.name, property)

@JvmName("propStringCollectionString")
inline fun <D, reified C: Collection<String>> prop(
    name: String,
    noinline extractor: (D) -> C,
) = prop(name, extractor, JsonString)

@JvmName("propStringCollection")
inline fun <D, reified C: Collection<String>> prop(
    property: KProperty1<D, C>,
) = prop(property.name, property)

fun <D> stringProp(name: String, extractor: (D) -> String) =
    prop(name, extractor, JsonString)

@JvmName("propNullableString")
fun <D> stringProp(name: String, extractor: (D) -> String?) =
    prop(name, extractor, NullableJsonString)


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

object NullableJsonString : JsonConverter<String?> {

    override fun toJson(value: String?, factory: NodeFactory): JsonNode =
        if (value == null) NullNode.instance else
        TextNode.valueOf(value)

    override fun fromJson(node: JsonNode): String? = when (node) {
        is TextNode -> node.asText()
        is NullNode -> null
        else -> error("$node is not text")
    }

    override fun schema(factory: NodeFactory): JsonNode =
        factory.objectNode(
            "type" to TextNode.valueOf("string")
        )
}